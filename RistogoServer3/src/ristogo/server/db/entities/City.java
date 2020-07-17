package ristogo.server.db.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import ristogo.server.db.DBManager;

@NodeEntity
public class City
{
	@Id
	private String name;
	private transient String oldName;
	@Property
	private double latitude;
	@Property
	private double longitude;

	@Relationship(type = "LOCATED", direction = Relationship.INCOMING)
	private List<Restaurant> restaurants;
	@Relationship(type = "LOCATED", direction = Relationship.INCOMING)
	private List<User> users;

	public City()
	{
	}

	public City(String name, double latitude, double longitude)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void setName(String name)
	{
		this.oldName = this.name;
		this.name = name;
	}

	public void save()
	{
		DBManager.session().query("MATCH (n:City{name:$oldName}) SET n = {name : $name, latitude : $latitude, longitude : $longitude}",
				Map.ofEntries(Map.entry("name", name),
						Map.entry("oldName", oldName),
						Map.entry("latitude", latitude),
						Map.entry("longitude", longitude)));
	}

	public String getName()
	{
		return name;
	}

	public List<Restaurant> getRestaurants()
	{
		if (restaurants == null) {
			Iterable<Restaurant> found = DBManager.session().query(Restaurant.class,
			"MATCH (r:Restaurant)-[l:LOCATED]->(c:City) " +
			"WHERE c.name = $name " +
			"RETURN (r)-[l]->(c)",
			Map.ofEntries(Map.entry("name", name)));
			restaurants = new ArrayList<Restaurant>();
			found.forEach(restaurants::add);
		}
		return restaurants;
	}

	public List<User> getUsers()
	{
		if (users == null) {
			Iterable<User> found = DBManager.session().query(User.class,
			"MATCH (user:User)-[l:LOCATED]->(c:City) " +
			"WHERE c.name = $name " +
			"RETURN (user)-[l]->(c)",
			Map.ofEntries(Map.entry("name", name)));
			users = new ArrayList<User>();
			found.forEach(users::add);
		}
		return users;
	}

	public void addRestaurant(Restaurant restaurant)
	{
		getRestaurants().add(restaurant);
	}

	public void removeRestaurant(Restaurant restaurant)
	{
		getRestaurants().remove(restaurant);
	}

	public void addUser(User user)
	{
		getUsers().add(user);
	}

	public void removeUser(User user)
	{
		getUsers().remove(user);
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

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
			return true;
		if (!(o instanceof City))
			return false;
		City c = (City)o;
		return name.equals(c.name);
	}

}
