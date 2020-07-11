package ristogo.common.net;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.List;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	protected final ActionRequest action;

	public RequestMessage(ActionRequest action, List<Entity> entities)
	{
		super(entities);
		this.action = action;
	}

	public RequestMessage(ActionRequest action,  Entity... entities)
	{
		this(action, Arrays.asList(entities));
	}

	public ActionRequest getAction()
	{
		return action;
	}

	public static RequestMessage receive(DataInputStream input)
	{
		return (RequestMessage)Message.receive(input);
	}

	public boolean isValid()
	{
		//TODO: Gestire azioni
		boolean hasOwner = false;
		boolean hasRestaurant = false;
		switch(action) {
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case LOGOUT:
		case REGISTER_USER:
		case REGISTER_RESTAURANT:
		case LIST_USERS:
		case LIST_FRIENDS:
		case FOLLOW_USER:
		case UNFOLLOW_USER:
		case DELETE_USER:
		case EDIT_RESTAURANT:
		case DELETE_RESTAURANT:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case LIST_RESTAURANTS:
			return (getEntityCount() == 0) || (getEntityCount() == 1 && getEntity() instanceof Restaurant);
		case GET_OWN_RESTAURANT:
			return getEntityCount() == 0;
		case PUT_LIKE_RESTAURANT:
		case REMOVE_LIKE_RESTAURANT:
		case GET_STATISTIC_RESTAURANT:
		case LIST_CUISINES:
		case ADD_CUISINE:
		case DELETE_CUISINE:
		case PUT_LIKE_CUISINE:
		case REMOVE_LIKE_CUISINE:
		case LIST_CITIES:
		case ADD_CITY:
		case DELETE_CITY:
		default:
			return false;
		}
	}
}
