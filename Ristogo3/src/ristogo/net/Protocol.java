package ristogo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import ristogo.common.entities.City;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ActionRequest;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.config.Configuration;

public class Protocol implements AutoCloseable
{
	private Socket socket = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	private static Protocol instance = null;

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
		if(instance == null)
			try {
				instance = new Protocol();
			} catch (IOException ex) {
				Logger.getLogger(Protocol.class.getName()).severe("Unable to connect to server: " + ex.getMessage());
				System.exit(1);
			}
		return instance;
	}


	public ResponseMessage performLogin(User user)
	{
		return sendRequest(ActionRequest.LOGIN, user);
	}

	public ResponseMessage performLogout()
	{
		return sendRequest(ActionRequest.LOGOUT);
	}

	public ResponseMessage registerUser(Customer customer)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER_USER, customer);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof Customer))
			return getProtocolErrorMessage();
		return resMsg;
	}

	public ResponseMessage registerRestaurant(Owner owner, Restaurant restaurant)
	{		
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER_RESTAURANT, owner, restaurant);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof Owner))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage getUsers()
	{
		return sendRequest(ActionRequest.LIST_USERS);
	}
	
	public ResponseMessage getUsers(User user)
	{
		return sendRequest(ActionRequest.LIST_USERS, user);
	}

	public ResponseMessage followUser(User user)
	{
		return sendRequest(ActionRequest.FOLLOW_USER, user);
	}
	
	public ResponseMessage unfollowUser(User user)
	{
		return sendRequest(ActionRequest.UNFOLLOW_USER, user);
	}
	
	public ResponseMessage getRestaurants()
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS);
	}
	
	public ResponseMessage getRestaurants(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS, restaurant);
	}
	
	public ResponseMessage putLikeRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.PUT_LIKE_RESTAURANT, restaurant);
	}
	
	public ResponseMessage removeLikeRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.REMOVE_LIKE_RESTAURANT, restaurant);
	}
	
	public ResponseMessage getOwnRestaurant()
	{
		return sendRequest(ActionRequest.GET_OWN_RESTAURANT);
	}
	
	public ResponseMessage getStatisticRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.LIST_USERS, restaurant);
	}

	public ResponseMessage editRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.EDIT_RESTAURANT, restaurant);
	}

	public ResponseMessage deleteRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.DELETE_RESTAURANT, restaurant);
	}
	
	public ResponseMessage getCuisines()
	{
		return sendRequest(ActionRequest.LIST_CUISINES);
	}
	
	public ResponseMessage addCuisine(Cuisine cuisine)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.ADD_CUISINE, cuisine);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof Cuisine))
			return getProtocolErrorMessage();
		return resMsg;
	}

	public ResponseMessage deleteCuisine(Cuisine cuisine)
	{
		return sendRequest(ActionRequest.DELETE_CUISINE, cuisine);
	}
	
	public ResponseMessage getCities()
	{
		return sendRequest(ActionRequest.LIST_CITIES);
	}
	
	public ResponseMessage getCities(City city)
	{
		return sendRequest(ActionRequest.LIST_CITIES, city);
	}
	
	public ResponseMessage addCity(City city)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.ADD_CITY, city);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof City))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage deleteCity(City city)
	{
		return sendRequest(ActionRequest.DELETE_CITY, city);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		Logger.getLogger(Protocol.class.getName()).entering(Protocol.class.getName(), "sendRequest", entities);
		new RequestMessage(actionRequest, entities).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		Logger.getLogger(Protocol.class.getName()).exiting(Protocol.class.getName(), "sendRequest", entities);
		return resMsg != null && resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}

	private ResponseMessage getProtocolErrorMessage()
	{
		Logger.getLogger(Protocol.class.getName()).warning("Received an invalid response from server.");
		return new ResponseMessage("Invalid response from server.");
	}

	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
