package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import ristogo.server.db.DBManager;

@NodeEntity
public class Cuisine
{
	@Id
	private String name;

	@Relationship(type = "SERVES", direction = Relationship.INCOMING)
	private List<Restaurant> restaurants;
	@Relationship(type = "LIKES", direction = Relationship.INCOMING)
	private List<User> usersWhoLike;

	public Cuisine()
	{
	}

	public Cuisine(String name)
	{
		this.name = name;
		this.restaurants = new ArrayList<Restaurant>();
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addRestaurant(Restaurant restaurant)
	{
		if (restaurants == null)
			restaurants = new ArrayList<Restaurant>();
		restaurants.add(restaurant);
	}

	public String getName()
	{
		return name;
	}

	public List<Restaurant> getRestaurants()
	{
		return restaurants;
	}

	public List<User> getUsersWhoLike()
	{
		if(usersWhoLike == null) {
			Iterable<User> users = DBManager.session().query(User.class,
					"MATCH (user:User)-[:LIKE]->(cuisine:Cuisine{name: $cuisine}) "
					+ "RETURN user", Map.ofEntries(Map.entry("cuisine", getName())));
			usersWhoLike = new ArrayList<User>();
			users.forEach(usersWhoLike::add);
		}
			
		return usersWhoLike;
	}
	
	public void addUserWhoLike(User user) {
		getUsersWhoLike().add(user);
	}
	
	public static List<Cuisine> loadCuisinesLikedBy(User user, String nameRegex, int page, int perPage)
	{
		if (nameRegex == null || nameRegex.isEmpty() )
			 return loadCuisinesLikedBy(user,page,perPage);
		String regexFilter = "";
		regexFilter = "AND c.name =~ $regex ";
		Iterable<Cuisine> found = DBManager.session().query(Cuisine.class,
			"MATCH (u:User)-[:LIKES]->(c:Cuisine) " +
			"WHERE u.username = $username " + regexFilter +
			"RETURN c " +
			"ORDER BY c.name " +
			"SKIP $skip " +
			"LIMIT $limit ",
			Map.ofEntries(Map.entry("username", user.getUsername()),
				Map.entry("regex", nameRegex),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)));
		List<Cuisine> cuisines = new ArrayList<Cuisine>();
		found.forEach(cuisines::add);
		return cuisines;
	}
	
	public static List<Cuisine> loadCuisinesLikedBy(User user, int page, int perPage)
	{
		
		Iterable<Cuisine> found = DBManager.session().query(Cuisine.class,
				"MATCH (u:User)-[:LIKES]->(c:Cuisine) " +
				"WHERE u.username = $username " +
				"RETURN c " +
				"ORDER BY c.name " +
				"SKIP $skip " +
				"LIMIT $limit ",
				Map.ofEntries(Map.entry("username", user.getUsername()),
					Map.entry("skip", page*perPage),
					Map.entry("limit", perPage)));
			List<Cuisine> cuisines = new ArrayList<Cuisine>();
			found.forEach(cuisines::add);
			return cuisines;
	}
	
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Cuisine)) return false;
		Cuisine c = (Cuisine) o;
		return name.equals(c.name);
	}
}
