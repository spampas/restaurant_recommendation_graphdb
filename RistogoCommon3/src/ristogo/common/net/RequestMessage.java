package ristogo.common.net;

import java.io.DataInputStream;
import java.util.Arrays;
import java.util.List;

import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.Entity;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RecommendRestaurantInfo;
import ristogo.common.net.entities.RecommendUserInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.enums.ActionRequest;

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
		switch (action) {
		case ADD_CITY:
			return getEntityCount() == 1 && getEntity(CityInfo.class) != null;
		case ADD_CUISINE:
			return getEntityCount() == 1 && getEntity(CuisineInfo.class) != null;
		case ADD_RESTAURANT:
			return getEntityCount() == 1 && getEntity(RestaurantInfo.class) != null;
		case DELETE_USER:
		case DELETE_RESTAURANT:
		case DELETE_CUISINE:
		case DELETE_CITY:
		case FOLLOW_USER:
		case GET_RESTAURANT:
		case GET_USER:
		case GET_STATISTIC_RESTAURANT:
		case LIKE_CUISINE:
		case LIKE_RESTAURANT:
		case UNFOLLOW_USER:
		case UNLIKE_CUISINE:
		case UNLIKE_RESTAURANT:
		case SET_CITY:
			return getEntityCount() == 1 && getEntity(StringFilter.class) != null;
		case EDIT_CITY:
			return getEntityCount() == 2 && getEntity(StringFilter.class) != null && getEntity(CityInfo.class) != null;
		case EDIT_CUISINE:
			return getEntityCount() == 2 && getEntity(StringFilter.class) != null && getEntity(CuisineInfo.class) != null;
		case EDIT_RESTAURANT:
			return getEntityCount() == 2 && getEntity(StringFilter.class) != null && getEntity(RestaurantInfo.class) != null;
		case LIST_CITIES:
		case LIST_CUISINES:
		case LIST_LIKED_CUISINES:
		case LIST_LIKED_RESTAURANTS:
		case LIST_OWN_RESTAURANTS:
		case LIST_RESTAURANTS:
		case LIST_FOLLOWING:
		case LIST_FOLLOWERS:
		case LIST_USERS:
			return getEntity(PageFilter.class) != null
				&& (getEntityCount() == 1 || (getEntityCount() == 2 && getEntity(StringFilter.class) != null));
		case RECOMMEND_RESTAURANT:
			return getEntity(RecommendRestaurantInfo.class) != null && getEntity(PageFilter.class) != null
				&& (getEntityCount() == 2 || (getEntityCount() == 3 && getEntity(StringFilter.class) != null));
		case RECOMMEND_USER:
			return getEntity(RecommendUserInfo.class) != null && getEntity(PageFilter.class) != null
				&& (getEntityCount() == 2 || (getEntityCount() == 3 && getEntity(StringFilter.class) != null));
		case LOGIN:
		case REGISTER_USER:
			return getEntityCount() == 1 && getEntity(UserInfo.class) != null;
		case GET_CITY:
		case LOGOUT:
			return getEntityCount() == 0;
		default:
			return false;
		}
	}
}
