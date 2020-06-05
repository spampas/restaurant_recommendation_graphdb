package ristogo.server.storage.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ristogo.common.entities.Reservation;
import ristogo.common.entities.enums.ReservationTime;

/**
 * Represents a reservation JPA entity.
 */
@javax.persistence.Entity
@Table(name = "reservations")
public class Reservation_ extends Entity_
{
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userId", nullable=false)
	protected User_ user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurantId", nullable=false)
	protected Restaurant_ restaurant;

	@Column(name = "date", nullable=false)
	protected LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "time", nullable=false)
	protected ReservationTime time;

	@Column(name = "seats", nullable=false)
	protected int seats;

	/**
	 * Creates a new reservation.
	 */
	public Reservation_()
	{
		this(0, null, null, 0);
	}

	/**
	 * Creates a new reservation, with the specified fields.
	 * @param id Reservation's id.
	 * @param date Reservation's date.
	 * @param time Reservation's time.
	 * @param seats Reservation's requested seats.
	 */
	public Reservation_(int id, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.date = date;
		this.time = time;
		this.seats = seats;
	}

	/**
	 * Convert this entity to its corresponding common entity.
	 * @see ristogo.common.entities.Reservation
	 * @return An instance of the corresponding common entity.
	 */
	public Reservation toCommonEntity()
	{
		return new Reservation(getId(), getUser().getUsername(), getRestaurant().getName(), getDate(), getTime(), getSeats());
	}

	/**
	 * Merge a common entity into this by copying all its fields.
	 * @see ristogo.common.entities.Reservation
	 * @param restaurant The common entity from which to copy the fields.
	 * @return True if the merge succeeded; False otherwise.
	 */
	public boolean merge(Reservation reservation)
	{
		setTime(reservation.getTime());
		setDate(reservation.getDate());
		return setSeats(reservation.getSeats());
	}

	/**
	 * Sets the user that made this reservation.
	 * @param user The reservation's creator.
	 */
	public void setUser(User_ user)
	{
		this.user = user;
	}

	/**
	 * Gets the user that made this reservation.
	 * @return The reservation's creator.
	 */
	public User_ getUser()
	{
		return user;
	}

	/**
	 * Sets the restaurant for which this reservation is.
	 * @param restaurant The restaurant of this reservation.
	 */
	public void setRestaurant(Restaurant_ restaurant)
	{
		this.restaurant = restaurant;
	}

	/**
	 * Gets the restaurant for which this reservation is.
	 * @return The restaurant of this reservation.
	 */
	public Restaurant_ getRestaurant()
	{
		return restaurant;
	}

	/**
	 * Sets the date of this reservation.
	 * @param date Reservation's date.
	 */
	public void setDate(LocalDate date)
	{
		this.date = date;
	}

	/**
	 * Gets the date of this reservation.
	 * @return Reservation's date.
	 */
	public LocalDate getDate()
	{
		return date;
	}

	/**
	 * Sets the time of this reservation.
	 * @param time Reservation's time.
	 */
	public void setTime(ReservationTime time)
	{
		this.time = time;
	}

	/**
	 * Gets the time of this reservation.
	 * @return Reservation's time.
	 */
	public ReservationTime getTime()
	{
		return time;
	}

	/**
	 * Sets the requested number of seats for this reservation.
	 * @param seats The requested number of seats.
	 * @return True if the set succeeded; False if the number of seats is
	 * invalid.
	 */
	public boolean setSeats(int seats)
	{
		if (seats < 1)
			return false;
		this.seats = seats;
		return true;
	}

	/**
	 * Gets the requested number of seats for this reservation.
	 * @return The requested number of seats.
	 */
	public int getSeats()
	{
		return seats;
	}

	/**
	 * Checks whether this reservation is active.
	 * <p>
	 * Reservations are considered active if their date field is after or
	 * equal the current date.
	 * </p>
	 * @return
	 */
	public boolean isActive()
	{
		return date != null && !date.isBefore(LocalDate.now());
	}
}
