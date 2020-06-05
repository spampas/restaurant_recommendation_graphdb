package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class RestaurantBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty ownerName;
	private final SimpleObjectProperty<Cuisine> cuisine;
	private final SimpleObjectProperty<Price> price;
	private final SimpleStringProperty city;
	private final SimpleStringProperty description;

	public RestaurantBean(int id, String name, String ownerName, Cuisine cuisine,
		Price price, String city, String description)
	{
		super(id);
		this.name = new SimpleStringProperty(name);
		this.ownerName = new SimpleStringProperty(ownerName);
		this.cuisine = new SimpleObjectProperty<Cuisine>(cuisine);
		this.price = new SimpleObjectProperty<Price>(price);
		this.city = new SimpleStringProperty(city);
		this.description = new SimpleStringProperty(description);
	}
	
	public static RestaurantBean fromEntity(Restaurant restaurant)
	{
		return new RestaurantBean(restaurant.getId(), restaurant.getName(),
			restaurant.getOwnerName(), restaurant.getCuisine(), restaurant.getPrice(),
			restaurant.getCity(), restaurant.getDescription());
	}

	public Restaurant toEntity()
	{
		return new Restaurant(getId(), getName(), getOwnerName(), getCuisine(),
			getPrice(), getCity(), getDescription());
	}

	public String getOwnerName()
	{
		return ownerName.get();
	}

	public String getName()
	{
		return name.get();
	}

	public Cuisine getCuisine()
	{
		return cuisine.get();
	}

	public String getCity()
	{
		return city.get();
	}

	public String getDescription()
	{
		return description.get();
	}

	public Price getPrice()
	{
		return price.get();
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName.set(ownerName);
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public void setCuisine(Cuisine cuisine)
	{
		this.cuisine.set(cuisine);
	}

	public void setCity(String city)
	{
		this.city.set(city);
	}

	public void setDescription(String description)
	{
		this.description.set(description);
	}

	public void setPrice(Price price)
	{
		this.price.set(price);
	}

}