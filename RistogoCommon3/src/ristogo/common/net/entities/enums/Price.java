package ristogo.common.net.entities.enums;

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
	
	public List<Price> getLowerThen() {
		switch(this) {
		case ECONOMIC:
			return Arrays.asList(Price.ECONOMIC);
		case LOW:
			return Arrays.asList(Price.ECONOMIC, Price.LOW);
		case MIDDLE:
			return Arrays.asList(Price.ECONOMIC, Price.LOW, Price.MIDDLE);
		case HIGH:
			return Arrays.asList(Price.ECONOMIC, Price.LOW, Price.MIDDLE, Price.HIGH);
		case LUXURY:
			return Arrays.asList(Price.ECONOMIC, Price.LOW, Price.MIDDLE, Price.HIGH, Price.LUXURY);
		default:
			Logger.getLogger(Price.class.getName()).severe("Invalid enum value.");
			return null;
		}
	
	}
}
