package ristogo.common.net.enums;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	REGISTER_USER,
	ADD_RESTAURANT,
	LIST_USERS,
	LIST_FOLLOWERS,
	LIST_FOLLOWING,
	FOLLOW_USER,
	UNFOLLOW_USER,
	DELETE_USER,
	LIST_RESTAURANTS,
	LIST_LIKED_RESTAURANTS,
	LIKE_RESTAURANT,
	UNLIKE_RESTAURANT,
	LIST_OWN_RESTAURANTS,
	GET_STATISTIC_RESTAURANT,
	EDIT_RESTAURANT,
	DELETE_RESTAURANT,
	LIST_CUISINES,
	ADD_CUISINE,
	DELETE_CUISINE,
	LIKE_CUISINE,
	UNLIKE_CUISINE,
	LIST_CITIES,
	ADD_CITY,
	DELETE_CITY,
	GET_USER,
	GET_RESTAURANT,
	RECOMMEND_RESTAURANT,
	RECOMMEND_USER,
	SET_CITY,
	GET_CITY,
	EDIT_CITY,
	EDIT_CUISINE,
	LIST_LIKED_CUISINES;

	public String toCamelCaseString()
	{
		char[] name = this.name().toLowerCase().toCharArray();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(name[0]));
		for (int i = 1; i < name.length; i++) {
			char c = name[i];
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				sb.append(c);
				continue;
			}
			c = name[i + 1];
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				sb.append(Character.toUpperCase(c));
				i++;
			}
		}
		return sb.toString();
	}
}
