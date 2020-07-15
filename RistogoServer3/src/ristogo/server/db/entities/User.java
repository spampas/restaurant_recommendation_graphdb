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
				"RETURN restaurant",
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

	public List<Cuisine> getLikedCuisines(String regex){
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
	}
	
	public void unlikeRestaurant(Restaurant restaurant) {
		ListIterator<Restaurant> iterator = getLikedRestaurants().listIterator();
		while(iterator.hasNext())
			if(iterator.next().equals(restaurant)) {
				iterator.remove();
				return;
			}
	}

	public void likeCuisine(Cuisine cuisine)
	{
		getLikedCuisines().add(cuisine);
	}

	public boolean checkPasswordHash(String passwordHash)
	{
		return passwordHash.equalsIgnoreCase(passwordHash);
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
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public List<User> getFollowedUsers()
	{
		if (followedUsers == null) {
			Iterable<User> users = DBManager.session().query(User.class,
				"MATCH (user1:User)-[:FOLLOWS]->(user2:User) " +
				"WHERE user1.username = $username " +
				"RETURN user2",
				Map.ofEntries(Map.entry("username", username)));
			followedUsers = new ArrayList<User>();
			users.forEach(followedUsers::add);
		}
		return followedUsers;
	}
	
	public List<User> getFollowedUsers(String regex)
	{
		String regexFilter = "";
		if (regex == null)
			return getFollowingUsers();
		
		regexFilter = "AND user2.username =~ $regex ";
		Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)-[:FOLLOWS]->(user2:User) " +
			"WHERE user1.username = $username " + regexFilter +
			"RETURN user2",
			Map.ofEntries(Map.entry("username", username), Map.entry("regex", regex)));
		List<User> following = new ArrayList<User>();
		users.forEach(following::add);
		return following;
	}

	public List<User> getFollowingUsers(String regex)
	{
		
		String regexFilter = "";
		if (regex == null)
			return getFollowingUsers();
		regexFilter = "AND user1.username =~ $regex ";
		
		Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (user1:User)-[:FOLLOWS]->(user2:User) " +
			"WHERE user2.username = $username " + regexFilter +
			"RETURN user1",
			Map.ofEntries(Map.entry("username", username), Map.entry("regex", regex)));
		List<User> followers = new ArrayList<User>();
		users.forEach(followers::add);
	
		return followers;
	}

	public List<User> getFollowingUsers()
	{
		if (followingUsers == null) {
			Iterable<User> users = DBManager.session().query(User.class,
				"MATCH (user1:User)-[:FOLLOWS]->(user2:User) " +
				"WHERE user2.username = $username " +
				"RETURN user1",
				Map.ofEntries(Map.entry("username", username)));
			followingUsers = new ArrayList<User>();
			users.forEach(followingUsers::add);
		}
		return followingUsers;
	}
	
	public List<User> recommendUser(Cuisine cuisine, int distance, boolean airDistance, City city){
		City targetCity = city == null ? getCity() : city;
		Map<String,Object> parameters = new HashMap<String,Object>();
		String distanceQuery = null;
		String cuisineQuery = "";
		String cityQuery = "(city) ";
		parameters.put("city", targetCity.getName());
		parameters.put("username", getUsername());
		if(distance > 0) {
			parameters.put("distance", distance);
			cityQuery = "(city|city2) ";
			if(airDistance)
				distanceQuery = ", (city2:City) "
				+ "WHERE distance(point1({latitude: city2.latitude, longitude: city2.longitude}), "
						+ "point2({latitude: city.latitude, longitude: city.longitude:})) "
						+ "<= $distance ";
			else
				distanceQuery = "";
		}
		if(cuisine != null) {
			parameters.put("cuisine", cuisine.getName());
			cuisineQuery = "(cuisine:Cuisine{name:$cuisine})<-[:LIKE]-";
		}
			
		
		Iterable<User> recommended = DBManager.session().query(User.class,
				"MATCH (city:City{name:$city}) " + distanceQuery 
				+ "MATCH "+ cuisineQuery +"(user:User)-[:LIVE]->"+ cityQuery
					+ "WHERE NOT EXISTS( (:User{name:$username})-[:FOLLOWS]->(user) ) "
				+ "RETURN user", parameters);
		List<User> users = new ArrayList<User>();
		recommended.forEach(users::add);
		return users;
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
	}

	public void addFollower(User user)
	{
		List<User> followers = getFollowingUsers();
		followers.add(user);
	}

	public void unfollow(User user)
	{
		List<User> followed = getFollowedUsers();
		ListIterator<User> iterator = followed.listIterator();
		while (iterator.hasNext())
			if (iterator.next().equals(user)) {
				iterator.remove();
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

	
}
