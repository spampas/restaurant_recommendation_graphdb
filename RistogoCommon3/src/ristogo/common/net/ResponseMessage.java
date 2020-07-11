package ristogo.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	protected final boolean success;
	protected final String errorMsg;

	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, new ArrayList<Entity>());
	}

	public ResponseMessage(List<Entity> entities)
	{
		this(true, null, entities);
	}

	public ResponseMessage(Entity... entities)
	{
		this(true, null, entities);
	}

	protected ResponseMessage(boolean success, String errorMsg, List<Entity> entities)
	{
		super(entities);
		this.success = success;
		this.errorMsg = errorMsg;
	}

	protected ResponseMessage(boolean success, String errorMsg, Entity... entities)
	{
		this(success, errorMsg, Arrays.asList(entities));
	}

	public boolean isSuccess()
	{
		return success;
	}

	public boolean isValid(ActionRequest actionRequest)
	{
		// TODO: Gestire azioni
		if (!isSuccess())
			return getEntityCount() == 0;
		switch(actionRequest)
		{
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case LOGOUT:
		case REGISTER_USER:
		case REGISTER_RESTAURANT:
		case LIST_USERS:
		case LIST_FRIENDS:
		case FOLLOW_USER:
		case UNFOLLOW_USER:
		case GET_OWN_RESTAURANT:
		case EDIT_RESTAURANT:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case LIST_RESTAURANTS:
			if (getEntityCount() > 0)
				for (Entity entity: getEntities())
					if (!(entity instanceof Restaurant))
						return false;
			return true;
		case DELETE_RESTAURANT:
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

	public static ResponseMessage receive(DataInputStream input)
	{
		return (ResponseMessage)Message.receive(input);
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}
}
