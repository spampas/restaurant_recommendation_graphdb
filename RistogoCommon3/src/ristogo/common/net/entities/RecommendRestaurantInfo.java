package ristogo.common.net.entities;

import ristogo.common.net.entities.enums.LikesFrom;
import ristogo.common.net.entities.enums.Price;

public class RecommendRestaurantInfo extends Entity
{
	private static final long serialVersionUID = -8007294141944742604L;

	private CuisineInfo cuisine;
	private CityInfo city;
	private int distance;
	private boolean airDistance;
	private LikesFrom depth;
	private Price price;

	public RecommendRestaurantInfo(CuisineInfo cuisine, CityInfo city, int distance, boolean airDistance, LikesFrom depth, Price price)
	{
		this.cuisine = cuisine;
		this.city = city;
		this.distance = distance;
		this.airDistance = airDistance;
		this.depth = depth;
		this.price = price;
	}

	public CuisineInfo getCuisine()
	{
		return cuisine;
	}

	public void setCuisine(CuisineInfo cuisine)
	{
		this.cuisine = cuisine;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public boolean isAirDistance()
	{
		return airDistance;
	}

	public void setAirDistance(boolean airDistance)
	{
		this.airDistance = airDistance;
	}

	public LikesFrom getDepth()
	{
		return depth;
	}

	public void setDepth(LikesFrom depth)
	{
		this.depth = depth;
	}

	public Price getPrice()
	{
		return price;
	}

	public void setPrice(Price price)
	{
		this.price = price;
	}

	public CityInfo getCity()
	{
		return city;
	}

	public void setCity(CityInfo city)
	{
		this.city = city;
	}
}
