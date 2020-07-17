package ristogo.common.net.entities.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public enum Price
{
	ECONOMIC,
	LOW,
	MIDDLE,
	HIGH,
	LUXURY;

	public String toString()
	{
		switch (this) {
		case ECONOMIC:
			return "Economic";
		case LOW:
			return "Low";
		case MIDDLE:
			return "Middle";
		case HIGH:
			return "High";
		case LUXURY:
			return "Luxury";
		default:
			Logger.getLogger(Price.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}

	public List<Price> getLowerValues()
	{
		List<Price> prices = new ArrayList<Price>();
		switch (this) {
		case LUXURY:
			prices.add(Price.LUXURY);
		case HIGH:
			prices.add(Price.HIGH);
		case MIDDLE:
			prices.add(Price.MIDDLE);
		case LOW:
			prices.add(Price.LOW);
		case ECONOMIC:
			prices.add(Price.ECONOMIC);
			break;
		default:
			Logger.getLogger(Price.class.getName()).severe("Invalid enum value.");
			return null;
		}
		return prices;
	}
}
