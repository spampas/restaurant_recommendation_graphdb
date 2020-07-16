package ristogo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
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
import ristogo.config.Configuration;

public class Protocol implements AutoCloseable
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private static Protocol instance;

	private Protocol() throws IOException
	{
		Configuration config = Configuration.getConfig();
		this.socket = new Socket(config.getServerIp(), config.getServerPort());
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		Logger.getLogger(Protocol.class.getName()).info("Connected to " + config.getServerIp() + ":" + config.getServerPort() + ".");
	}

	public static Protocol getInstance()
	{
		if(instance != null)
			return instance;
		try {
			instance = new Protocol();
		} catch (IOException ex) {
			Logger.getLogger(Protocol.class.getName()).severe("Unable to connect to server: " + ex.getMessage());
			System.exit(1);
		}
		return instance;
	}


	public ResponseMessage performLogin(UserInfo user)
	{
		return sendRequest(ActionRequest.LOGIN, user);
	}

	public ResponseMessage performLogout()
	{
		return sendRequest(ActionRequest.LOGOUT);
	}

	public ResponseMessage registerUser(UserInfo user)
	{
		return sendRequest(ActionRequest.REGISTER_USER, user);
	}

	public ResponseMessage addRestaurant(RestaurantInfo restaurant)
	{
		return sendRequest(ActionRequest.ADD_RESTAURANT, restaurant);
	}

	public ResponseMessage listUsers(StringFilter stringFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_USERS, stringFilter, pageFilter);
	}

	public ResponseMessage listUsers(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_USERS, pageFilter);
	}

	public ResponseMessage listFollowers(StringFilter stringFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_FOLLOWERS, stringFilter, pageFilter);
	}

	public ResponseMessage listFollowers(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_FOLLOWERS, pageFilter);
	}

	public ResponseMessage listFollowing(StringFilter stringFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_FOLLOWING, stringFilter, pageFilter);
	}

	public ResponseMessage listFollowing(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_FOLLOWING, pageFilter);
	}

	public ResponseMessage followUser(StringFilter filter)
	{
		return sendRequest(ActionRequest.FOLLOW_USER, filter);
	}

	public ResponseMessage unfollowUser(StringFilter filter)
	{
		return sendRequest(ActionRequest.UNFOLLOW_USER, filter);
	}

	public ResponseMessage deleteUser(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.DELETE_USER, nameFilter);
	}

	public ResponseMessage listRestaurants(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS, nameFilter, pageFilter);
	}

	public ResponseMessage listRestaurants(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS, pageFilter);
	}

	public ResponseMessage listLikedRestaurants(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_LIKED_RESTAURANTS, nameFilter, pageFilter);
	}

	public ResponseMessage listLikedRestaurants(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_LIKED_RESTAURANTS, pageFilter);
	}

	public ResponseMessage likeRestaurant(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.LIKE_RESTAURANT, nameFilter);
	}

	public ResponseMessage unlikeRestaurant(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.UNLIKE_RESTAURANT, nameFilter);
	}

	public ResponseMessage listOwnRestaurants(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_OWN_RESTAURANTS, pageFilter);
	}

	public ResponseMessage listOwnRestaurants(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_OWN_RESTAURANTS, nameFilter, pageFilter);
	}

	public ResponseMessage editRestaurant(StringFilter nameFilter, RestaurantInfo restaurant)
	{
		return sendRequest(ActionRequest.EDIT_RESTAURANT, nameFilter, restaurant);
	}

	public ResponseMessage getRestaurant(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.GET_RESTAURANT, nameFilter);
	}

	public ResponseMessage getUser(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.GET_USER, nameFilter);
	}

	public ResponseMessage getStatisticRestaurant(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.GET_STATISTIC_RESTAURANT, nameFilter);
	}

	public ResponseMessage deleteRestaurant(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.DELETE_RESTAURANT, nameFilter);
	}
	
	public ResponseMessage listLikedCuisines(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_LIKED_CUISINES, pageFilter);
	}

	public ResponseMessage listLikedCuisines(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_LIKED_CUISINES, nameFilter, pageFilter);
	}

	public ResponseMessage listCuisines(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_CUISINES, pageFilter);
	}

	public ResponseMessage listCuisines(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_CUISINES, nameFilter, pageFilter);
	}

	public ResponseMessage listCuisines(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.LIST_CUISINES, nameFilter);
	}

	public ResponseMessage addCuisine(CuisineInfo cuisine)
	{
		return sendRequest(ActionRequest.ADD_CUISINE, cuisine);
	}

	public ResponseMessage deleteCuisine(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.DELETE_CUISINE, nameFilter);
	}

	public ResponseMessage likeCuisine(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.LIKE_CUISINE, nameFilter);
	}

	public ResponseMessage unlikeCuisine(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.UNLIKE_CUISINE, nameFilter);
	}

	public ResponseMessage listCities()
	{
		return sendRequest(ActionRequest.LIST_CITIES);
	}

	public ResponseMessage listCities(StringFilter nameFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_CITIES, nameFilter, pageFilter);
	}

	public ResponseMessage listCities(PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.LIST_CITIES, pageFilter);
	}

	public ResponseMessage listCities(StringFilter nameFilter)
	{
		return sendRequest(ActionRequest.LIST_CITIES, nameFilter);
	}

	public ResponseMessage addCity(CityInfo city)
	{
		return sendRequest(ActionRequest.ADD_CITY, city);
	}

	public ResponseMessage editCity(StringFilter nameFilter, CityInfo city)
	{
		return sendRequest(ActionRequest.EDIT_CITY, nameFilter, city);
	}

	public ResponseMessage deleteCity(StringFilter stringFilter)
	{
		return sendRequest(ActionRequest.DELETE_CITY, stringFilter);
	}

	public ResponseMessage setCity(StringFilter stringFilter)
	{
		return sendRequest(ActionRequest.SET_CITY, stringFilter);
	}

	public ResponseMessage getCity()
	{
		return sendRequest(ActionRequest.GET_CITY);
	}

	public ResponseMessage recommendUser(RecommendUserInfo recommendFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.RECOMMEND_USER, recommendFilter, pageFilter);
	}

	public ResponseMessage recommendUser(StringFilter nameFilter, RecommendUserInfo recommendFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.RECOMMEND_USER, nameFilter, recommendFilter, pageFilter);
	}

	public ResponseMessage recommendRestaurant(RecommendRestaurantInfo recommendFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.RECOMMEND_RESTAURANT, recommendFilter, pageFilter);
	}

	public ResponseMessage recommendRestaurant(StringFilter nameFilter, RecommendRestaurantInfo recommendFilter, PageFilter pageFilter)
	{
		return sendRequest(ActionRequest.RECOMMEND_RESTAURANT, nameFilter, recommendFilter, pageFilter);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		Logger.getLogger(Protocol.class.getName()).entering(Protocol.class.getName(), "sendRequest", entities);
		new RequestMessage(actionRequest, entities).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		Logger.getLogger(Protocol.class.getName()).exiting(Protocol.class.getName(), "sendRequest", entities);
		return resMsg != null && resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}

	private ResponseMessage getProtocolErrorMessage()
	{
		Logger.getLogger(Protocol.class.getName()).warning("Received an invalid response from server.");
		return new ResponseMessage("Invalid response from server.");
	}

	@Override
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
