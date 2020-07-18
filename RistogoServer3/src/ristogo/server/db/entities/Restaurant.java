package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import ristogo.common.net.entities.enums.LikesFrom;
import ristogo.common.net.entities.enums.Price;
import ristogo.server.db.DBManager;

@NodeEntity
public class Restaurant
{
	@Id
	private String name;
	private transient String oldName;
	@Property
	private Price price;
	@Property
	private String description;

	@Relationship(type = "SERVES")
	private Cuisine cuisine;
	@Relationship(type = "LOCATED")
	private City city;
	@Relationship(type = "OWNS", direction = Relationship.INCOMING)
	private User owner;
	@Relationship(type = "LIKES", direction = Relationship.INCOMING)
	private List<User> usersWhoLike;

	public Restaurant()
	{
	}

	public Restaurant(String name, User owner, Price price, Cuisine cuisine, City city, String description)
	{
		this.name = name;
		this.owner = owner;
		this.price = price;
		this.cuisine = cuisine;
		this.city = city;
		this.description = description;
	}

	public void setName(String name)
	{
		this.oldName = this.name;
		this.name = name;
	}

	public void save()
	{
		DBManager.session().query("MATCH (newCity:City{name:$city}), (newCuisine:Cuisine{name:$cuisine}), (:City)<-[loc:LOCATED]-(r:Restaurant{name:$oldName})-[ser:SERVES]->(:Cuisine) "
				+ "SET r = {name : $name, description : $description, price: $price} "
				+ "DELETE loc, ser "
				+ "CREATE (newCuisine)<-[:SERVES]-(r)-[:LOCATED]->(newCity)",
				Map.ofEntries(Map.entry("name", name), 
						Map.entry("oldName", oldName),
						Map.entry("description", description),
						Map.entry("price", price),
						Map.entry("city", city.getName()),
						Map.entry("cuisine", cuisine.getName())));
	}
	public void setPrice(Price price)
	{
		this.price = price;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setCuisine(Cuisine cuisine)
	{
		getCuisine().removeRestaurant(this);
		this.cuisine = cuisine;
		cuisine.addRestaurant(this);
	}

	public void setCity(City city)
	{
		getCity().removeRestaurant(this);
		this.city = city;
		city.addRestaurant(this);
	}

	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	public String getName()
	{
		return name;
	}

	public Price getPrice()
	{
		return price;
	}

	public String getDescription()
	{
		return description;
	}

	public Cuisine getCuisine()
	{
		if(cuisine == null) {
			cuisine = DBManager.session().queryForObject(Cuisine.class,
					"MATCH (c:Cuisine)<-[:SERVES]-(r:Restaurant) " +
					"WHERE r.name = $name " +
					"RETURN c",
					Map.ofEntries(Map.entry("name", name)));
			if(cuisine == null)
				return new Cuisine("N/D");
		}
		
		return cuisine;
	}

	public City getCity()
	{
		if (city == null) {
			city = DBManager.session().queryForObject(City.class,
					"MATCH (c:City)<-[:LOCATED]-(r:Restaurant) " +
					"WHERE r.name = $name " +
					"RETURN c",
					Map.ofEntries(Map.entry("name", name)));
				if(city == null)
					return new City("N/D", Double.NaN, Double.NaN);
		}
		return city;
	}

	public User getOwner()
	{
		return owner;
	}

	public List<User> getUsersWhoLike()
	{
		if (usersWhoLike == null) {
			Iterable<User> users = DBManager.session().query(User.class,
			"MATCH (c:City)<-[l:LOCATED]-(user:User)-[li:LIKES]->(r:Restaurant) " +
			"WHERE r.name = $name " +
			"RETURN (r)<-[li]-(user)-[l]->(c)",
			Map.ofEntries(Map.entry("name", name)));
			usersWhoLike = new ArrayList<User>();
			users.forEach(usersWhoLike::add);
		}
		return usersWhoLike;
	}

	public int getLikesCount()
	{
		getUsersWhoLike();
		return usersWhoLike == null ? 0 : usersWhoLike.size();
	}

	public boolean isLikedBy(User user)
	{
		List<User> likes = getUsersWhoLike();
		if(likes == null) return false;
		for (User u: likes)
			if (u.equals(user))
				return true;
		return false;
	}

	public void addLikedFrom(User user)
	{
		getUsersWhoLike().add(user);
	}

	public void removeLikedFrom(User user)
	{
		getUsersWhoLike().remove(user);
	}

	public static List<Restaurant> loadRestaurantsLikedBy(User user, String nameRegex, int page, int perPage)
	{
		if (nameRegex == null || nameRegex.isEmpty())
			return loadRestaurantsLikedBy(user,page,perPage);
		String regexFilter = "";
		regexFilter = "AND r.name =~ $regex ";
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[like:LIKES]->(r:Restaurant)<-[:OWNS]-(o:User) " +
			"WHERE u.username = $username " + regexFilter +
			"RETURN (r)--() " +
			"ORDER BY r.name " +
			"SKIP $skip " +
			"LIMIT $limit ",
			Map.ofEntries(Map.entry("username", user.getUsername()),
				Map.entry("regex", nameRegex),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)));
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		found.forEach(restaurants::add);
		return restaurants;
	}

	public static List<Restaurant> loadRestaurantsOwnedBy(User user, String nameRegex, int page, int perPage)
	{
		if (nameRegex == null || nameRegex.isEmpty())
			return loadRestaurantsOwnedBy(user,page,perPage);
		String regexFilter = "";
		regexFilter = "AND r.name =~ $regex ";
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (r:Restaurant)<-[:OWNS]-(u:User) " +
			"WHERE u.username = $username " + regexFilter +
			"RETURN (r)--() " +
			"ORDER BY r.name " +
			"SKIP $skip " +
			"LIMIT $limit ",
			Map.ofEntries(Map.entry("username", user.getUsername()),
				Map.entry("regex", nameRegex),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)));
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		found.forEach(restaurants::add);
		return restaurants;
	}

	public static List<Restaurant> loadRestaurantsOwnedBy(User user, int page, int perPage)
	{
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
				"MATCH (r:Restaurant)<-[own:OWNS]-(u:User) " +
				"WHERE u.username = $username " +
				"RETURN (r)--() " +
				"ORDER BY r.name " +
				"SKIP $skip " +
				"LIMIT $limit ",
				Map.ofEntries(Map.entry("username", user.getUsername()),
					Map.entry("skip", page*perPage),
					Map.entry("limit", perPage)));
			List<Restaurant> restaurants = new ArrayList<Restaurant>();
			found.forEach(restaurants::add);
			return restaurants;
	}

	public static List<Restaurant> loadRestaurantsLikedBy(User user, int page, int perPage)
	{
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[like:LIKES]->(r:Restaurant)<-[own:OWNS]-(o:User) " +
			"WHERE u.username = $username " +
			"RETURN (r)--() " +
			"ORDER BY r.name " +
			"SKIP $skip " +
			"LIMIT $limit ",
			Map.ofEntries(Map.entry("username", user.getUsername()),
				Map.entry("skip", page*perPage),
				Map.entry("limit", perPage)));
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		found.forEach(restaurants::add);
		return restaurants;
	}

	public int getCityRank()
	{
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[:LIKES]->(r:Restaurant)-[:LOCATED]->(city:City{name:$city}) "
			+ "RETURN r, count(u) as rank "
			+ "ORDER BY rank DESC, r.name ",
			Map.ofEntries(Map.entry("city", getCity().getName())));
		List<Restaurant> ranking = new ArrayList<Restaurant>();
		found.forEach(ranking::add);
		return ranking.indexOf(this) == -1 ? ranking.size() + 1 : ranking.indexOf(this) + 1 ;
	}

	public int getCuisineRank()
	{
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[:LIKES]->(r:Restaurant)-[:SERVES]->(cuisine:Cuisine{name:$cuisine}) "	
			+ "RETURN r, count(u) as rank "
			+ "ORDER BY rank DESC, r.name  ",
			Map.ofEntries(Map.entry("cuisine", getCuisine().getName())));
		List<Restaurant> ranking = new ArrayList<Restaurant>();
		found.forEach(ranking::add);
		return ranking.indexOf(this) == -1 ? ranking.size() + 1 : ranking.indexOf(this) + 1;
	}

	public int getCityCuisineRank()
	{
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (city:City{name:$city})<-[:LOCATED]-(r:Restaurant)-[:SERVES]->(cuisine:Cuisine{name:$cuisine})"
			+ ", (user:User)-[:LIKES]->(r) "
			+ "RETURN r, count(user) as rank "
			+ "ORDER BY rank DESC, r.name ",
			Map.ofEntries(Map.entry("cuisine", getCuisine().getName()),
					Map.entry("city", getCity().getName())));
		List<Restaurant> ranking = new ArrayList<Restaurant>();
		found.forEach(ranking::add);
		return ranking.indexOf(this) == -1 ? ranking.size() + 1 : ranking.indexOf(this) + 1;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if(!(o instanceof Restaurant))
			return false;
		Restaurant r = (Restaurant) o;
		return name.equals(r.name);
	}

	public static List<Restaurant> recommendRestaurant(User user, Cuisine cuisine, City city, int distance, LikesFrom depth, Price price,
			int page, int perPage)
	{
		Map<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("city", city.getName());
		parameters.put("distance", distance * 1000);
		parameters.put("depth", depth.ordinal()+1);
		parameters.put("price", price.getLowerValues());
		parameters.put("skip", page*perPage);
		parameters.put("limit", perPage);
		parameters.put("username", user.getUsername());

		String cuisineFilter = "";
		String depthFilter =  depth == LikesFrom.FRIENDS_OF_FRIENDS ? "*1..2" : "";
		if(cuisine != null) {
			cuisineFilter = "-[:SERVES]->(:Cuisine{name:$cuisine})";
			parameters.put("cuisine", cuisine.getName());
		}

		String query = "MATCH (ctarget:City{name:$city}), (c:City)<-[:LOCATED]-(r:Restaurant)" + cuisineFilter + ", " +
				"(me:User{username:$username})-[:FOLLOWS" + depthFilter + "]->(f:User)-[:LIKES]->(r) " +
				"WHERE f.username <> $username AND r.price IN $price " +
				"WITH ctarget, c, r, f, point({longitude: ctarget.longitude, latitude:ctarget.latitude}) as p1, " +
				"	point({longitude: c.longitude, latitude:c.latitude}) as p2 " +
				"WITH distance(p1,p2) as dist, r, f " +
				"WHERE dist <= $distance AND NOT EXISTS ((r)-[:LIKES|OWNS]-(:User{username:$username})) " +
				"RETURN (r)--(), count(DISTINCT f) as likes " +
				"ORDER BY likes DESC, r.name " +
				"SKIP $skip " +
				"LIMIT $limit ";

		Iterable<Restaurant> recommended = DBManager.session().query(Restaurant.class, query ,parameters);
		List<Restaurant> restaurants = new ArrayList<Restaurant>();
		recommended.forEach(restaurants::add);
		return restaurants;
	}
}
