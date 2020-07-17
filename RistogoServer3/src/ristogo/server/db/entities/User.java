package ristogo.server.db.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import ristogo.server.db.DBManager;

@NodeEntity
public class User
{
	@Id
	private String username;
	@Property
	private String passwordHash;
	@Labels
	private List<String> labels = new ArrayList<>();

	@Relationship(type = "LOCATED")
	private City city;
	@Relationship(type = "OWNS")
	private List<Restaurant> ownedRestaurants;
	@Relationship(type = "LIKES")
	private List<Restaurant> likedRestaurants;
	@Relationship(type = "FOLLOWS")
	private List<User> followedUsers;
	@Relationship(type = "FOLLOWS", direction = Relationship.INCOMING)
	private List<User> followingUsers;
	@Relationship(type = "LIKES")
	private List<Cuisine> likedCuisines;

	public User()
	{
	}

	public User(String username, String password, City city)
	{
		validateUsername(username);
		validatePassword(password);
		this.username = username;
		this.passwordHash = hashPassword(password);
		this.city = city;
	}

	public static final void validateUsername(String username)
	{
		if (username == null || username.isBlank())
			throw new IllegalArgumentException("Username must be specified.");
		if (!username.matches("^[A-Za-z][A-Za-z0-9]{2,31}$"))
			throw new IllegalArgumentException("Username must contains only alphanumeric characters, at least 3 characters and at most 32 characters.");
	}

	public static boolean isValidUsername(String username)
	{
		try {
			validateUsername(username);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static final void validatePassword(String password)
	{
		if (password == null || password.isBlank() || password.length() < 8)
			throw new IllegalArgumentException("Password must be at least 8 characters long.");
	}

	protected final static String hashPassword(String password)
	{
		String hash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			hash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, "This Java installation does not support SHA-256 hashing algorithm (required for password hashing).", ex);
			hash = password;
		}
		return hash;
	}

	public String getUsername()
	{
		return username;
	}

	public List<Restaurant> getOwnedRestaurants()
	{
		if (ownedRestaurants == null) {
			Iterable<Restaurant> restaurants = DBManager.session().query(Restaurant.class,
				"MATCH (user:User)-[:OWNS]->(restaurant:Restaurant) " +
				"WHERE user.username = $username " +
				"RETURN (restaurant)--()",
				Map.ofEntries(Map.entry("username", username)));
			ownedRestaurants = new ArrayList<Restaurant>();
			restaurants.forEach(ownedRestaurants::add);
		}
		return ownedRestaurants;
	}

	public List<Restaurant> getLikedRestaurants()
	{
			Iterable<Restaurant> restaurants = DBManager.session().query(Restaurant.class,
					"MATCH (user:User)-[:LIKES]->(restaurant:Restaurant) "
					+ "WHERE user.username = $username "
					+ "RETURN restaurant", Map.ofEntries(Map.entry("username", getUsername())));
			likedRestaurants = new ArrayList<Restaurant>();
			if(restaurants != null)
				restaurants.forEach(likedRestaurants::add);
		return likedRestaurants;
	}

	public List<Cuisine> getLikedCuisines(String regex)
	{
		String regexFilter = "";
		if (regex == null)
			return getLikedCuisines();

		regexFilter = "AND cuisine.name =~ $regex ";
		Iterable<Cuisine> cuisines = DBManager.session().query(Cuisine.class,
				"MATCH (user:User)-[:LIKES]->(cuisine:Cuisine) "
				+ "WHERE user.username = $username " + regexFilter
				+ "RETURN cuisine", Map.ofEntries(Map.entry("username", getUsername()),
						Map.entry("regex", regex)));
		likedCuisines = new ArrayList<Cuisine>();
		cuisines.forEach(likedCuisines::add);
		return likedCuisines;
	}

	public List<Cuisine> getLikedCuisines()
	{
		Iterable<Cuisine> cuisines = DBManager.session().query(Cuisine.class,
					"MATCH (user:User)-[:LIKES]->(cuisine:Cuisine) "
					+ "WHERE user.username = $username "
					+ "RETURN cuisine", Map.ofEntries(Map.entry("username", getUsername())));
			likedCuisines = new ArrayList<Cuisine>();
			cuisines.forEach(likedCuisines::add);
			return likedCuisines;
	}

	public void unlikeCuisine(Cuisine cuisine)
	{
		getLikedCuisines().remove(cuisine);
		cuisine.removeLikedFrom(this);
	}

	public void setUsername(String username)
	{
		validateUsername(username);
		this.username = username;
	}

	public void addRestaurant(Restaurant restaurant)
	{
		getOwnedRestaurants().add(restaurant);
	}

	public void likeRestaurant(Restaurant restaurant)
	{
		getLikedRestaurants().add(restaurant);
		restaurant.addLikedFrom(this);
	}

	public void unlikeRestaurant(Restaurant restaurant)
	{
		ListIterator<Restaurant> iterator = getLikedRestaurants().listIterator();
		while(iterator.hasNext())
			if(iterator.next().equals(restaurant)) {
				iterator.remove();
				restaurant.removeLikedFrom(this);
				return;
			}
	}

	public void likeCuisine(Cuisine cuisine)
	{
		getLikedCuisines().add(cuisine);
		cuisine.addLikedFrom(this);
	}

	public boolean checkPasswordHash(String passwordHash)
	{
		return this.passwordHash.equalsIgnoreCase(passwordHash);
	}

	public boolean checkPassword(String password)
	{
		return checkPasswordHash(hashPassword(password));
	}

	public void setPassword(String password)
	{
		this.passwordHash = hashPassword(password);
	}

	public boolean isAdmin()
	{
		for (String label: labels)
			if (label.equals("Admin"))
				return true;
		return false;
	}

	public void setAdmin(boolean admin)
	{
		if (admin && !isAdmin())
			labels.add("Admin");
		if (!admin && isAdmin()) {
			ListIterator<String> iterator = labels.listIterator();
			while (iterator.hasNext())
				if (iterator.next().equals("Admin")) {
					iterator.remove();
					break;
				}
		}
	}

	public void promote()
	{
		setAdmin(true);
	}

	public void demote()
	{
		setAdmin(false);
	}

	public boolean isOwner()
	{
		return getOwnedRestaurants().size() > 0;
	}

	public City getCity()
	{
		if (city == null)
			city = DBManager.session().queryForObject(City.class,
				"MATCH (c:City)<-[:LOCATED]-(u:User) " +
				"WHERE u.username = $username " +
				"RETURN c",
				Map.ofEntries(Map.entry("username", username)));
		if(city == null)
			return new City("nd", 0, 0);
		return city;
	}

	public void setCity(City city)
	{
		if(this.city != null)
			this.city.removeUser(this);
		this.city = city;
		city.addUser(this);
	}

	public static List<User> loadFollowingOf(String filter, User startUser, int page, int perPage)
	{
		String filterQuery = "";
		if (filter != null)
			filterQuery = "AND u.username =~ $regex ";
		else
			filter = "";
		Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)-[:FOLLOWS]->(user2:User)-[l:LOCATED]->(c:City) " +
			"WHERE user1.username = $username " + filterQuery +
			"RETURN (user2)-[l]->(c) " +
			"ORDER BY user2.name " +
			"SKIP $skip " +
			"LIMIT $limit",
			Map.ofEntries(
				Map.entry("username", startUser.username),
				Map.entry("regex", filter),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)
			));
		List<User> following = new ArrayList<User>();
		users.forEach(following::add);
		return following;
	}

	public static List<User> loadFollowersOf(String filter, User startUser, int page, int perPage)
	{
		String filterQuery = "";
		if (filter != null)
			filterQuery = "AND u.username =~ $regex ";
		else
			filter = "";
		Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)<-[:FOLLOWS]-(user2:User)-[l:LOCATED]->(c:City) " +
			"WHERE user1.username = $username " + filterQuery +
			"RETURN (user2)-[l]->(c) " +
			"ORDER BY user2.name " +
			"SKIP $skip " +
			"LIMIT $limit",
			Map.ofEntries(
				Map.entry("username", startUser.username),
				Map.entry("regex", filter),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)
			));
		List<User> following = new ArrayList<User>();
		users.forEach(following::add);
		return following;
	}

	public List<User> recommendUser(Cuisine cuisine, int distance, City city, int page, int perPage)
	{
		City targetCity = city == null ? getCity() : city;
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("city", targetCity.getName());
		parameters.put("username", getUsername());
		parameters.put("distance", distance*1000);
		parameters.put("skip", page*perPage);
		parameters.put("limit", perPage);
		String cuisineQuery = " ";
		if(cuisine != null) {
			parameters.put("cuisine", cuisine.getName());
			cuisineQuery = "-[:LIKES]->(c:Cuisine{name:$cuisine}) ";
		}

		Iterable<User> recommended = DBManager.session().query(User.class, "MATCH (city:City{name:$city}), (city1:City)<-[:LOCATED]-(u:User)" + cuisineQuery +
				"WITH city1, u, point({longitude: city.longitude, latitude:city.latitude}) as p1, " +
				"point({longitude: city1.longitude, latitude:city1.latitude}) as p2 " +
				"WITH distance(p1,p2) as dist, city1, u " +
				"WHERE dist <= $distance AND NOT EXISTS ((u)<-[:FOLLOWS]-(:User{username:$username})) "
				+ "AND (u.username <> $username)" +
				"RETURN (u)-[:LOCATED]->(:City), dist " +
				"ORDER BY dist " +
				"SKIP $skip " +
				"LIMIT $limit", parameters);

		List<User> users = new ArrayList<User>();
		recommended.forEach(users::add);
		return users;
	}

	public List<User> getFollowedUsers()
	{
		if (followedUsers == null) {
			Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)-[:FOLLOWS]->(user2:User) " +
			"WHERE user1.username = $username " +
			"RETURN (user2)--()",
			Map.ofEntries(Map.entry("username", username)));
			followedUsers = new ArrayList<User>();
			users.forEach(followedUsers::add);
		}
		return followedUsers;
	}

	public List<User> getFollowingUsers()
	{
		if (followingUsers == null) {
			Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)<-[:FOLLOWS]-(user2:User)-[l:LOCATED]->(c:City) " +
			"WHERE user1.username = $username " +
			"RETURN (user2)-[l]->(c)",
			Map.ofEntries(Map.entry("username", username)));
			followingUsers = new ArrayList<User>();
			users.forEach(followingUsers::add);
		}
		return followingUsers;
	}

	public boolean isFollowing(User user)
	{
		List<User> following = getFollowedUsers();
		for (User followed: following)
			if (followed.equals(user))
				return true;
		return false;
	}

	public boolean isFollowedBy(User user)
	{
		List<User> followers = getFollowingUsers();
		for (User follower: followers)
			if (follower.equals(user))
				return true;
		return false;
	}

	public void follow(User user)
	{
		if (followedUsers == null)
			followedUsers = new ArrayList<User>();
		followedUsers.add(user);
		user.addFollower(this);
	}

	public void addFollower(User user)
	{
		List<User> followers = getFollowingUsers();
		followers.add(user);
	}

	public void unfollow(User user)
	{
		ListIterator<User> iterator = getFollowedUsers().listIterator();
		while (iterator.hasNext())
			if (iterator.next().equals(user)) {
				iterator.remove();
				user.removeFollower(this);
				return;
			}
	}

	public void removeFollower(User user)
	{
		List<User> followers = getFollowingUsers();
		ListIterator<User> iterator = followers.listIterator();
		while (iterator.hasNext())
			if (iterator.next().equals(user)) {
				iterator.remove();
				return;
			}
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
			return true;
		if (!(o instanceof User))
			return false;
		User u = (User)o;
		return username.equals(u.username);
	}

	public static List<User> loadAll(String filter, User me, int page, int perPage)
	{
		String filterQuery = "";
		if (filter != null)
			filterQuery = "AND u.username =~ $regex ";
		else
			filter = "";
		Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (u:User)" +
			"WHERE u.username <> $username " + filterQuery +
			"RETURN (u)--() " +
			"ORDER BY u.username " +
			"SKIP $skip " +
			"LIMIT $limit",
			Map.ofEntries(
				Map.entry("username", me.username),
				Map.entry("regex", filter),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)
			));
		List<User> list = new ArrayList<User>();
		users.forEach(list::add);
		return list;
	}
}
