package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class RestaurantBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty ownerName;
	private final SimpleObjectProperty<Genre> genre;
	private final SimpleObjectProperty<Price> price;
	private final SimpleStringProperty city;
	private final SimpleStringProperty address;
	private final SimpleStringProperty description;
	private final SimpleIntegerProperty seats;
	private final SimpleObjectProperty<OpeningHours> openingHours;

	public RestaurantBean(int id, String name, String ownerName, Genre genre,
		Price price, String city, String address, String description,
		int seats, OpeningHours openingHours)
	{
		super(id);
		this.name = new SimpleStringProperty(name);
		this.ownerName = new SimpleStringProperty(ownerName);
		this.genre = new SimpleObjectProperty<Genre>(genre);
		this.price = new SimpleObjectProperty<Price>(price);
		this.city = new SimpleStringProperty(city);
		this.address = new SimpleStringProperty(address);
		this.description = new SimpleStringProperty(description);
		this.seats = new SimpleIntegerProperty(seats);
		this.openingHours = new SimpleObjectProperty<OpeningHours>(openingHours);
	}

	public static RestaurantBean fromEntity(Restaurant restaurant)
	{
		return new RestaurantBean(restaurant.getId(), restaurant.getName(),
			restaurant.getOwnerName(), restaurant.getGenre(), restaurant.getPrice(),
			restaurant.getCity(), restaurant.getAddress(),
			restaurant.getDescription(), restaurant.getSeats(),
			restaurant.getOpeningHours());
	}

	public Restaurant toEntity()
	{
		return new Restaurant(getId(), getName(), getOwnerName(), getGenre(),
			getPrice(), getCity(), getAddress(), getDescription(),
			getSeats(), getOpeningHours());
	}

	public String getOwnerName()
	{
		return ownerName.get();
	}

	public String getName()
	{
		return name.get();
	}

	public Genre getGenre()
	{
		return genre.get();
	}

	public int getSeats()
	{
		return seats.get();
	}

	public String getCity()
	{
		return city.get();
	}

	public String getAddress()
	{
		return address.get();
	}

	public String getDescription()
	{
		return description.get();
	}

	public Price getPrice()
	{
		return price.get();
	}

	public OpeningHours getOpeningHours()
	{
		return openingHours.get();
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName.set(ownerName);
	}

	public void setName(String name)
	{
		this.name.set(name);
	}

	public void setGenre(Genre genre)
	{
		this.genre.set(genre);
	}

	public void setSeats(int seats)
	{
		this.seats.set(seats);
	}

	public void setCity(String city)
	{
		this.city.set(city);
	}

	public void setAddress(String address)
	{
		this.address.set(address);
	}

	public void setDescription(String description)
	{
		this.description.set(description);
	}

	public void setPrice(Price price)
	{
		this.price.set(price);
	}

	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours.set(openingHours);
	}
}