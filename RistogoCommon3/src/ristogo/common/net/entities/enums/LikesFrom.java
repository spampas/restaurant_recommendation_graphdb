package ristogo.common.net.entities.enums;

import java.util.logging.Logger;

public enum LikesFrom
{
	FRIENDS,
	FRIENDS_OF_FRIENDS;

	public String toString()
	{
		switch (this) {
		case FRIENDS:
			return "Users you follow";
		case FRIENDS_OF_FRIENDS:
			return "Users followed by users you follow";
		default:
			Logger.getLogger(Price.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}
}
