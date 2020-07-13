package ristogo.server.db.entities;

import java.util.Arrays;
import java.util.List;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "ROAD")
public class Road
{
	@Id @GeneratedValue
	private Long id;

	@StartNode
	private City startCity;
	@EndNode
	private City endCity;
	@Property
	private int distance;

	public Road()
	{
	}

	public Road(City startCity, City endCity, int distance)
	{
		this.startCity = startCity;
		this.endCity = endCity;
		if (distance < 0)
			throw new IllegalArgumentException("Distance must not be negative.");
		this.distance = distance;
	}

	public City getStartCity()
	{
		return startCity;
	}

	public City getEndCity()
	{
		return endCity;
	}

	public List<City> getConnections()
	{
		return Arrays.asList(startCity, endCity);
	}

	public int getDistance()
	{
		return distance;
	}

	public void setStartCity(City city)
	{
		this.startCity = city;
	}

	public void setEndCity(City city)
	{
		this.endCity = city;
	}

	public void setConnections(City startCity, City endCity)
	{
		setStartCity(startCity);
		setEndCity(endCity);
	}

	public void setDistance(int distance)
	{
		if (distance < 0)
			throw new IllegalArgumentException("Distance must not be negative.");
		this.distance = distance;
	}
}
