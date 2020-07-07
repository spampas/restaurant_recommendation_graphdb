package ristogo.common.net;

import java.util.Arrays;
import java.util.List;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

/**
 * Represents a request message, sent from the client to the server.
 */
public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	protected final ActionRequest action;

	/**
	 * Creates a new request message, with optional entities attached.
	 * @param action The type of action requested.
	 * @param entities The list of entities to attach.
	 */
	
	public RequestMessage(ActionRequest action, List<Entity> entities)
	{
		super(entities);
		this.action = action;
	}

	public RequestMessage(ActionRequest action,  Entity... entities)
	{
		this(action, Arrays.asList(entities));
	}


	/**
	 * Returns the type of action requested.
	 * @return The type of action.
	 */
	public ActionRequest getAction()
	{
		return action;
	}

	/**
	 * Checks whether this message is valid (properly formed).
	 * @return True if valid; False otherwise.
	 */
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
		case LIST_CITIES:
		case ADD_CITY:
		case DELETE_CITY:
		default:
			return false;
		}
	}
}
