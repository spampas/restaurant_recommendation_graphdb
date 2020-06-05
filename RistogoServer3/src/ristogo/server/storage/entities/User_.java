package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.UserType;

/**
 * Represents a JPA user entity (Customer or Owner).
 */
@javax.persistence.Entity
@Table(name="users")
public class User_ extends Entity_
{
	@Column(name="username", length=32, nullable=false, unique=true)
	protected String username;

	/**
	 * User's password hash.
	 */
	@Column(name="password", length=32, nullable=false)
	protected String password;

	/**
	 * User's active reservations.
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
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();

	/**
	 * User's restaurant. This is null if it is not an Owner. When using
	 * JPA (Hibernate), this field will be initialized when it's accessed
	 * through its getter method; when using LevelDB, the restaurant must be
	 * read directly from db (a wrapper method exists in RestaurantManager
	 * for this purpose).
	 */
	@OneToOne(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected Restaurant_ restaurant;

	/**
	 * Creates a new user, with empty username and password hash.
	 */
	public User_()
	{
		this("", "");
	}

	/**
	 * Creates a new user, with the specified username and password hash.
	 * @param username User's username.
	 * @param passwordHash User's password hash.
	 */
	public User_(String username, String passwordHash)
	{
		this(0, username, passwordHash);
	}

	/**
	 * Creates a new user, with the specified id, username and password hash.
	 * @param id User's id.
	 * @param username User's username.
	 * @param passwordHash User's password hash.
	 */
	public User_(int id, String username, String passwordHash)
	{
		super(id);
		this.username = username;
		this.password = passwordHash;
	}

	/**
	 * Convert this entity to its corresponding common entity, trying to
	 * determine automatically which type of user is this (Customer or
	 * Owner).
	 * @see ristogo.common.entities.User
	 * @see ristogo.common.entities.Customer
	 * @see ristogo.common.entities.Owner
	 * @return An instance of the corresponding common entity.
	 */
	public User toCommonEntity()
	{
		return toCommonEntity(isOwner() ? UserType.OWNER : UserType.CUSTOMER);
	}

	/**
	 * Convert this entity to the specified common entity.
	 * @see ristogo.common.entities.Customer
	 * @see ristogo.common.entitties.Owner
	 * @param type Specifies whether the returned entity must be a Customer
	 * or an Owner.
	 * @return An instance of the specified common entity.
	 */
	public User toCommonEntity(UserType type)
	{
		switch (type) {
		case OWNER:
			return new Owner(getId(), getUsername());
		case CUSTOMER:
			return new Customer(getId(), getUsername());
		default:
			Logger.getLogger(User_.class.getName()).warning("Invalid UserType " + type + ".");
			return null;
		}

	}

	/**
	 * Merge a common entity into this by copying all its fields.
	 * @see ristogo.common.entities.User
	 * @param user The common entity from which to copy the fields.
	 * @return True if the merge succeeded; False otherwise.
	 */
	public boolean merge(User user)
	{
		return setUsername(user.getUsername()) &&
			setPassword(user.getPasswordHash());
	}

	/**
	 * Sets the user's username.
	 * @param username The user's username.
	 * @return True if the set succeeded; False if the username is invalid.
	 */
	public boolean setUsername(String username)
	{
		if (!User.validateUsername(username)) {
			Logger.getLogger(User_.class.getName()).warning("User " + getId() + " has an invalid username.");
			return false;
		}
		this.username = username;
		return true;
	}

	/**
	 * Sets the user's password hash.
	 * @param passwordHash The user's password hash.
	 * @return True if the set succeeded; False if the password hash is
	 * invalid.
	 */
	public boolean setPassword(String passwordHash)
	{
		if (!User.validatePasswordHash(passwordHash)) {
			Logger.getLogger(User_.class.getName()).warning("User " + username + " has an invalid password hash.");
			return false;
		}
		this.password = passwordHash;
		return true;
	}

	/**
	 * Gets the user's username.
	 * @return The user's username.
	 */
	public String getUsername()
	{
		return this.username;
	}

	/**
	 * Gets the user's password hash.
	 * @return The user's password hash.
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * Checks whether the user is an Owner or not.
	 * <p>
	 * NOTE: The restaurant field may not be initialized if using LevelDB
	 * (db must be read manually).
	 * </p>
	 * @return True if the user is an Owner (i.e. has a restaurant); False
	 * otherwise.
	 */
	public boolean isOwner()
	{
		return restaurant != null;
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

	/**
	 * Gets the user's restaurant.
	 * <p>
	 * NOTE: The restaurant field may not be initialized if using LevelDB
	 * (db must be read manually).
	 * </p>
	 * @return The user's restaurant.
	 */
	public Restaurant_ getRestaurant()
	{
		return restaurant;
	}

	/**
	 * Checks whether the user's own the specified restaurant.
	 * <p>
	 * NOTE: The restaurant field may not be initialized if using LevelDB
	 * (db must be read manually).
	 * </p>
	 * @param restaurantId The id of the restaurant to check.
	 * @return True if the user's own the restaurant with the specified id;
	 * False otherwise.
	 */
	public boolean hasRestaurant(int restaurantId)
	{
		return (restaurant == null) ? false : restaurant.getId() == restaurantId;
	}

	/**
	 * Checks whether the user's own the specified restaurant.
	 * @param restaurant The restaurant to check.
	 * @return True if the user's own the specified restaurant; False
	 * otherwise.
	 */
	public boolean hasRestaurant(Restaurant_ restaurant)
	{
		return hasRestaurant(restaurant.getId());
	}

	/**
	 * Checks whether the user's own the specified reservation.
	 * @param reservationId The id of the reservation to check.
	 * @return True if the user's own the reservation with the specified id
	 * and the reservation is active; False otherwise.
	 */
	public boolean hasReservation(int reservationId)
	{
		for (Reservation_ reservation: activeReservations)
			if (reservation.getId() == reservationId)
				return true;
		return false;
	}

	/**
	 * Checks whether the user's own the specified reservation.
	 * @param reservation The reservation to check.
	 * @return True if the user's own the specified reservation and the
	 * reservation is active; False otherwise.
	 */
	public boolean hasReservation(Reservation_ reservation)
	{
		return hasReservation(reservation.getId());
	}
}
