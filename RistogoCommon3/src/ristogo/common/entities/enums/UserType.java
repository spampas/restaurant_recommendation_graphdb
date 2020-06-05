package ristogo.common.entities.enums;

import java.util.logging.Logger;

public enum UserType
{
	CUSTOMER,
	OWNER;

	public String toString()
	{
		switch (this) {
		case CUSTOMER:
			return "Customer";
		case OWNER:
			return "Owner";
		default:
			Logger.getLogger(UserType.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}
}
