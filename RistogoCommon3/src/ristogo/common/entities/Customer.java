package ristogo.common.entities;

/**
 * This class represents a Customer in the application.
 */
public class Customer extends User
{
	private static final long serialVersionUID = -8660294617868877892L;

	/**
	 * {@inheritDoc}
	 */
	public Customer(int id, String username)
	{
		super(id, username);
	}

	/**
	 * Creates a Customer with the give username and password.
	 * @param username The username.
	 * @param password The password.
	 */
	
	public Customer(String username, String password)
	{
		super(username);
		setPassword(password);
	}
	
	public Customer(String username, String password, String city, Double latitude, Double longitude)
	{
		super(username);
		setPassword(password);
		setLatitude(latitude);
		setLongitude(longitude);
		setCity(city);
	}
}
