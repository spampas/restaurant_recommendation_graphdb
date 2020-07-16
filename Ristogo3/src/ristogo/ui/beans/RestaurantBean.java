package ristogo.ui.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.net.entities.enums.Price;

public class RestaurantBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty cuisine;
	private final SimpleObjectProperty<Price> price;
	private final SimpleStringProperty city;
	private final SimpleBooleanProperty liked;

	public RestaurantBean(String name, String cuisine,
		Price price, String city, boolean liked)
	{
		this.name = new SimpleStringProperty(name);
		this.cuisine = new SimpleStringProperty(cuisine);
		this.price = new SimpleObjectProperty<Price>(price);
		this.city = new SimpleStringProperty(city);
		this.liked = new SimpleBooleanProperty(liked);
	}

	public RestaurantBean(String name, String cuisine,
		Price price, String city)
	{
		this(name, cuisine, price, city, false);
	}

	public String getName()
	{
		return name.get();
	}

	public String getCity()
	{
		return city.get();
	}

	public Price getPrice()
	{
		return price.get();
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public String getCuisine()
	{
		return cuisine.get();
	}

	public void setCuisine(String cuisine)
	{
		this.cuisine.set(cuisine);
	}

	public void setCity(String city)
	{
		this.city.set(city);
	}

	public void setPrice(Price price)
	{
		this.price.set(price);
	}

	public void setLiked(boolean liked)
	{
		this.liked.set(liked);
	}

	public boolean isLiked()
	{
		return liked.get();
	}

}