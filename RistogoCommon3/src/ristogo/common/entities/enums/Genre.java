package ristogo.common.entities.enums;

import java.util.logging.Logger;

public enum Genre
{
	PIZZA,
	JAPANESE,
	MEXICAN,
	ITALIAN,
	STEAKHOUSE;

	@Override
	public String toString()
	{
		switch (this) {
		case PIZZA:
			return "Pizza";
		case JAPANESE:
			return "Japanese";
		case MEXICAN:
			return "Mexican";
		case ITALIAN:
			return "Italian";
		case STEAKHOUSE:
			return "SteakHouse";
		default:
			Logger.getLogger(Genre.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}
}
