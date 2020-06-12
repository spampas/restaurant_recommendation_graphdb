package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;

public class RestaurantBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty ownerName;
	private final SimpleObjectProperty<Cuisine> cuisine;
	private final SimpleObjectProperty<Price> price;
	private final SimpleStringProperty state;
	private final SimpleStringProperty country;
	private final SimpleStringProperty city;
	private final SimpleStringProperty description;

	public RestaurantBean(int id, String name, String ownerName, Cuisine cuisine,
		Price price, String state, String country, String city, String description)
	{
		super(id);
		this.name = new SimpleStringProperty(name);
		this.ownerName = new SimpleStringProperty(ownerName);
		this.cuisine = new SimpleObjectProperty<Cuisine>(cuisine);
		this.price = new SimpleObjectProperty<Price>(price);
		this.state = new SimpleStringProperty(state);
		this.country = new SimpleStringProperty(country);
		this.city = new SimpleStringProperty(city);
		this.description = new SimpleStringProperty(description);
	}
	
	public static RestaurantBean fromEntity(Restaurant restaurant)
	{
		return new RestaurantBean(restaurant.getId(), restaurant.getName(), restaurant.getOwnerName(), restaurant.getCuisine(),
			restaurant.getPrice(), restaurant.getState(), restaurant.getCountry(), restaurant.getCity(), restaurant.getDescription());
	}

	public Restaurant toEntity()
	{
		return new Restaurant(getId(), getName(), getOwnerName(), getCuisine(),
			getPrice(), getState(), getCountry(), getCity(), getDescription());
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
	
	public String getState()
	{
		return state.get();
	}
	
	public String getCountry()
	{
		return country.get();
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

	public void setState(String state)
	{
		this.state.set(state);
	}
	
	public void setCountry(String country)
	{
		this.country.set(country);
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