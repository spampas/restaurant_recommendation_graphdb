package ristogo.common.entities;

/**
 * This class represents an Owner in the application.
 */
public class Owner extends User
{
	private static final long serialVersionUID = 2737707758495190002L;

	/**
	 * {@inheritDoc}
	 */
	public Owner(int id, String username)
	{
		super(id, username);
	}

	/**
	 * Creates an Owner with the give username and password.
	 * @param username The username.
	 * @param password The password.
	 */
	
	public Owner(String username, String password)
	{
		super(username);
		setPassword(password);
	}
	
	public Owner(String username, String password, String city, Double latitude, Double longitude)
	{
		super(username);
		setPassword(password);
		setLatitude(latitude);
		setLongitude(longitude);
		setCity(city);
	}
}
