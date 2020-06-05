package ristogo.common.entities;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;

public class Restaurant extends Entity
{
	private static final long serialVersionUID = -2839130753004235292L;
	/**
	 * The name of the Owner.
	 */
	protected String ownerName;
	/**
	 * The name of the Restaurant.
	 */
	protected String name;
	/**
	 * The Genre of the restaurant.
	 */
	protected Cuisine cuisine;
	/**
	 * The Price target in a scale from ECONOMIC to LUXURY.
	 */
	protected Price price;
	/**
	 * The city where the restaurant is.
	 */
	protected String city;
	/**
	 * A description of the restaurant.
	 */
	protected String description;
	
	
	
	public Restaurant()
	{
		super();
	}

	/**
	 * Creates a restaurant, with the specified id.
	 * @param id Restaurant's id.
	 */

	public Restaurant(int id)
	{
		super(id);
	}

	/**
	 * Creates the default restaurant associated with a new Owner.
	 * @param ownerName The username of the owner.
	 */
	public Restaurant(String ownerName)
	{
		this(0, ownerName + "'s Restaurant", ownerName, null, null, null, null);
	}

	/**
	 * Creates a restaurant with the specified fields.
	 * @param id Restaurant's id.
	 * @param name Restaurant's name.
	 * @param ownerName username of the owner of the restaurant.
	 * @param genre Restaurant's genre.
	 * @param price Restaurant's price.
	 * @param city Restaurant's city.
	 * @param address Restaurant's address.
	 * @param description Restaurant's description.
	 * @param seats Restaurant's total number of seats.
	 * @param openingHours Restaurant's opening hours.
	 */
	public Restaurant(int id, String name, String ownerName, Cuisine genre, Price price, String city,  String description)
	{

		super(id);
		this.name = name;
		this.ownerName = ownerName;
		this.cuisine = cuisine;
		this.price = price;
		this.city = city;
		this.description = description;
	}

	/**
	 * Sets the username of the owner of this restaurant.
	 * @param ownerName The username of the owner.
	 */
	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	/**
	 * Gets the username of the owner of this restaurant.
	 * @return The username of the owner.
	 */
	public String getOwnerName()
	{
		return ownerName;
	}

	/**
	 * Sets the name of the restaurant.
	 * @param name Restaurant's name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the name of the restaurant.
	 * @return Restaurant's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * FGets the genre.
	 * @return The genre.
	 */
	public Cuisine getGenre()
	{
		return cuisine;
	}

	/**
	 * Sets the genre.
	 * @param genre The genre.
	 */
	public void setGenre(Cuisine cuisine)
	{
		this.cuisine = cuisine;
	}

	/**
	 * Gets the price.
	 * @return The price.
	 */
	public Price getPrice()
	{
		return price;
	}

	/**
	 * Sets the price.
	 * @param price The price.
	 */
	public void setPrice(Price price)
	{
		this.price = price;
	}

	/**
	 * Gets the city.
	 * @return The city.
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Sets the city.
	 * @param city The city.
	 */
	public void setCity(String city)
	{
		this.city = city;
	}
	/**
	 * Gets the description.
	 * @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description.
	 * @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Name: " + getName() + "\n" +
			"Owner: " + getOwnerName() + "\n" +
			"Genre: " + fieldToString(getGenre()) + "\n" +
			"Price: " + fieldToString(getPrice()) + "\n" +
			"City: " + fieldToString(getCity()) + "\n" +
			"Description: " + fieldToString(getDescription()) + "\n";
	}

	private String fieldToString(Object field)
	{
		return field == null ? "<NOT-SET>" : field.toString();
	}
}
