package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.UserType;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.db.DBManager;
import ristogo.server.annotations.RequestHandlerMethod;


public class RequestHandler extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private User loggedUser;


	RequestHandler(Socket clientSocket)
	{
		
		socket = clientSocket;

		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
			process();
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void process()
	{
		RequestMessage reqMsg = (RequestMessage)Message.receive(inputStream);
		if (reqMsg == null) {
			Thread.currentThread().interrupt();
			return;
		}
		if (!reqMsg.isValid()) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		
		dispatchMessage(reqMsg).send(outputStream);
	}
	
	private ResponseMessage dispatchMessage(RequestMessage reqMsg)
	{
		String handlerName = "handle" + reqMsg.getAction().toCamelCaseString();
		try {
			Method handler = getClass().getDeclaredMethod(handlerName, RequestMessage.class);
			handler.setAccessible(true);
			if (!handler.isAnnotationPresent(RequestHandlerMethod.class))
				throw new NoSuchMethodException();
			boolean requiresAdmin = handler.getAnnotation(RequestHandlerMethod.class).value();
			boolean requiresLogin = requiresAdmin ? true : handler.getAnnotation(RequestHandlerMethod.class).requiresLogin();
			if (!requiresLogin)
				return (ResponseMessage)handler.invoke(this, reqMsg);
			if (requiresAdmin)
				return new ResponseMessage("This action requires admin privileges.");
			return (ResponseMessage)handler.invoke(this, reqMsg);
		} catch (NoSuchMethodException e) {
			return new ResponseMessage("Invalid action.");
		} catch (InvocationTargetException e) {
			return new ResponseMessage("ERROR: " + e.getCause().getMessage());
		} catch (IllegalAccessException | IllegalArgumentException
			| SecurityException e) {
			e.printStackTrace();
			return new ResponseMessage("Can not run action handler.");
		}
	}

	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		if (!user.hasValidUsername() || !user.hasValidPassword())
			return new ResponseMessage("Invalid username or password.");
		User savedUser = DBManager.getUserByUsername(user.getUsername());
		if (savedUser == null || !user.checkPasswordHash(savedUser.getPasswordHash()))
			return new ResponseMessage("Invalid username or password.");
		loggedUser = savedUser;
		return new ResponseMessage(loggedUser.toCommonEntity((DBManager.getRestaurantByOwner(loggedUser) == null) ? UserType.CUSTOMER : UserType.OWNER));
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
		User savedUser = new User();
		try {
			DBManager.beginTransaction();
			savedUser.merge(user);
			DBManager.persist(savedUser);
			if (restaurant != null) {
				Restaurant savedRestaurant = new Restaurant();
				if (!savedRestaurant.merge(restaurant)) {
					DBManager.rollbackTransaction();
					return new ResponseMessage("Some restaurant's fields are invalid.");
				}
				savedRestaurant.setOwner(savedUser);
				DBManager.persist(savedRestaurant);
			}
			DBManager.commitTransaction();
		} catch (PersistenceException ex) {
			return new ResponseMessage("Username already in use.");
		}
		DBManager.refresh(savedUser);
		return new ResponseMessage(savedUser.toCommonEntity((restaurant == null) ? UserType.CUSTOMER : UserType.OWNER));
	}

	private ResponseMessage handleGetOwnRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = DBManager.getRestaurantByOwner(loggedUser);
		if (restaurant == null)
			return new ResponseMessage("You do not have any restaurant.");
		return new ResponseMessage(restaurant.toCommonEntity());
	}

	private boolean hasRestaurant(User user, int restaurantId)
	{
		Restaurant restaurant = DBManager.getRestaurantByOwner(user);
		if (restaurant == null)
			return false;
		return restaurant.getOwner().getId() == user.getId();
	}


	private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		Restaurant restaurant_ = DBManager.get(restaurant.getId());
		if (!restaurant_.merge(restaurant))
			return new ResponseMessage("Some restaurant's fields are invalid.");
		restaurant_.setOwner(loggedUser);
		try {
			DBManager.update(restaurant_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while saving the restaurant to the database.");
		}
		DBManager.refresh(restaurant_);
		return new ResponseMessage(restaurant_.toCommonEntity());
	}
	

	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		List<Restaurant> restaurants;
		if (reqMsg.getEntityCount() > 0) {
			Restaurant restaurant = (Restaurant)reqMsg.getEntity();
			restaurants = DBManager.getRestaurantsByCity(restaurant.getCity());
		} else {
			restaurants = DBManager.getAll();
		}
		ResponseMessage resMsg = new ResponseMessage();
		for (Restaurant restaurant: restaurants)
			resMsg.addEntity(restaurant.toCommonEntity());
		return resMsg;
	}


	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only delete restaurants that you own.");
		Restaurant restaurant_ = DBManager.get(restaurant.getId());
		if (restaurant_ == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		try {
			DBManager.delete(restaurant.getId());
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while deleting the restaurant from the database.");
		}
		DBManager.refresh(restaurant_.getOwner());
		return new ResponseMessage();
	}

	
}
