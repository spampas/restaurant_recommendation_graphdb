package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.entities.enums.UserType;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.server.storage.EntityManager;
import ristogo.server.storage.ReservationManager;
import ristogo.server.storage.RestaurantManager;
import ristogo.server.storage.UserManager;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

public class Client extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private User_ loggedUser;
	private UserManager userManager;
	private RestaurantManager restaurantManager;
	private ReservationManager reservationManager;

	Client(Socket clientSocket)
	{
		Logger.getLogger(Client.class.getName()).info("New incoming connection from " +
			clientSocket.getRemoteSocketAddress() + "." +
			"Request handled by " + this.getName() + ".");
		socket = clientSocket;
		userManager = new UserManager();
		restaurantManager = new RestaurantManager();
		reservationManager = new ReservationManager();
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
			process();
		Logger.getLogger(Client.class.getName()).warning(getName() + ": interrupted. Exiting...");
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void process()
	{
		RequestMessage reqMsg = (RequestMessage)Message.receive(inputStream);
		if (reqMsg == null) {
			Logger.getLogger(Client.class.getName()).warning(getName() + ": failure in receiving message. Client probably terminated.");
			Thread.currentThread().interrupt();
			return;
		}
		if (!reqMsg.isValid()) {
			Logger.getLogger(Client.class.getName()).warning(getName() +
				": received an invalid request" +
				(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		Logger.getLogger(Client.class.getName()).info(getName() +
			": received " + reqMsg.getAction() + " request." +
			(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");

		ResponseMessage resMsg = null;
		switch(reqMsg.getAction()) {
		case LOGIN:
		case REGISTER:
			if (loggedUser != null)
				resMsg = new ResponseMessage("You are already logged in.");
			break;
		case LOGOUT:
			if (loggedUser == null)
				resMsg = new ResponseMessage("You are not logged in.");
			break;
		default:
			if (loggedUser == null)
				resMsg = new ResponseMessage("You must be logged in to perform this action.");
		}

		if (resMsg == null)
			switch (reqMsg.getAction()) {
			case LOGIN:
				resMsg = handleLogin(reqMsg);
				break;
			case LOGOUT:
				resMsg = handleLogout(reqMsg);
				break;
			case REGISTER:
				resMsg = handleRegister(reqMsg);
				break;
			case GET_OWN_RESTAURANT:
				resMsg = handleGetOwnRestaurant(reqMsg);
				break;
			case EDIT_RESTAURANT:
				resMsg = handleEditRestaurant(reqMsg);
				break;
			case LIST_OWN_RESERVATIONS:
				resMsg = handleListOwnReservations(reqMsg);
				break;
			case EDIT_RESERVATION:
				resMsg = handleEditReservation(reqMsg);
				break;
			case LIST_RESTAURANTS:
				resMsg = handleListRestaurants(reqMsg);
				break;
			case RESERVE:
				resMsg = handleReserve(reqMsg);
				break;
			case DELETE_RESERVATION:
				resMsg = handleDeleteReservation(reqMsg);
				break;
			case DELETE_RESTAURANT:
				resMsg = handleDeleteRestaurant(reqMsg);
				break;
			case LIST_RESERVATIONS:
				resMsg = handleListReservations(reqMsg);
				break;
			case CHECK_SEATS:
				resMsg = handleCheckSeats(reqMsg);
				break;
			default:
				resMsg = new ResponseMessage("Invalid request.");
			}

		Logger.getLogger(Client.class.getName()).info(getName() +
			": sending response." +
			(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
		resMsg.send(outputStream);
	}

	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		if (!user.hasValidUsername() || !user.hasValidPassword())
			return new ResponseMessage("Invalid username or password.");
		User_ savedUser = userManager.getUserByUsername(user.getUsername());
		if (savedUser == null || !user.checkPasswordHash(savedUser.getPassword()))
			return new ResponseMessage("Invalid username or password.");
		loggedUser = savedUser;
		return new ResponseMessage(loggedUser.toCommonEntity((restaurantManager.getRestaurantByOwner(loggedUser) == null) ? UserType.CUSTOMER : UserType.OWNER));
	}

	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		loggedUser = null;
		return new ResponseMessage();
	}

	private ResponseMessage handleRegister(RequestMessage reqMsg)
	{
		User user = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof User)
				user = (User)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		if (!user.hasValidUsername())
			return new ResponseMessage("Username must be at least 3 characters long and no more than 32 characters long.");
		if (!user.hasValidPassword())
			new ResponseMessage("Invalid password.");
		User_ savedUser = new User_();
		try {
			EntityManager.beginTransaction();
			savedUser.merge(user);
			userManager.persist(savedUser);
			if (restaurant != null) {
				Restaurant_ savedRestaurant = new Restaurant_();
				if (!savedRestaurant.merge(restaurant)) {
					EntityManager.rollbackTransaction();
					return new ResponseMessage("Some restaurant's fields are invalid.");
				}
				savedRestaurant.setOwner(savedUser);
				restaurantManager.persist(savedRestaurant);
			}
			EntityManager.commitTransaction();
		} catch (PersistenceException ex) {
			return new ResponseMessage("Username already in use.");
		}
		userManager.refresh(savedUser);
		return new ResponseMessage(savedUser.toCommonEntity((restaurant == null) ? UserType.CUSTOMER : UserType.OWNER));
	}

	private ResponseMessage handleGetOwnRestaurant(RequestMessage reqMsg)
	{
		Restaurant_ restaurant = restaurantManager.getRestaurantByOwner(loggedUser);
		if (restaurant == null)
			return new ResponseMessage("You do not have any restaurant.");
		return new ResponseMessage(restaurant.toCommonEntity());
	}

	private boolean hasRestaurant(User_ user, int restaurantId)
	{
		Restaurant_ restaurant = restaurantManager.getRestaurantByOwner(user);
		if (restaurant == null)
			return false;
		return restaurant.getOwner().getId() == user.getId();
	}

	private boolean hasReservation(User_ user, int reservationId)
	{
		List<Reservation_> reservations = reservationManager.getActiveReservations(user);
		for (Reservation_ reservation: reservations)
			if (reservation.getId() == reservationId)
				return true;
		return false;
	}

	private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		if (!restaurant_.merge(restaurant))
			return new ResponseMessage("Some restaurant's fields are invalid.");
		restaurant_.setOwner(loggedUser);
		try {
			restaurantManager.update(restaurant_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while saving the restaurant to the database.");
		}
		restaurantManager.refresh(restaurant_);
		return new ResponseMessage(restaurant_.toCommonEntity());
	}

	private ResponseMessage handleListOwnReservations(RequestMessage reqMsg)
	{
		ResponseMessage resMsg = new ResponseMessage();
		List<Reservation_> reservations = reservationManager.getActiveReservations(loggedUser);
		for (Reservation_ reservation: reservations)
			resMsg.addEntity(reservation.toCommonEntity());
		return resMsg;
	}

	private ResponseMessage handleEditReservation(RequestMessage reqMsg)
	{
		Reservation reservation = (Reservation)reqMsg.getEntity();
		if (reservation.getDate().isBefore(LocalDate.now()))
			return new ResponseMessage("The reservation date must be a date in future.");
		if (!hasReservation(loggedUser, reservation.getId()))
			return new ResponseMessage("You can only edit your own reservations.");
		Reservation_ reservation_ = reservationManager.get(reservation.getId());
		Restaurant_ restaurant_ = reservation_.getRestaurant();
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		if (rt == null)
			return new ResponseMessage("Invalid reservation time.");
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for " + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		if (!reservation.isActive())
			return new ResponseMessage("Invalid date.");
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), rt);
		if (reservations != null)
			for (Reservation_ r: reservations)
				if (r.getId() != reservation.getId())
					availSeats -= r.getSeats();
		if (reservation.getSeats() > availSeats)
			return new ResponseMessage("Not enough seats for this date and time (available seats: " + availSeats + ").");
		if (!reservation_.merge(reservation))
			return new ResponseMessage("Some reservation's fields are invalid.");
		reservation_.setUser(loggedUser);
		reservation_.setRestaurant(restaurant_);
		try {
			reservationManager.update(reservation_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("You already have a reservation for " + reservation.getDate() + " at " + rt + ".");
		}
		reservationManager.refresh(reservation_);
		return new ResponseMessage(reservation_.toCommonEntity());
	}

	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		List<Restaurant_> restaurants;
		if (reqMsg.getEntityCount() > 0) {
			Restaurant restaurant = (Restaurant)reqMsg.getEntity();
			restaurants = restaurantManager.getRestaurantsByCity(restaurant.getCity());
		} else {
			restaurants = restaurantManager.getAll();
		}
		ResponseMessage resMsg = new ResponseMessage();
		for (Restaurant_ restaurant: restaurants)
			resMsg.addEntity(restaurant.toCommonEntity());
		return resMsg;
	}

	private ResponseMessage handleReserve(RequestMessage reqMsg)
	{
		Reservation reservation = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof Reservation)
				reservation = (Reservation)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		if (reservation.getDate().isBefore(LocalDate.now()))
			return new ResponseMessage("The reservation date must be a date in future.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		if (rt == null)
			return new ResponseMessage("Invalid reservation time.");
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for " + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		if (!reservation.isActive())
			return new ResponseMessage("Invalid date.");
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), rt);
		if (reservations != null)
			for (Reservation_ r: reservations)
				availSeats -= r.getSeats();
		if (reservation.getSeats() > availSeats)
			return new ResponseMessage("Not enough seats for this date and time (available seats: " + availSeats + ").");
		Reservation_ reservation_ = new Reservation_();
		if (!reservation_.merge(reservation))
			return new ResponseMessage("Some reservation's fields are invalid.");
		reservation_.setRestaurant(restaurant_);
		reservation_.setUser(loggedUser);
		try {
			reservationManager.insert(reservation_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("You already have a reservation for " + reservation.getDate() + " at " + rt + ".");
		}
		return new ResponseMessage(reservation_.toCommonEntity());
	}

	private ResponseMessage handleDeleteReservation(RequestMessage reqMsg)
	{
		Reservation reservation = (Reservation)reqMsg.getEntity();
		if (!hasReservation(loggedUser, reservation.getId()))
			return new ResponseMessage("You can only delete your own reservations.");
		Reservation_ reservation_ = reservationManager.get(reservation.getId());
		if (reservation_ == null)
			return new ResponseMessage("Can not find the specified reservation.");
		try {
			reservationManager.delete(reservation_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while deleting the reservation from the database.");
		}
		return new ResponseMessage();
	}

	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only delete restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		if (restaurant_ == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		try {
			restaurantManager.delete(restaurant.getId());
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while deleting the restaurant from the database.");
		}
		userManager.refresh(restaurant_.getOwner());
		return new ResponseMessage();
	}

	private ResponseMessage handleListReservations(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only view reservations for restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		restaurantManager.refresh(restaurant_);
		if (restaurant_ == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		List<Reservation_> reservations = reservationManager.getActiveReservations(restaurant_);
		ResponseMessage resMsg = new ResponseMessage();
		for (Reservation_ reservation: reservations)
			resMsg.addEntity(reservation.toCommonEntity());
		return resMsg;
	}

	private ResponseMessage handleCheckSeats(RequestMessage reqMsg)
	{
		Reservation reservation = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof Reservation)
				reservation = (Reservation)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		Restaurant_ restaurant_ = null;
		if (restaurant == null) {
			Reservation_ reservation_ = reservationManager.get(reservation.getId());
			if (reservation_ != null)
				restaurant_ = reservation_.getRestaurant();
		} else {
			restaurant_ = restaurantManager.get(restaurant.getId());
		}
		if (restaurant_ == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		if (rt == null)
			return new ResponseMessage("Invalid reservation time.");
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for " + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		if (!reservation.isActive())
			return new ResponseMessage("Invalid date.");
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), rt);
		if (reservations != null)
			for (Reservation_ r: reservations)
				if (r.getId() != reservation.getId())
					availSeats -= r.getSeats();
		Restaurant toReturn = restaurant_.toCommonEntity();
		toReturn.setSeats(availSeats);
		return new ResponseMessage(toReturn);
	}
}
