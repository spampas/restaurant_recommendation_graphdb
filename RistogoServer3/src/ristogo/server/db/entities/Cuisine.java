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
}
