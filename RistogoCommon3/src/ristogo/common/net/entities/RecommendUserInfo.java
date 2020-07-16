package ristogo.common.net.entities;

public class RecommendUserInfo extends Entity
{
	private static final long serialVersionUID = 5699873032923359538L;

	private CuisineInfo cuisine;
	private int distance;
	private boolean airDistance;
	private CityInfo city;

	public RecommendUserInfo(CuisineInfo cuisine, int distance, boolean airDistance, CityInfo city)
	{
		this.cuisine = cuisine;
		this.distance = distance;
		this.airDistance = airDistance;
		this.city = city;
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

	public CityInfo getCity()
	{
		return city;
	}

	public void setCity(CityInfo city)
	{
		this.city = city;
	}
}
