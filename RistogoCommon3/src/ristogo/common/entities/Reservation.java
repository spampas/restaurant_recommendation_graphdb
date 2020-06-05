package ristogo.common.entities;

import java.time.LocalDate;

import ristogo.common.entities.enums.ReservationTime;

public class Reservation extends Entity
{
	private static final long serialVersionUID = -1379979727099899831L;

	/**
	 * The username of the Customer who issued the reservation.
	 */
	protected String userName;
	/**
	 * The name of the restaurant where the reservation has been done.
	 */
	protected String restaurantName;
	/**
	 * Date of the reservation.
	 */
	protected LocalDate date;
	/**
	 * Time of the reservation.
	 */
	protected ReservationTime time;
	/**
	 * Number of seats reserved.
	 */
	protected int seats;

	/**
	 * Creates a reservation.
	 */
	public Reservation()
	{
		super();
	}

	/**
	 * Creates a reservation with the specified id.
	 * @param id
	 */
	public Reservation(int id)
	{
		super(id);
	}

	/**
	 * Creates a reservation with the specified fields.
	 * @param id Reservation's id.
	 * @param userName The username of the reservation's issuer.
	 * @param restaurantName The name of the restaurant related to this reservation.
	 * @param date Reservation's date.
	 * @param time Reservation's time.
	 * @param seats Reservation's requested seats.
	 */
	public Reservation(int id, String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.userName = userName;
		this.restaurantName = restaurantName;
		this.date = date;
		this.time = time;
		this.seats = seats;
	}

	/**
	 * Creates a reservation with the specified fields.
	 * @param userName The username of the reservation's issuer.
	 * @param restaurantName The name of the restaurant related to this reservation.
	 * @param date Reservation's date.
	 * @param time Reservation's time.
	 * @param seats Reservation's requested seats.
	 */
	public Reservation(String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		this(0, userName, restaurantName, date, time, seats);
	}

	/**
	 * Sets the username of the reservation's issuer.
	 * @param userName The username of the issuer.
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the username of the reservation's issuer.
	 * @return The username of the issuer.
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the restaurant's name.
	 * @param restaurantName The restaurant's name.
	 */
	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}

	/**
	 * Gets the restaurant's name.
	 * @return The restaurant's name.
	 */
	public String getRestaurantName()
	{
		return restaurantName;
	}

	/**
	 * Sets the date.
	 * @param date The date.
	 */
	public void setDate(LocalDate date)
	{
		this.date = date;
	}

	/**
	 * Gets the date.
	 * @return The date.
	 */
	public LocalDate getDate()
	{
		return date;
	}

	/**
	 * Sets the time.
	 * @param time The time.
	 */
	public void setTime(ReservationTime time)
	{
		this.time = time;
	}

	/**
	 * Gets the time.
	 * @return The time.
	 */
	public ReservationTime getTime()
	{
		return time;
	}

	/**
	 * Sets the number of seats.
	 * @param seats The number of seats.
	 */
	public void setSeats(int seats)
	{
		this.seats = seats;
	}

	/**
	 * Gets the number of seats.
	 * @return The number of seats.
	 */
	public int getSeats()
	{
		return seats;
	}

	/**
	 * Checks whether the reservation is active.
	 * @return True if active; False otherwise.
	 */
	public boolean isActive()
	{
		return date != null && !date.isBefore(LocalDate.now());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "Restaurant: " + getRestaurantName() + "\n" +
			"User: " + getUserName() + "\n" +
			"Date: " + getDate() + "\n" +
			"Time: " + getTime() + "\n" +
			"Seats: " + getSeats() + "\n";
	}
}
