package ristogo.common.net.enums;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN, 
	LOGOUT,
	REGISTER_USER,
	ADD_RESTAURANT, // 
	LIST_USERS, 
	LIST_FOLLOWERS,
	LIST_FOLLOWING,
	FOLLOW_USER,
	UNFOLLOW_USER,
	DELETE_USER,//TODO CHECK
	LIST_RESTAURANTS,// TODO CHECK
	LIST_LIKED_RESTAURANTS,//TODO CHECK
	LIKE_RESTAURANT,//TODO CJECK
	UNLIKE_RESTAURANT, //TODO CHECK
	LIST_OWN_RESTAURANTS, //TODO CHECK
	GET_STATISTIC_RESTAURANT, //
	EDIT_RESTAURANT, //TODO CHECK
	DELETE_RESTAURANT, //TODO CHECK
	LIST_CUISINES, //TODO CHECK
	ADD_CUISINE, //TODO CHECK
	DELETE_CUISINE, //TODO CHECK
	LIKE_CUISINE, //
	UNLIKE_CUISINE, //
	LIST_CITIES, //TODO CHECK
 	ADD_CITY, //TODO CHECK
	DELETE_CITY, //TODO CHECK
	GET_USER, //UTENTE CITTA CUCINE SE SEGUO
	GET_RESTAURANT,//NOME PREZZO CITTA CUCINA DESCRIZIONE SE LIKE 
	RECOMMEND_RESTAURANT,
	RECOMMEND_USER,
	SET_CITY, //TODO
	GET_CITY; //TODO

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
