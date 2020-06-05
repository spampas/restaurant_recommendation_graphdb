package ristogo.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

/**
 * Represents a response message, sent from the server to the client after each
 * request.
 */
public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	protected final boolean success;
	protected final String errorMsg;

	/**
	 * Creates a new error response message, with the specified error
	 * message.
	 * @param errorMsg The error message.
	 */
	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, new ArrayList<Entity>());
	}

	/**
	 * Creates a new response message, with optional attached entities.
	 * @param entities The list of entities to attach.
	 */
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

	/**
	 * Checks whether this message is a response to a successfully
	 * completed request or not.
	 * @return True if the message is a response to a successful request;
	 * False if it is a error response.
	 */
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * Checks whether this message is valid (properly formed).
	 * @param actionRequest The type of request that resulted in this
	 * response.
	 * @return True if the message is a valid response for the request type
	 * specified; False otherwise.
	 */
	public boolean isValid(ActionRequest actionRequest)
	{
		if (!isSuccess())
			return getEntityCount() == 0;
		switch(actionRequest)
		{
		case GET_OWN_RESTAURANT:
		case EDIT_RESTAURANT:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case LIST_RESTAURANTS:
			if (getEntityCount() > 0)
				for (Entity entity: getEntities())
					if (!(entity instanceof Restaurant))
						return false;
			return true;
		case REGISTER:
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case LOGOUT:
		case DELETE_RESTAURANT:
			return getEntityCount() == 0;
		default:
			return false;
		}
	}
	
	public static ResponseMessage receive(DataInputStream input)
	{
		return (ResponseMessage)Message.receive(input);
	}

	/**
	 * Returns the error message associated with the response.
	 * @return The error message.
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}
}
