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

import javax.persistence.PersistenceException;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.UserType;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.server.annotations.RequestHandlerMethod;


public class RequestHandler extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private User_ loggedUser;


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
			String receivedAuthToken = reqMsg.getAuthToken();
			if (!requiresLogin)
				return (ResponseMessage)handler.invoke(this, reqMsg);
			if (receivedAuthToken == null)
				return new ResponseMessage("NO-AUTH: user not authenticated.");
			authToken = (new AuthTokenManager()).find(receivedAuthToken).next();
			if (authToken == null)
				return new ResponseMessage("NO-AUTH: can not find auth token.");
			if (requiresAdmin && !authToken.isAdmin())
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

	
}
