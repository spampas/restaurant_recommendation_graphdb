package ristogo.ui.graphics.beans;

import java.time.LocalDate;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.enums.ReservationTime;

public class ReservationBean extends EntityBean
{
	private final SimpleStringProperty userName;
	private final SimpleStringProperty restaurantName;
	private final SimpleObjectProperty<LocalDate> date;
	private final SimpleObjectProperty<ReservationTime> time;
	private final SimpleIntegerProperty seats;

	public ReservationBean(int id, String userName, String restaurantName,
		LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.userName = new SimpleStringProperty(userName);
		this.restaurantName = new SimpleStringProperty(restaurantName);
		this.date = new SimpleObjectProperty<LocalDate>(date);
		this.time = new SimpleObjectProperty<ReservationTime>(time);
		this.seats = new SimpleIntegerProperty(seats);

	}

	public static ReservationBean fromEntity(Reservation reservation)
	{
		return new ReservationBean(reservation.getId(), reservation.getUserName(),
			reservation.getRestaurantName(), reservation.getDate(),
			reservation.getTime(), reservation.getSeats());
	}

	public Reservation toEntity()
	{
		return new Reservation(getId(), getUserName(), getRestaurantName(),
			getDate(), getTime(), getSeats());
	}

	public LocalDate getDate()
	{
		return date.get();
	}

	public int getSeats()
	{
		return seats.get();
	}

	public ReservationTime getTime()
	{
		return time.get();
	}

	public String getUserName()
	{
		return userName.get();
	}

	public String getRestaurantName()
	{
		return restaurantName.get();
	}

	public void setDate(LocalDate date)
	{
		this.date.set(date);
	}

	public void setSeats(int seats)
	{
		this.seats.set(seats);
	}

	public void setTime(ReservationTime time)
	{
		this.time.set(time);
	}

	public void setUserName(String userName)
	{
		this.userName.set(userName);
	}

	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName.set(restaurantName);
	}
}