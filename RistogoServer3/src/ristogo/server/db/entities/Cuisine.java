package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
		return usersWhoLike;
	}
}
