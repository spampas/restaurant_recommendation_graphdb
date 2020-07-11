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
		RequestMessage reqMsg = RequestMessage.receive(inputStream);
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
			boolean requiresOwnership = handler.getAnnotation(RequestHandlerMethod.class).requiresOwnership();
			boolean requiresLogin = requiresAdmin || requiresOwnership ? true : handler.getAnnotation(RequestHandlerMethod.class).requiresLogin();
			if (requiresLogin && loggedUser == null)
				return new ResponseMessage("Thi action requires authentication.");
			if (requiresAdmin && !loggedUser.isAdmin())
				return new ResponseMessage("This action requires admin privileges.");
			if (requiresOwnership && !loggedUser.isOwner() && !loggedUser.isAdmin())
				return new ResponseMessage("This action is reserved to restaurants' owners.");
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

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		if (!user.hasValidUsername() || !user.hasValidPassword())
			return new ResponseMessage("Invalid username or password.");
		User savedUser = DBManager.getUserByUsername(user.getUsername());
		if (savedUser == null || !user.checkPasswordHash(savedUser.getPasswordHash()))
			return new ResponseMessage("Invalid username or password.");
		loggedUser = savedUser;
		return new ResponseMessage(loggedUser);
	}

	@RequestHandlerMethod
	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		loggedUser = null;
		return new ResponseMessage();
	}

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleRegister(RequestMessage reqMsg)
	{
		User user = reqMsg.getEntity(User.class);
		Restaurant restaurant = reqMsg.getEntity(Restaurant.class);
		if (!user.hasValidUsername())
			return new ResponseMessage("Username must be at least 3 characters long and no more than 32 characters long.");
		if (!user.hasValidPassword())
			new ResponseMessage("Invalid password.");
		User savedUser = null;
		/*try {
			DBManager.addUser(tx, user)
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
		}*/
		return new ResponseMessage(savedUser);
	}

	@RequestHandlerMethod(requiresOwnership=true)
	private ResponseMessage handleGetOwnRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = DBManager.getRestaurantByOwner(loggedUser);
		if (restaurant == null)
			return new ResponseMessage("You do not have any restaurant.");
		return new ResponseMessage(restaurant);
	}

	private boolean hasRestaurant(User user, int restaurantId)
	{
		Restaurant restaurant = DBManager.getRestaurantByOwner(user);
		if (restaurant == null)
			return false;
		return restaurant.getOwner().getId() == user.getId();
	}

	@RequestHandlerMethod(requiresOwnership=true)
	private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = reqMsg.getEntity(Restaurant.class);
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		Restaurant savedRestaurant = DBManager.getRestaurant(restaurant.getId());
		/*if (!restaurant_.merge(restaurant))
			return new ResponseMessage("Some restaurant's fields are invalid.");*/
		/*savedRestaurant.setOwner(loggedUser);
		try {
			DBManager.update(savedRestaurant);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while saving the restaurant to the database.");
		}*/
		return new ResponseMessage(savedRestaurant);
	}

	@RequestHandlerMethod
	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		List<Restaurant> restaurants;
		if (reqMsg.getEntityCount() > 0) {
			Restaurant restaurant = reqMsg.getEntity(Restaurant.class);
			restaurants = DBManager.getRestaurantsByCity(restaurant.getCity());
		} else {
			restaurants = DBManager.getAllRestaurants();
		}
		ResponseMessage resMsg = new ResponseMessage();
		for (Restaurant restaurant: restaurants)
			resMsg.addEntity(restaurant);
		return resMsg;
	}


	@RequestHandlerMethod(requiresOwnership=true)
	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		Restaurant restaurant = reqMsg.getEntity(Restaurant.class);
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only delete restaurants that you own.");
		Restaurant savedRestaurant = DBManager.getRestaurant(restaurant.getId());
		if (savedRestaurant == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		/*try {
			DBManager.deleteRestaurant(restaurant.getId());
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while deleting the restaurant from the database.");
		}*/
		return new ResponseMessage();
	}

	
}
