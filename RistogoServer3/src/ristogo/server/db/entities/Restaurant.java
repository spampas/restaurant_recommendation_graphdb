package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import ristogo.common.net.entities.enums.Price;
import ristogo.server.db.DBManager;
import ristogo.server.db.entities.annotations.PreDelete;

@NodeEntity
public class Restaurant
{
	@Id
	private String name;
	@Property
	private Price price;
	@Property
	private String description;

	@Relationship(type = "SERVES")
	private Cuisine cuisine;
	@Relationship(type = "LOCATED_IN")
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
		this.name = name;
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
		this.cuisine = cuisine;
	}

	public void setCity(City city)
	{
		this.city = city;
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
		return cuisine;
	}

	public City getCity()
	{
		return city;
	}

	public User getOwner()
	{
		return owner;
	}

	public List<User> getUsersWhoLike()
	{
		return usersWhoLike;
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

	public static List<Restaurant> loadRestaurantsLikedBy(User user, String nameRegex, int page, int perPage)
	{
		if (nameRegex == null || nameRegex.isEmpty() )
			 return loadRestaurantsLikedBy(user,page,perPage);
		String regexFilter = "";
		regexFilter = "AND r.name =~ $regex ";
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[:LIKES]->(r:Restaurant)<-[:OWNS]-(o:User) " +
			"OPTIONAL MATCH (c:Cuisine)<-[:SERVES]-(r)-[:LOCATED]->(ci:City) " +
			"WHERE u.username = $username " + regexFilter +
			"RETURN u, r, o, c, ci " +
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
	
	public static List<Restaurant> loadRestaurantsLikedBy(User user, int page, int perPage)
	{
		
		Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (u:User)-[:LIKES]->(r:Restaurant)<-[:OWNS]-(o:User) " +
			"OPTIONAL MATCH (c:Cuisine)<-[:SERVES]-(r)-[:LOCATED]->(ci:City) " +
			"WHERE u.username = $username " +
			"RETURN u, r, o, c, ci " +
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

	@PreDelete
	private void preDelete()
	{
	}
}
