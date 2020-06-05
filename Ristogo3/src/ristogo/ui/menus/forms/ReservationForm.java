package ristogo.ui.menus.forms;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.ui.Console;

public class ReservationForm extends TextForm
{
	protected Restaurant restaurant;

	public ReservationForm(Restaurant restaurant)
	{
		super("Reserve a table");
		this.restaurant = restaurant;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Date", LocalDate.now().toString(), this::validateFutureDate));
		fields.add(new ChoiceFormField<ReservationTime>("Time", ReservationTime.class, this::validateReservationTime));
		fields.add(new FormField("Seats", this::validateSeats));
		return fields;
	}

	private boolean validateSeats(String value)
	{
		if (!validatePositiveInteger(value))
			return false;
		int seats = Integer.parseInt(value);
		if (seats > restaurant.getSeats()) {
			Console.println("The selected restaurant does not have enough available seats for this reservation.");
			return false;
		}
		return true;
	}

	private boolean validateReservationTime(ReservationTime reservationTime)
	{
		OpeningHours oh = restaurant.getOpeningHours();
		switch (oh) {
		case DINNER:
		case LUNCH:
			if (reservationTime.toOpeningHours() != oh) {
				Console.println("The selected restaurant does not allow reservations for " + reservationTime + ".");
				return false;
			}
		default:
			return true;
		}
	}
}
