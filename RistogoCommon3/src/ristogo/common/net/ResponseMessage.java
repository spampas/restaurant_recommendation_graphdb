package ristogo.common.net;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.Entity;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StatisticInfo;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.enums.ActionRequest;

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
		if (!isSuccess())
			return getEntityCount() == 0;
		List<Entity> entities = getEntities();
		switch (actionRequest) {
		case GET_CITY:
			return getEntityCount() == 1 && getEntity(CityInfo.class) != null;
		case GET_RESTAURANT:
			return getEntityCount() == 1 && getEntity(RestaurantInfo.class) != null;
		case GET_STATISTIC_RESTAURANT:
			return getEntityCount() == 2 && getEntity(RestaurantInfo.class) != null && getEntity(StatisticInfo.class) != null;
		case GET_USER:
			for (Entity entity: entities)
				if (!(entity instanceof UserInfo) && !(entity instanceof CuisineInfo))
					return false;
			return getEntity(UserInfo.class) != null;
		case LIST_CITIES:
			for (Entity entity: entities)
				if (!(entity instanceof CityInfo))
					return false;
			return true;
		case LIST_CUISINES:
		case LIST_LIKED_CUISINES:
			for (Entity entity: entities)
				if (!(entity instanceof CuisineInfo))
					return false;
			return true;
		case LIST_LIKED_RESTAURANTS:
		case LIST_OWN_RESTAURANTS:
		case LIST_RESTAURANTS:
		case RECOMMEND_RESTAURANT:
			for (Entity entity: entities)
				if (!(entity instanceof RestaurantInfo))
					return false;
			return true;
		case LIST_FOLLOWING:
		case LIST_FOLLOWERS:
		case LIST_USERS:
		case RECOMMEND_USER:
			for (Entity entity: entities)
				if (!(entity instanceof UserInfo))
					return false;
			return true;
		case REGISTER_USER:
		case LOGIN:
			return getEntityCount() == 1 && getEntity(UserInfo.class) != null;
		case LIKE_RESTAURANT:
		case UNLIKE_RESTAURANT:
			return getEntityCount() == 1 && getEntity(RestaurantInfo.class) != null;
		case LIKE_CUISINE:
		case FOLLOW_USER:
		case EDIT_RESTAURANT:
		case EDIT_CUISINE:
		case EDIT_CITY:
		case DELETE_USER:
		case DELETE_RESTAURANT:
		case DELETE_CUISINE:
		case DELETE_CITY:
		case ADD_RESTAURANT:
		case ADD_CUISINE:
		case ADD_CITY:
		case SET_CITY:
		case UNLIKE_CUISINE:
		case UNFOLLOW_USER:
		case LOGOUT:
			return getEntityCount() == 0;
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
