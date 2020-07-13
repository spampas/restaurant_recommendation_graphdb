package ristogo.common.net.entities;

import ristogo.common.net.entities.enums.Price;

public class RestaurantInfo extends Entity
{
	private static final long serialVersionUID = -2839130753004235292L;

	protected UserInfo owner;
	protected String name;
	protected CuisineInfo cuisine;
	protected Price price;
	protected CityInfo city;
	protected String description;
	protected boolean liked;

	public RestaurantInfo(String name, UserInfo owner, CuisineInfo cuisine, Price price, CityInfo city, String description, boolean liked)
	{
		this.name = name;
		this.owner = owner;
		this.cuisine = cuisine;
		this.price = price;
		this.city = city;
		this.description = description;
		this.liked = liked;
	}

	public RestaurantInfo(String name, UserInfo owner, CuisineInfo cuisine, Price price, CityInfo city, String description)
	{
		this(name, owner, cuisine, price, city, description, false);
	}

	public RestaurantInfo(String name, CuisineInfo cuisine, Price price, CityInfo city, String description)
	{
		this(name, null, cuisine, price, city, description);
	}

	public RestaurantInfo(String name, UserInfo owner, CuisineInfo cuisine, Price price, CityInfo city)
	{
		this(name, owner, cuisine, price, city, null);
	}

	public RestaurantInfo(String name, CuisineInfo cuisine, Price price, CityInfo city)
	{
		this(name, null, cuisine, price, city, null);
	}

	public void setOwner(UserInfo owner)
	{
		this.owner = owner;
	}

	public UserInfo getOwner()
	{
		return owner;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public CuisineInfo getCuisine()
	{
		return cuisine;
	}

	public void setCuisine(CuisineInfo cuisine)
	{
		this.cuisine = cuisine;
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setLiked(boolean value)
	{
		this.liked = value;
	}

	public boolean isLiked()
	{
		return liked;
	}
}
