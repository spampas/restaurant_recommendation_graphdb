package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class City
{
	@Id
	private String name;
	@Property
	private double latitude;
	@Property
	private double longitude;

	@Relationship(type = "LOCATED")
	private List<Restaurant> restaurants;
	@Relationship(type = "LOCATED")
	private List<User> users;
	@Relationship(type = "ROAD", direction = Relationship.UNDIRECTED)
	private List<Road> roads;

	public City()
	{
	}

	public City(String name, double latitude, double longitude, List<Road> roads)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.roads = roads;
	}

	public City(String name, double latitude, double longitude)
	{
		this(name, latitude, longitude, null);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public List<Restaurant> getRestaurants()
	{
		return restaurants;
	}

	public List<User> getUsers()
	{
		return users;
	}

	public List<Road> getRoads()
	{
		return roads;
	}

	public void addRestaurant(Restaurant restaurant)
	{
		if (restaurants == null)
			restaurants = new ArrayList<Restaurant>();
		restaurants.add(restaurant);
	}

	public void addUser(User user)
	{
		if (users == null)
			users = new ArrayList<User>();
		users.add(user);
	}

	public void addRoad(Road road)
	{
		if (roads == null)
			roads = new ArrayList<Road>();
		roads.add(road);
	}

	public void addRoad(City endCity, int distance)
	{
		addRoad(new Road(this, endCity, distance));
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}
}
