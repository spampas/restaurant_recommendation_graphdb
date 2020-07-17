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
	private transient String oldName;
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
		this.oldName = this.name;
		this.name = name;
	}

	public void addRestaurant(Restaurant restaurant)
	{
		getRestaurants().add(restaurant);
	}

	public void removeRestaurant(Restaurant restaurant)
	{
		getRestaurants().remove(restaurant);
	}

	public void save() {
		DBManager.session().query("MATCH (n:Cuisine{name:$oldName}) SET n.name = $name", Map.ofEntries(Map.entry("name", name), Map.entry("oldName", oldName)));
	}
	public String getName()
	{
		return name;
	}

	public List<Restaurant> getRestaurants()
	{
		if (restaurants == null) {
			Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (r:Restaurant)-[s:SERVES]->(c:Cuisine) " +
			"WHERE c.name = $name " +
			"RETURN (r)-[s]->(c)",
			Map.ofEntries(Map.entry("name", name)));
			restaurants = new ArrayList<Restaurant>();
			found.forEach(restaurants::add);
		}
		return restaurants;

	}

	public List<User> getUsersWhoLike()
	{
		if(usersWhoLike == null) {
			Iterable<User> users = DBManager.session().query(User.class,
					"MATCH (user:User)-[l:LIKES]->(cuisine:Cuisine{name: $cuisine}) "
					+ "RETURN (user)-[l]->(cuisine)",
					Map.ofEntries(Map.entry("cuisine", getName())));
			usersWhoLike = new ArrayList<User>();
			users.forEach(usersWhoLike::add);
		}

		return usersWhoLike;
	}

	public void addLikedFrom(User user)
	{
		getUsersWhoLike().add(user);
	}

	public void removeLikedFrom(User user)
	{
		getUsersWhoLike().remove(user);
	}

	public static List<Cuisine> loadCuisinesLikedBy(User user, String nameRegex, int page, int perPage)
	{
		if (nameRegex == null || nameRegex.isEmpty() )
			 return loadCuisinesLikedBy(user,page,perPage);
		String regexFilter = "";
		regexFilter = "AND c.name =~ $regex ";
		Iterable<Cuisine> found = DBManager.session().query(Cuisine.class,
			"MATCH (u:User)-[l:LIKES]->(c:Cuisine) " +
			"WHERE u.username = $username " + regexFilter +
			"RETURN (c)<-[l]-(u) " +
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
				"MATCH (u:User)-[l:LIKES]->(c:Cuisine) " +
				"WHERE u.username = $username " +
				"RETURN (c)<-[l]-(u) " +
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

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if(!(o instanceof Cuisine))
			return false;
		Cuisine c = (Cuisine) o;
		return name.equals(c.name);
	}
}
