package ristogo.common.entities.enums;

import java.util.logging.Logger;

public enum ReservationTime
{
	LUNCH,
	DINNER;

	public String toString()
	{
		switch (this) {
		case LUNCH:
			return "Lunch";
		case DINNER:
			return "Dinner";
		default:
			Logger.getLogger(ReservationTime.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}

	public OpeningHours toOpeningHours()
	{
		return OpeningHours.valueOf(this.name());
	}
}
