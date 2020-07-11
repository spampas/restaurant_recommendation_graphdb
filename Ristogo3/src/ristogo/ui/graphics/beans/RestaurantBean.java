package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Price;

public class RestaurantBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty ownerName;
	private final SimpleObjectProperty<Cuisine> cuisine;
	private final SimpleObjectProperty<Price> price;
	private final SimpleStringProperty city;
	private final SimpleDoubleProperty latitude;
	private final SimpleDoubleProperty longitude;
	private final SimpleStringProperty description;

	public RestaurantBean(int id, String name, String ownerName, Cuisine cuisine,
		Price price, String city, Double latitude, Double longitude, String description)
	{
		super(id);
		this.name = new SimpleStringProperty(name);
		this.ownerName = new SimpleStringProperty(ownerName);
		this.cuisine = new SimpleObjectProperty<Cuisine>(cuisine);
		this.price = new SimpleObjectProperty<Price>(price);
		this.latitude = new SimpleDoubleProperty(latitude);
		this.longitude = new SimpleDoubleProperty(longitude);
		this.city = new SimpleStringProperty(city);
		this.description = new SimpleStringProperty(description);
	}
	
	public static RestaurantBean fromEntity(Restaurant restaurant)
	{
		return new RestaurantBean(restaurant.getId(), restaurant.getName(), restaurant.getOwnerName(), restaurant.getCuisine(),
			restaurant.getPrice(), restaurant.getCity(), restaurant.getLatitude(), restaurant.getLongitude(), restaurant.getDescription());
	}

	public Restaurant toEntity()
	{
		return new Restaurant(getId(), getName(), getOwnerName(), getCuisine(),
			getPrice(), getCity(), getLatitude(), getLongitude(), getDescription());
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
	
	public Double getLatitude()
	{
		return latitude.get();
	}
	
	public Double getLongitude()
	{
		return longitude.get();
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

	public void setLatitude(Double latitude)
	{
		this.latitude.set(latitude);
	}
	
	public void setLongitude(Double longitude)
	{
		this.longitude.set(longitude);
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