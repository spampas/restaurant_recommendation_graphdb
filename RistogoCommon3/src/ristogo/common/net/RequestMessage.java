package ristogo.common.net;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Reservation;
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
	public RequestMessage(ActionRequest action, Entity... entities)
	{
		super(entities);
		this.action = action;
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
		boolean hasOwner = false;
		boolean hasRestaurant = false;
		boolean hasReservation = false;
		switch(action) {
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case REGISTER:
			if (getEntityCount() == 1 && getEntity() instanceof Customer)
				return true;
			if (getEntityCount() != 2)
				return false;
			for (Entity entity: getEntities())
				if (entity instanceof Owner)
					hasOwner = true;
				else if (entity instanceof Restaurant)
					hasRestaurant = true;
			return hasOwner && hasRestaurant;

		case EDIT_RESTAURANT:
		case DELETE_RESTAURANT:
		case LIST_RESERVATIONS:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case EDIT_RESERVATION:
		case DELETE_RESERVATION:
			return getEntityCount() == 1 && getEntity() instanceof Reservation;
		case RESERVE:
		case CHECK_SEATS:
			if (getEntityCount() < 1 || getEntityCount() > 2)
				return false;
			for (Entity entity: getEntities())
				if (entity instanceof Reservation)
					hasReservation = true;
				else if (entity instanceof Restaurant)
					hasRestaurant = true;
			return (getEntityCount() == 2 && hasReservation && hasRestaurant) || (getEntityCount() == 1 && hasReservation);
		case LIST_RESTAURANTS:
			return (getEntityCount() == 0) || (getEntityCount() == 1 && getEntity() instanceof Restaurant);
		case LOGOUT:
		case GET_OWN_RESTAURANT:
		case LIST_OWN_RESERVATIONS:
			return getEntityCount() == 0;
		default:
			return false;
		}
	}
}
