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
import org.neo4j.ogm.session.Session;

import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.Entity;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RecommendRestaurantInfo;
import ristogo.common.net.entities.RecommendUserInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StatisticInfo;
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

		System.out.println("--- RECEIVED ---");
		System.out.println(reqMsg.toXML());

		if (loggedUser != null)
			loggedUser = DBManager.session().load(User.class, loggedUser.getUsername(), 1);

		ResponseMessage resMsg = dispatchMessage(reqMsg);

		System.out.println("--- RESPONSE ---");
		System.out.println(resMsg.toXML());
		System.out.println("----------------");

		resMsg.send(outputStream);
		DBManager.getInstance().close();
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
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		List<Restaurant> savedRestaurants = Restaurant.loadRestaurantsOwnedBy(loggedUser, regex, pageFilter.getPage(), pageFilter.getPerPage());
		List<RestaurantInfo> restaurants = new ArrayList<RestaurantInfo>();
		for (Restaurant restaurant: savedRestaurants)
			restaurants.add(new RestaurantInfo(restaurant.getName(),
				new CuisineInfo(restaurant.getCuisine().getName()),
				restaurant.getPrice(),
				new CityInfo(restaurant.getCity().getName())));
		return new ResponseMessage(restaurants.toArray(new RestaurantInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleAddRestaurant(RequestMessage reqMsg)
	{
		RestaurantInfo restaurant = reqMsg.getEntity(RestaurantInfo.class);
		City city = DBManager.session().load(City.class, restaurant.getCity().getName(), 1);
		if (city == null)
			return new ResponseMessage("The specified city can not be found.");
		Cuisine cuisine = DBManager.session().load(Cuisine.class, restaurant.getCuisine().getName(), 1);
		if (cuisine == null)
			return new ResponseMessage("The specified cuisine can not be found.");
		Restaurant savedRestaurant = new Restaurant(restaurant.getName(), loggedUser, restaurant.getPrice(), cuisine, city, restaurant.getDescription());
		city.addRestaurant(savedRestaurant);
		cuisine.addRestaurant(savedRestaurant);
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
		Iterable<Restaurant> restaurants = null;
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
		if(restaurants == null)
			return new ResponseMessage();
		restaurants.forEach((Restaurant r) -> {
			infos.add(new RestaurantInfo(
				r.getName(),
				new UserInfo(r.getOwner().getUsername()),
				new CuisineInfo(r.getCuisine().getName()),
				r.getPrice(),
				new CityInfo(r.getCity().getName(), r.getCity().getLatitude(),r.getCity().getLongitude()),
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
		savedRestaurant.save();
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleDeleteRestaurant(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String name = filter == null ? null : filter.getValue();
		if (name == null)
			return new ResponseMessage("Invalid restaurant specified.");
		Restaurant savedRestaurant = DBManager.session().load(Restaurant.class, name ,1);
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
		List<User> users = User.loadAll(regex, loggedUser, pageFilter.getPage(), pageFilter.getPerPage());
		List<UserInfo> infos = new ArrayList<UserInfo>();
		users.forEach((User u) -> {
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
		Collection<User> users = User.loadFollowersOf(regex, loggedUser, pageFilter.getPage(), pageFilter.getPerPage());
		List<UserInfo> infos = new ArrayList<UserInfo>();
		users.forEach((User u) -> {
			infos.add(new UserInfo(u.getUsername(), new CityInfo(u.getCity().getName()), loggedUser.isFollowedBy(u)));
		});
		return new ResponseMessage(infos.toArray(new UserInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleListFollowing(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<User> users = User.loadFollowingOf(regex, loggedUser, pageFilter.getPage(), pageFilter.getPerPage());
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
		loggedUser = DBManager.session().load(User.class, loggedUser.getUsername(),1);
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

	@RequestHandlerMethod
	private ResponseMessage handleGetCity(RequestMessage reqMsg)
	{
		CityInfo city = new CityInfo(loggedUser.getCity().getName(), loggedUser.getCity().getLatitude(), loggedUser.getCity().getLongitude());
		return new ResponseMessage(city);
	}

	@RequestHandlerMethod
	private ResponseMessage handleSetCity(RequestMessage reqMsg)
	{
		StringFilter city = reqMsg.getEntity(StringFilter.class);
		if (city == null)
			return new ResponseMessage("No city selected.");
		City savedCity = DBManager.session().load(City.class, city.getValue(), 0);
		if(savedCity == null)
			return new ResponseMessage("Can't find selected city " + city.getValue() + ".");
		loggedUser.setCity(savedCity);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleEditCity(RequestMessage reqMsg)
	{
		StringFilter name = reqMsg.getEntity(StringFilter.class);
		if(name == null)
			return new ResponseMessage("No City specified");
		CityInfo city = reqMsg.getEntity(CityInfo.class);
		if (city == null)
			return new ResponseMessage("No update for city " + name.getValue());
		City savedCity = DBManager.session().load(City.class, name.getValue(), 0);
		if(savedCity == null)
			return new ResponseMessage("Can't find selected city " + city.getName() + ".");

		savedCity.setName(city.getName());
		savedCity.setLatitude(city.getLatitude());
		savedCity.setLongitude(city.getLongitude());

		savedCity.save();
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleEditCuisine(RequestMessage reqMsg)
	{
		StringFilter name = reqMsg.getEntity(StringFilter.class);
		if(name == null)
			return new ResponseMessage("No cuisine selected");
		CuisineInfo cuisine = reqMsg.getEntity(CuisineInfo.class);
		if (cuisine == null)
			return new ResponseMessage("No update for cuisine" + name.getValue());
		Cuisine saved = DBManager.session().load(Cuisine.class, name.getValue(), 0);
		if(saved == null)
			return new ResponseMessage("Can't find selected cuisine " + cuisine.getName() + ".");

		saved.setName(cuisine.getName());
		saved.save();
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
	private ResponseMessage handleLikeRestaurant(RequestMessage reqMsg)
	{
		StringFilter restaurant = reqMsg.getEntity(StringFilter.class);
		String name = restaurant == null ? null : restaurant.getValue();
		if(name == null)
			return new ResponseMessage("No restaurant specified.");
		Restaurant toBeLikedRestaurant = DBManager.session().load(Restaurant.class, name, 0);
		if(toBeLikedRestaurant == null)
			return new ResponseMessage("Can't find the specified Restaurant.");
		if(loggedUser.getLikedRestaurants().contains(toBeLikedRestaurant))
			return new ResponseMessage("You already like this restaurant.");
		loggedUser.likeRestaurant(toBeLikedRestaurant);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleUnlikeRestaurant(RequestMessage reqMsg)
	{
		StringFilter restaurant = reqMsg.getEntity(StringFilter.class);
		String name = restaurant == null ? null : restaurant.getValue();
		if(name == null)
			return new ResponseMessage("No restaurant specified.");
		Restaurant toBeUnlikedRestaurant = DBManager.session().load(Restaurant.class, name, 0);
		if(toBeUnlikedRestaurant == null)
			return new ResponseMessage("Can't find the specified Restaurant.");
		if(!loggedUser.getLikedRestaurants().contains(toBeUnlikedRestaurant))
			return new ResponseMessage("You already unliked this restaurant.");
		loggedUser.unlikeRestaurant(toBeUnlikedRestaurant);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleGetStatisticRestaurant(RequestMessage reqMsg)
	{
		StringFilter restaurant = reqMsg.getEntity(StringFilter.class);
		String name = restaurant == null ? null : restaurant.getValue();
		if(name == null)
			return new ResponseMessage("No restaurant specified.");
		Restaurant savedRestaurant = DBManager.session().load(Restaurant.class, name, 1);
		if(savedRestaurant == null)
			return new ResponseMessage("Can't find the specified Restaurant.");
		StatisticInfo stat = new StatisticInfo(savedRestaurant.getLikesCount(),
				savedRestaurant.getCuisineRank(),
				savedRestaurant.getCityRank(),
				savedRestaurant.getCityCuisineRank());
		RestaurantInfo res = new RestaurantInfo(savedRestaurant.getName(),
				null,
				new CuisineInfo(savedRestaurant.getCuisine().getName()),
				savedRestaurant.getPrice(),
				new CityInfo(savedRestaurant.getCity().getName(),
						savedRestaurant.getCity().getLatitude(),
						savedRestaurant.getCity().getLongitude()),
				savedRestaurant.getDescription());
		return new ResponseMessage(stat, res);
	}

	@RequestHandlerMethod
	private ResponseMessage handleGetUser(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String username = filter == null ? null : filter.getValue();
		if (username == null || !User.isValidUsername(username))
			return new ResponseMessage("Invalid user.");
		User user = DBManager.session().load(User.class, username, 0);
		if (user == null)
			return new ResponseMessage("Can not find the specified user.");
		List<CuisineInfo> cuisineInfo = new ArrayList<CuisineInfo>();
		user.getLikedCuisines().forEach((Cuisine cuisine) -> {
			cuisineInfo.add(new CuisineInfo(cuisine.getName()));
		});

		UserInfo userInfo = new UserInfo(user.getUsername(),
				new CityInfo(user.getCity().getName(),
				user.getCity().getLatitude(),
				user.getCity().getLongitude()),
				loggedUser.isFollowing(user));

		List<Entity> entities = new ArrayList<Entity>();

		entities.add(userInfo);
		cuisineInfo.forEach(entities::add);
		return new ResponseMessage(entities);
	}

	@RequestHandlerMethod
	private ResponseMessage handleGetRestaurant(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		String name = filter == null ? null : filter.getValue();

		Restaurant restaurant = DBManager.session().load(Restaurant.class, name, 1);
		if (restaurant == null)
			return new ResponseMessage("Can not find the specified restaurant.");

		RestaurantInfo restaurantInfo = new RestaurantInfo(restaurant.getName(),
				new UserInfo(restaurant.getOwner().getUsername()),
				new CuisineInfo(restaurant.getCuisine().getName()),
				restaurant.getPrice(),
				new CityInfo(restaurant.getCity().getName(),
						restaurant.getCity().getLatitude(),
						restaurant.getCity().getLongitude()),
				restaurant.getDescription(),
				loggedUser.getLikedRestaurants().contains(restaurant));

		return new ResponseMessage(restaurantInfo);
	}

	@RequestHandlerMethod
	private ResponseMessage handleListCuisines(RequestMessage reqMsg)
	{

		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<Cuisine> cuisines;
		if(pageFilter == null)
			if(filter == null)
				cuisines = DBManager.session().loadAll(Cuisine.class,
					new SortOrder().add("name"), 1);
			else
				cuisines = DBManager.session().loadAll(Cuisine.class,
						new Filter("name", ComparisonOperator.MATCHES, regex),
						new SortOrder().add("name"),1);
		else
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
			infos.add(new CuisineInfo(c.getName()));
		});
		return new ResponseMessage(infos.toArray(new CuisineInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleListLikedCuisines(RequestMessage reqMsg)
	{

		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		List<Cuisine> cuisines = Cuisine.loadCuisinesLikedBy(loggedUser, regex, pageFilter.getPage(), pageFilter.getPerPage() );
		List<CuisineInfo> infos = new ArrayList<CuisineInfo>();
		cuisines.forEach((Cuisine c) -> {
			infos.add(new CuisineInfo(c.getName()));
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
		return new ResponseMessage("This cuisine is already present.");
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteCuisine(RequestMessage reqMsg)
	{
		StringFilter cuisine = reqMsg.getEntity(StringFilter.class);
		Cuisine savedCuisine = DBManager.session().load(Cuisine.class, cuisine.getValue(), 0);
		if(savedCuisine == null)
			return new ResponseMessage("No such Cuisine " + cuisine.getValue());
		DBManager.session().delete(savedCuisine);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleRecommendRestaurant(RequestMessage reqMsg)
	{
		RecommendRestaurantInfo info = reqMsg.getEntity(RecommendRestaurantInfo.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		if(info == null)
			return new ResponseMessage("Recommendation info is null.");
		Cuisine cuisine = null;
		if(info.getCuisine() != null)
			cuisine = DBManager.session().load(Cuisine.class, info.getCuisine().getName(), 0);
		if(info.getCity() == null)
			return new ResponseMessage("No city specified.");
		City city = DBManager.session().load(City.class, info.getCity().getName(), 0);
		if(info.getDepth() == null)
			return new ResponseMessage("No depth of search specified");
		if(info.getPrice() == null)
			return new ResponseMessage("No price specified");

		List<Restaurant> recommended = Restaurant.recommendRestaurant(loggedUser, cuisine, city, info.getDistance(), info.getDepth(), info.getPrice(), pageFilter.getPage(), pageFilter.getPerPage());
		List<RestaurantInfo> restaurants = new ArrayList<RestaurantInfo>();
		recommended.forEach((Restaurant r) -> {
			CityInfo cityInfo = new CityInfo(r.getCity().getName());
			CuisineInfo cuisineInfo = new CuisineInfo(r.getCuisine().getName());
			restaurants.add(new RestaurantInfo(r.getName(), cuisineInfo , r.getPrice(), cityInfo));
		});
		return new ResponseMessage(restaurants.toArray(new RestaurantInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleRecommendUser(RequestMessage reqMsg)
	{
		RecommendUserInfo info = reqMsg.getEntity(RecommendUserInfo.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		if(info == null)
			return new ResponseMessage("Recommendation info is null.");
		Cuisine cuisine = null;
		if(info.getCuisine() != null)
			cuisine = DBManager.session().load(Cuisine.class, info.getCuisine().getName(), 0);
		if(info.getCity() == null)
			return new ResponseMessage("No city specified.");

		City city = DBManager.session().load(City.class, info.getCity().getName(), 0);
		List<User> recommended = loggedUser.recommendUser(cuisine, info.getDistance(), city, pageFilter.getPage(), pageFilter.getPerPage());
		List<UserInfo> recommendedInfo = new ArrayList<UserInfo>();
		recommended.forEach((User u) -> {
			CityInfo cityInfo = new CityInfo(u.getCity().getName());
			recommendedInfo.add(new UserInfo(u.getUsername(), cityInfo));
		});

		return new ResponseMessage(recommendedInfo.toArray(new UserInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleLikeCuisine(RequestMessage reqMsg)
	{
		StringFilter cuisine = reqMsg.getEntity(StringFilter.class);
		String name = cuisine == null ? null : cuisine.getValue();
		if(name == null)
			return new ResponseMessage("No cuisine specified.");
		Cuisine toBeLikedCuisine = DBManager.session().load(Cuisine.class, name, 0);
		if(toBeLikedCuisine == null)
			return new ResponseMessage("Can't find the specified cuisine.");
		if(loggedUser.getLikedCuisines().contains(toBeLikedCuisine))
			return new ResponseMessage("You already like this cuisine.");
		loggedUser.likeCuisine(toBeLikedCuisine);
		DBManager.session().save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleUnlikeCuisine(RequestMessage reqMsg)
	{
		StringFilter cuisine = reqMsg.getEntity(StringFilter.class);
		String name = cuisine == null ? null : cuisine.getValue();
		if(name == null)
			return new ResponseMessage("No cuisine specified.");
		Session session = DBManager.session();
		Cuisine toBeUnlikedCuisine = session.load(Cuisine.class, name, 1);
		if(toBeUnlikedCuisine == null)
			return new ResponseMessage("Can't find the specified cuisine.");
		if(!loggedUser.getLikedCuisines().contains(toBeUnlikedCuisine))
			return new ResponseMessage("You already unlike this cuisine.");
		loggedUser.unlikeCuisine(toBeUnlikedCuisine);
		session.save(loggedUser);
		return new ResponseMessage();
	}

	@RequestHandlerMethod(requiresLogin=false)
	private ResponseMessage handleListCities(RequestMessage reqMsg)
	{
		StringFilter filter = reqMsg.getEntity(StringFilter.class);
		PageFilter pageFilter = reqMsg.getEntity(PageFilter.class);
		String regex = filter == null ? null : "(?i).*" + filter.getValue() + ".*";
		Collection<City> cities;
		if (filter == null)
			cities = DBManager.session().loadAll(City.class,
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 0);
		else
			cities = DBManager.session().loadAll(City.class,
				new Filter("name", ComparisonOperator.MATCHES, regex),
				new SortOrder().add("name"),
				new Pagination(pageFilter.getPage(), pageFilter.getPerPage()), 0);
		List<CityInfo> infos = new ArrayList<CityInfo>();
		cities.forEach((City c) -> { infos.add(new CityInfo(c.getName(), c.getLatitude(), c.getLongitude())); });
		return new ResponseMessage(infos.toArray(new CityInfo[0]));
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleAddCity(RequestMessage reqMsg)
	{
		CityInfo city = reqMsg.getEntity(CityInfo.class);
		City savedCity = DBManager.session().load(City.class, city.getName(), 0);
		if(savedCity == null) {
			savedCity = new City(city.getName(), city.getLatitude(), city.getLongitude());
			DBManager.session().save(savedCity);
			return new ResponseMessage();
		}
		return new ResponseMessage("This city is already present.");
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteCity(RequestMessage reqMsg)
	{
		StringFilter city = reqMsg.getEntity(StringFilter.class);
		if(city == null)
			return new ResponseMessage("No city selected");
		City savedCity = DBManager.session().load(City.class, city.getValue(), 0 );
		if(savedCity == null)
			return new ResponseMessage("No such City:  " + city.getValue());
		DBManager.session().delete(savedCity);
		return new ResponseMessage();
	}
}
