package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.cypher.query.SortOrder;

import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.enums.ActionRequest;
import ristogo.server.annotations.RequestHandlerMethod;
import ristogo.server.db.DBManager;
import ristogo.server.db.entities.City;
import ristogo.server.db.entities.Cuisine;
import ristogo.server.db.entities.Restaurant;
import ristogo.server.db.entities.User;

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
		DBManager.getInstance().close();
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

		ResponseMessage resMsg = dispatchMessage(reqMsg);
		if (resMsg == null)
			new ResponseMessage("Request handler returned NULL (Action: " + reqMsg.getAction().toString() + ").").send(outputStream);
		else
			resMsg.send(outputStream);
		if (reqMsg.getAction() == ActionRequest.LOGOUT)
			Thread.currentThread().interrupt();
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
			if (requiresLogin && loggedUser == null)
				return new ResponseMessage("This action requires authentication.");
			if (requiresAdmin && !loggedUser.isAdmin())
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

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		UserInfo user = (UserInfo)reqMsg.getEntity();
		try {
			User.validateUsername(user.getUsername());
			User.validatePassword(user.getPassword());
		} catch (IllegalArgumentException ex) {
			return new ResponseMessage(ex.getMessage());
		}
		User savedUser = DBManager.session().load(User.class, user.getUsername(), 0);
		if (savedUser == null || !savedUser.checkPassword(user.getPassword()))
			return new ResponseMessage("Invalid username or password.");
		loggedUser = savedUser;
		return new ResponseMessage(new UserInfo(savedUser.getUsername(), savedUser.isAdmin()));
	}

	@RequestHandlerMethod
	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		loggedUser = null;
		return new ResponseMessage();
	}

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleRegisterUser(RequestMessage reqMsg)
	{
		UserInfo user = reqMsg.getEntity(UserInfo.class);
		City city = DBManager.session().load(City.class, user.getCity().getName(), 0);
		if (city == null)
			return new ResponseMessage("Invalid city.");
		try {
			User savedUser = new User(user.getUsername(), user.getPassword(), city);
			DBManager.session().save(savedUser);
			loggedUser = savedUser;
			return new ResponseMessage(new UserInfo(savedUser.getUsername(), savedUser.isAdmin()));
		} catch (IllegalArgumentException ex) {
			return new ResponseMessage(ex.getMessage());
		}
	}

	@RequestHandlerMethod
	private ResponseMessage handleListOwnRestaurants(RequestMessage reqMsg)
	{
		List<Restaurant> savedRestaurants = loggedUser.getOwnedRestaurants();
		List<RestaurantInfo> restaurants = new ArrayList<RestaurantInfo>();
		for (Restaurant restaurant: savedRestaurants)
			restaurants.add(new RestaurantInfo(restaurant.getName(),
				new CuisineInfo(restaurant.getCuisine().getName()),
				restaurant.getPrice(),
				new CityInfo(restaurant.getCity().getName())));
		return new ResponseMessage(restaurants.toArray(new RestaurantInfo[0]));
	}

	/*private boolean hasRestaurant(UserInfo user, int restaurantId)
	{
		RestaurantInfo restaurant = DBManager.getRestaurantByOwner(user);
		if (restaurant == null)
			return false;
		return restaurant.getOwner().getId() == user.getId();
	}

	@RequestHandlerMethod(requiresOwnership=true)
	private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		RestaurantInfo savedRestaurant = DBManager.getRestaurant(restaurant.getId());
		if (!restaurant_.merge(restaurant))
			return new ResponseMessage("Some restaurant's fields are invalid.");
		savedRestaurant.setOwner(loggedUser);
		try {
			DBManager.update(savedRestaurant);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while saving the restaurant to the database.");
		}
		return new ResponseMessage(savedRestaurant);
	}

	@RequestHandlerMethod
	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		List<RestaurantInfo> restaurants;
		if (reqMsg.getEntityCount() > 0) {
			RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
			restaurants = DBManager.getRestaurantsByCity(restaurant.getCity());
		} else {
			restaurants = DBManager.getAllRestaurants();
		}
		ResponseMessage resMsg = new ResponseMessage();
		for (RestaurantInfo restaurant: restaurants)
			resMsg.addEntity(restaurant);
		return resMsg;
	}


	@RequestHandlerMethod(requiresOwnership=true)
	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		if (!hasRestaurant(loggedUser, restaurant.getId()))
			return new ResponseMessage("You can only delete restaurants that you own.");
		RestaurantInfo savedRestaurant = DBManager.getRestaurant(restaurant.getId());
		if (savedRestaurant == null)
			return new ResponseMessage("Can not find the specified restaurant.");
		try {
			DBManager.deleteRestaurant(restaurant.getId());
		} catch (PersistenceException ex) {
			return new ResponseMessage("Error while deleting the restaurant from the database.");
		}
		return new ResponseMessage();
	}*/

	@RequestHandlerMethod
	private ResponseMessage handleAddRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		City city = DBManager.session().load(City.class, restaurant.getCity().getName(), 0);
		if (city == null)
			return new ResponseMessage("The specified city can not be found.");
		Cuisine cuisine = DBManager.session().load(Cuisine.class, restaurant.getCuisine().getName(), 0);
		if (cuisine == null)
			return new ResponseMessage("The specified cuisine can not be found.");
		Restaurant savedRestaurant = new Restaurant(restaurant.getName(), loggedUser, restaurant.getPrice(), cuisine, city, restaurant.getDescription());
		DBManager.session().save(savedRestaurant);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleRestaurantDetails(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String name = filter == null ? null : filter.getValue();
		if (name == null)
			return new ResponseMessage("Invalid restaurant specified.");
		Restaurant restaurant = DBManager.session().load(Restaurant.class, name);
		if (restaurant == null)
			return new ResponseMessage("The specified restaurant can not be found.");

		return new ResponseMessage(new RestaurantInfo(restaurant.getName(),
			new UserInfo(restaurant.getOwner().getUsername()),
			new CuisineInfo(restaurant.getCuisine().getName()),
			restaurant.getPrice(),
			new CityInfo(restaurant.getCity().getName()),
			restaurant.getDescription(),
			restaurant.isLikedBy(loggedUser)));
	}

	@RequestHandlerMethod
	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<Restaurant> restaurants;
		if (filter == null)
			restaurants = DBManager.session().loadAll(Restaurant.class,
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 1);
		else
			restaurants = DBManager.session().loadAll(Restaurant.class,
				new Filter("name", ComparisonOperator.MATCHES, regex),
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 1);
		List<RestaurantInfo> infos = new ArrayList<RestaurantInfo>();
		restaurants.forEach((Restaurant r) -> {
			infos.add(new RestaurantInfo(
				r.getName(),
				new UserInfo(r.getOwner().getUsername()),
				new CuisineInfo(r.getCuisine().getName()),
				r.getPrice(),
				new CityInfo(r.getCity().getName()),
				r.getDescription(),
				r.isLikedBy(loggedUser)
				));
		});
		return new ResponseMessage(infos.toArray(new RestaurantInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleListLikedRestaurants(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		List<Restaurant> restaurants = Restaurant.loadRestaurantsLikedBy(
			loggedUser,
			regex,
			pageFilter.getPage(), pageFilter.getPerPage()
			);
		List<RestaurantInfo> infos = new ArrayList<RestaurantInfo>();
		restaurants.forEach((Restaurant r) -> {
			infos.add(new RestaurantInfo(
				r.getName(),
				new UserInfo(r.getOwner().getUsername()),
				new CuisineInfo(r.getCuisine().getName()),
				r.getPrice(),
				new CityInfo(r.getCity().getName()),
				r.getDescription(),
				true
				));
		});
		return new ResponseMessage(infos.toArray(new RestaurantInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String name = filter == null ? null : filter.getValue();
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		if (name == null)
			return new ResponseMessage("Invalid restaurant specified.");
		Restaurant savedRestaurant = DBManager.session().load(Restaurant.class, name);
		if (savedRestaurant == null)
			return new ResponseMessage("The specified restaurant can not be found.");
		if (!savedRestaurant.getOwner().equals(loggedUser))
			return new ResponseMessage("You can edit only restaurants that you own.");
		City city = DBManager.session().load(City.class, restaurant.getCity().getName(), 0);
		if (city == null)
			return new ResponseMessage("Can not find the specified city.");
		Cuisine cuisine = DBManager.session().load(Cuisine.class, restaurant.getCuisine().getName(), 0);
		if (cuisine == null)
			return new ResponseMessage("Can not find the specified cuisine.");
		savedRestaurant.setName(restaurant.getName());
		savedRestaurant.setCity(city);
		savedRestaurant.setCuisine(cuisine);
		savedRestaurant.setPrice(restaurant.getPrice());
		savedRestaurant.setDescription(restaurant.getDescription());
		DBManager.session().save(savedRestaurant);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String name = filter == null ? null : filter.getValue();
		if (name == null)
			return new ResponseMessage("Invalid restaurant specified.");
		Restaurant savedRestaurant = DBManager.session().load(Restaurant.class, name);
		if (savedRestaurant == null)
			return new ResponseMessage("The specified restaurant can not be found.");
		if (!loggedUser.isAdmin() && !savedRestaurant.getOwner().equals(loggedUser))
			return new ResponseMessage("You can edit only restaurants that you own.");
		DBManager.session().delete(savedRestaurant);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleListUsers(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<User> users;
		if (regex == null)
			users = DBManager.session().loadAll(User.class,
				new SortOrder().add("username"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 0);
		else
			users = DBManager.session().loadAll(User.class,
				new Filter("username", ComparisonOperator.MATCHES, regex),
				new SortOrder().add("username"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 0);
		List<UserInfo> infos = new ArrayList<UserInfo>();
		users.forEach((User u) -> {
			if(!u.equals(loggedUser))	
				infos.add(new UserInfo(u.getUsername(), new CityInfo(u.getCity().getName()), u.isFollowedBy(loggedUser)));
		});
		return new ResponseMessage(infos.toArray(new UserInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleListFollowers(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<User> users;
		if (regex == null)
			users = loggedUser.getFollowingUsers();
		else
			users = loggedUser.getFollowingUsers(regex);
		List<UserInfo> infos = new ArrayList<UserInfo>();
		users.forEach((User u) -> {
			infos.add(new UserInfo(u.getUsername(), new CityInfo(u.getCity().getName()), true));
		});
		return new ResponseMessage(infos.toArray(new UserInfo[0]));
	}
	
	@RequestHandlerMethod
	private ResponseMessage handleListFollowing(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<User> users;
		if (regex == null)
			users = loggedUser.getFollowedUsers();
		else
			users = loggedUser.getFollowedUsers(regex);
		List<UserInfo> infos = new ArrayList<UserInfo>();
		users.forEach((User u) -> {
			infos.add(new UserInfo(u.getUsername(), new CityInfo(u.getCity().getName()), true));
		});
		return new ResponseMessage(infos.toArray(new UserInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleFollowUser(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String username = filter == null ? null : filter.getValue();
		if (username == null || !User.isValidUsername(username))
			return new ResponseMessage("Invalid user.");
		User user = DBManager.session().load(User.class, username, 0);
		if (user == null)
			return new ResponseMessage("Can not find the specified user.");
		if (user.equals(loggedUser))
			return new ResponseMessage("You can not follow yourself.");
		if (loggedUser.isFollowing(user))
			return new ResponseMessage("You already follow this user.");
		loggedUser.follow(user);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}
	
	

	@RequestHandlerMethod
	private ResponseMessage handleUnfollowUser(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String username = filter == null ? null : filter.getValue();
		if (username == null || !User.isValidUsername(username))
			return new ResponseMessage("Invalid user.");
		User user = DBManager.session().load(User.class, username, 0);
		if (user == null)
			return new ResponseMessage("Can not find the specified user.");
		if (user.equals(loggedUser))
			return new ResponseMessage("You can not unfollow yourself.");
		if (!loggedUser.isFollowing(user))
			return new ResponseMessage("You are not following this user.");
		loggedUser.unfollow(user);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteUser(RequestMessage reqMsg)
	{
		
		UserInfo user = (UserInfo)reqMsg.getEntity();
		
		try {
			User.validateUsername(user.getUsername());
		} catch (IllegalArgumentException ex) {
			return new ResponseMessage(ex.getMessage());
		}
		if(!loggedUser.isAdmin() || !loggedUser.getUsername().equals(user.getUsername()))
			return new ResponseMessage("You don't have permissions to do this");
		User removedUser = DBManager.session().load(User.class, user.getUsername(), 0);
		DBManager.session().delete(removedUser);
		if (removedUser == null)
			return new ResponseMessage("No such user.");
		return new ResponseMessage(new UserInfo(removedUser.getUsername()));
	}

	@RequestHandlerMethod
	private ResponseMessage handlePutLikeRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		if(restaurant == null)
			return new ResponseMessage("No restaurant specified");
		
		Restaurant toBeLikedRestaurant = DBManager.session().load(Restaurant.class, restaurant.getName(), 0);
		if(loggedUser.getLikedRestaurants().contains(toBeLikedRestaurant))
			return new ResponseMessage("You already like this restaurant");
		loggedUser.likeRestaurant(toBeLikedRestaurant);
		DBManager.session().save(loggedUser);
		return new ResponseMessage(restaurant);
		
	}

	@RequestHandlerMethod
	private ResponseMessage handleRemoveLikeRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		if(restaurant == null)
			return new ResponseMessage("No restaurant specified");
		
		Restaurant toBeUnlikedRestaurant = DBManager.session().load(Restaurant.class, restaurant.getName(), 0);
		if(loggedUser.getLikedRestaurants().contains(toBeUnlikedRestaurant))
			return new ResponseMessage("You already like this restaurant");
		loggedUser.unlikeRestaurant(toBeUnlikedRestaurant);
		DBManager.session().save(loggedUser);
		return new ResponseMessage(restaurant);
		
	}

	@RequestHandlerMethod
	private ResponseMessage handleGetStatisticRestaurant(RequestMessage reqMsg)
	{
		return null;
	}

	@RequestHandlerMethod
	private ResponseMessage handleListCuisines(RequestMessage reqMsg)
	{

		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<Cuisine> cuisines;
		if (filter == null)
			cuisines = DBManager.session().loadAll(Cuisine.class,
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 1);
		else
			cuisines = DBManager.session().loadAll(Cuisine.class,
				new Filter("name", ComparisonOperator.MATCHES, regex),
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 1);
		List<CuisineInfo> infos = new ArrayList<CuisineInfo>();
		cuisines.forEach((Cuisine c) -> {
			infos.add(new CuisineInfo(
				c.getName()			
				));
		});
		return new ResponseMessage(infos.toArray(new CuisineInfo[0]));
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleAddCuisine(RequestMessage reqMsg)
	{
		CuisineInfo cuisine = reqMsg.getEntity(CuisineInfo.class);
		Cuisine savedCuisine = DBManager.session().load(Cuisine.class, cuisine.getName(), 0);
		if(savedCuisine == null) {
			savedCuisine = new Cuisine(cuisine.getName());
			DBManager.session().save(savedCuisine);
			return new ResponseMessage();
		}
		return new ResponseMessage("This cuisine is already present");
			
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteCuisine(RequestMessage reqMsg)
	{
		CuisineInfo cuisine = reqMsg.getEntity(CuisineInfo.class);
		Cuisine savedCuisine = DBManager.session().load(Cuisine.class, cuisine.getName(), 0 );
		if(savedCuisine == null)
			return new ResponseMessage("No such Cuisine " + cuisine.getName());
		if (!loggedUser.isAdmin())
			return new ResponseMessage("You can edit only restaurants that you own.");
		DBManager.session().delete(savedCuisine);
		return new ResponseMessage(cuisine);
	}

	@RequestHandlerMethod
	private ResponseMessage handlePutLikeCuisine(RequestMessage reqMsg)
	{
		return null;
	}

	@RequestHandlerMethod
	private ResponseMessage handleRemoveLikeCuisine(RequestMessage reqMsg)
	{
		return null;
	}

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleListCities(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<City> cities;
		if (filter == null)
			cities = DBManager.session().loadAll(City.class,
				new SortOrder().add("name"),
				new Pagination(0, 10), 0);
		else
			cities = DBManager.session().loadAll(City.class,
				new Filter("name", ComparisonOperator.MATCHES, regex),
				new SortOrder().add("name"),
				new Pagination(0, 10), 0);
		List<CityInfo> infos = new ArrayList<CityInfo>();
		cities.forEach((City c) -> { infos.add(new CityInfo(c.getName(), c.getLatitude(), c.getLongitude())); });
		return new ResponseMessage(infos.toArray(new CityInfo[0]));
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleAddCity(RequestMessage reqMsg)
	{
		return null;
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteCity(RequestMessage reqMsg)
	{
		return null;
	}
}
