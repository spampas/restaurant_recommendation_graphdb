package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

/**
 * Represents a restaurant JPA entity.
 */
@javax.persistence.Entity
@Table(name="restaurants")
@DynamicUpdate
public class Restaurant_ extends Entity_
{
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ownerId", nullable=false)
	protected User_ owner;

	@Column(name="name", length=45, nullable=false)
	protected String name;

	@Enumerated(EnumType.STRING)
	@Column(name="genre", nullable=true)
	protected Genre genre;

	@Enumerated(EnumType.STRING)
	@Column(name="price", nullable=true)
	protected Price price;

	@Column(name="city", length=32, nullable=true)
	protected String city;

	@Column(name="address", length=32, nullable=true)
	protected String address;

	@Column(name="description", nullable=true)
	protected String description;

	@Column(name="seats", nullable=false)
	protected int seats;

	@Enumerated(EnumType.STRING)
	@Column(name="openingHours", nullable=false)
	protected OpeningHours openingHours;

	/**
	 * Restaurant's active reservations.
	 * <p>
	 * Reservations are considered active if their date field is after or
	 * equal the current date.
	 * </p><p>
	 * The list of active reservation is initially set to empty. When using
	 * JPA (Hibernate), this field will be initialized when it's accessed
	 * through its getter method; when using LevelDB, the list of active
	 * reservations must be read directly from db (a wrapper method exists
	 * in UserManager for this purpose).
	 * </p>
	 */
	@OneToMany(mappedBy="restaurant", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();

	/**
	 * Creates a new restaurant.
	 */
	public Restaurant_()
	{
		this(0, null, null, null, null, null, null, 0, OpeningHours.BOTH);
	}

	/**
	 * Creates a new restaurant, with the specified fields.
	 * @param id Restaurant's id.
	 * @param name Restaurant's name.
	 * @param genre Restaurant's genre.
	 * @param price Restaurant's price.
	 * @param city Restaurant's city.
	 * @param address Restaurant's address.
	 * @param description Restaurant's description.
	 * @param seats Restaurant's total number of seats.
	 * @param openingHours Restaurant's opening hours.
	 */
	public Restaurant_(int id, String name, Genre genre, Price price, String city, String address, String description, int seats, OpeningHours openingHours)
	{
		super(id);
		this.name = name;
		this.genre = genre;
		this.price = price;
		this.city = city;
		this.address = address;
		this.description = description;
		this.seats = seats;
		this.openingHours = openingHours;
	}

	/**
	 * Convert this entity to its corresponding common entity.
	 * @see ristogo.common.entities.Restaurant
	 * @return An instance of the corresponding common entity.
	 */
	public Restaurant toCommonEntity()
	{
		return new Restaurant(getId(), getName(), getOwner().getUsername(), getGenre(), getPrice(), getCity(), getAddress(), getDescription(), getSeats(), getOpeningHours());
	}

	/**
	 * Merge a common entity into this by copying all its fields.
	 * @see ristogo.common.entities.Restaurant
	 * @param restaurant The common entity from which to copy the fields.
	 * @return True if the merge succeeded; False otherwise.
	 */
	public boolean merge(Restaurant restaurant)
	{
		setGenre(restaurant.getGenre());
		setPrice(restaurant.getPrice());
		setDescription(restaurant.getDescription());
		setOpeningHours(restaurant.getOpeningHours());
		return setName(restaurant.getName()) && setCity(restaurant.getCity()) &&
			setAddress(restaurant.getAddress()) && setSeats(restaurant.getSeats());
	}

	/**
	 * Sets the owner of this restaurant.
	 * @param owner The new owner.
	 */
	public void setOwner(User_ owner)
	{
		this.owner = owner;
	}

	/**
	 * Gets the owner of this restaurant.
	 * @return The owner.
	 */
	public User_ getOwner()
	{
		return owner;
	}

	/**
	 * Sets the name of this restaurant.
	 * @param name The new name of the restaurant.
	 * @return True if the set succeeded; False if the name is invalid.
	 */
	public boolean setName(String name)
	{
		if (name == null || name.isBlank() || name.length() > 45)
			return false;
		this.name = name;
		return true;
	}

	/**
	 * Gets the name of this restaurant.
	 * @return The name of the restaurant.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the genre of this restaurant.
	 * @return The genre of this restaurant.
	 */
	public Genre getGenre()
	{
		return genre;
	}

	/**
	 * Sets the genre of this restaurant.
	 * @param genre The new genre of this restaurant.
	 */
	public void setGenre(Genre genre)
	{
		this.genre = genre;
	}

	/**
	 * Get the price of this restaurant.
	 * @return The price of this restaurant.
	 */
	public Price getPrice()
	{
		return price;
	}

	/**
	 * Sets the price of this restaurant.
	 * @param price The new price of this restaurant.
	 */
	public void setPrice(Price price)
	{
		this.price = price;
	}

	/**
	 * Gets the city of this restaurant.
	 * @return The city of this restaurant.
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Sets the city of this restaurant.
	 * @param city The new city of this restaurant.
	 * @return True if the set succeeded; False if the city is invalid.
	 */
	public boolean setCity(String city)
	{
		if (city != null && city.length() > 32)
			return false;
		this.city = city;
		return true;
	}

	/**
	 * Gets the address of this restaurant.
	 * @return The address of this restaurant.
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * Sets the address of this restaurant.
	 * @param address The new address of this restaurant.
	 * @return True if the set succeeded; False if the address is invalid.
	 */
	public boolean setAddress(String address)
	{
		if (address != null && address.length() > 32)
			return false;
		this.address = address;
		return true;
	}

	/**
	 * Gets the description of this restaurant.
	 * @return The description of this restaurant.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of this restaurant.
	 * @param description The new description of this restaurant.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the total number of seats of this restaurant.
	 * @return The total number of seats of this restaurant.
	 */
	public int getSeats()
	{
		return seats;
	}

	/**
	 * Sets the total number of seats of this restaurant.
	 * @param seats The new total number of seats of this restaurant.
	 * @return True if the set succeeded; False if the number of seats is
	 * invalid.
	 */
	public boolean setSeats(int seats)
	{
		if (seats < 0)
			return false;
		this.seats = seats;
		return true;
	}

	/**
	 * Gets the opening hours of this restaurant.
	 * @return The opening hours of this restaurant.
	 */
	public OpeningHours getOpeningHours()
	{
		return openingHours;
	}

	/**
	 * Sets the opening hours of this restaurant.
	 * @param openingHours The new opening hours of this restaurant.
	 */
	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours = openingHours;
	}

	/**
	 * Returns the list of active (i.e. with a present or future date)
	 * reservations.
	 * <p>
	 * NOTE: The activeReservations field may not be initialized if using
	 * LevelDB (db must be read manually).
	 * </p>
	 * @return The list of active reservations.
	 */
	public List<Reservation_> getActiveReservations()
	{
		return activeReservations;
	}
}
