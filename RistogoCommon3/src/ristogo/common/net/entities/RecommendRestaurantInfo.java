package ristogo.common.net.entities;

import ristogo.common.net.entities.enums.Price;

public class RecommendRestaurantInfo extends Entity {
	private CuisineInfo cuisine;
	private int distance;
	private boolean airDistance;
	private int depth;
	private Price price;
	
	public RecommendRestaurantInfo(CuisineInfo cuisine, int distance, boolean airDistance, int depth, Price price ) {
		this.cuisine = cuisine;
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

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
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
}
