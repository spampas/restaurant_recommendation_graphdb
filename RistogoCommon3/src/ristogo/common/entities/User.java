package ristogo.common.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a generic user in the system. From this class Owner and Customer will inherit common
 * fields and methods.
 */
public abstract class User extends Entity
{
	private static final long serialVersionUID = -1609868778409848632L;

	protected String username;
	
	protected String password;
	
	protected String state;
	
	protected String country;
	
	protected String city;
	

	/**
	 * Creates an User.
	 */
	public User()
	{
		this(null);
	}

	/**
	 * Creates an User, with the specified username.
	 * @param username The username of the User.
	 */
	public User(String username)
	{
		this(username, null);
	}

	/**
	 * Creates an User, with the specified id and username.
	 * @param id Id of the user.
	 * @param username Username of the user.
	 */
	public User(int id, String username)
	{
		this(id, username, null, null, null, null);
	}

	/**
	 * Creates an User, with the specified username and password.
	 * @param username Username of the User.
	 * @param password Plain password of the User.
	 */
	public User(String username, String password)
	{
		this(0, username, password, null, null, null);
	}

	/**
	 * Creates an User, with the specified id, username and password.
	 * @param id Id of the user.
	 * @param username Username of the user.
	 * @param password Plain pasword of the user.
	 */
	public User(int id, String username, String password, String state, String country, String city)
	{
		super(id);
		setUsername(username);
		if (password != null)
			setPassword(password);
		this.state = state;
		this.country = country;
		this.city = city;
	}
		
	/**
	 * Sets the username of the User.
	 * @param username Username of the User.
	 * @return True if the set succeeded; False if the username is invalid.
	 */
	public boolean setUsername(String username)
	{
		if (!validateUsername(username))
			return false;
		this.username = username;
		return true;
	}

	/**
	 * Sets the password of the User, it stores the hash of the password.
	 * @param password Plain password of the User.
	 * @return True if the set succeeded; False otherwise.
	 */
	public boolean setPassword(String password)
	{
		if (!validatePassword(password))
			return false;
		setPasswordHash(hashPassword(password));
		return true;
	}

	/**
	 * Sets the password hash of the user.
	 * @param passwordHash The password hash.
	 */
	public void setPasswordHash(String passwordHash)
	{
		password = passwordHash;
	}

	/**
	 * Returns the username of the User.
	 * @return String The username.
	 */
	public String getUsername()
	{
		return this.username;
	}

	/**
	 * Returns the password hash of the User.
	 * @return String The password hash.
	 */
	public String getPasswordHash()
	{
		return this.password;
	}
	
	/**
	 * Returns the state of the User.
	 * @return String The state.
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * Sets the state of the user.
	 * @param state The state.
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Returns the country of the User.
	 * @return String The country.
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets the country of the user.
	 * @param country The country.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Returns the city of the User.
	 * @return String The city.
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Sets the city of the user.
	 * @param city The city.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	protected final static String hashPassword(String password)
	{
		String passwordHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			passwordHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, "This Java installation does not support SHA-256 hashing algorithm (required for password hashing).", ex);
			passwordHash = "";
		}
		return passwordHash;
	}

	/**
	 * Validates the password.
	 * @param password The password to validate.
	 * @return True if meets the criteria; False otherwise.
	 */
	public static boolean validatePassword(String password)
	{
		return password != null && password.length() > 7;
	}

	/**
	 * Validates the username.
	 * @param username The username to validate.
	 * @return True if username is valid; False otherwise.
	 */
	public static boolean validateUsername(String username)
	{
		return username != null && username.matches("^[A-Za-z0-9]{3,32}$");
	}

	/**
	 * Checks if the user has a valid username.
	 * @return True if the username is valid; False otherwise.
	 */
	public boolean hasValidUsername()
	{
		return validateUsername(username);
	}

	/**
	 * Check if the given password is the same of this user's password.
	 * @param password The password to check.
	 * @return True if the password is correct; False otherwise.
	 */
	public boolean checkPassword(String password)
	{
		return this.password != null && this.password.equals(hashPassword(password));
	}

	/**
	 * This method is used to compare an Hash with the one stored inside the User object
	 * @param passwordHash to be checked
	 * @return true if passwordHash is equal to the stored hash
	 */
	public boolean checkPasswordHash(String passwordHash)
	{
		return password != null && password.equals(passwordHash);
	}

	/**
	 * Checks if the user has a valid password.
	 * @return True if the password is valid; False otherwise.
	 */
	public boolean hasValidPassword()
	{
		return validatePasswordHash(password);
	}

	/**
	 * Checks if the given password hash is valid.
	 * @param passwordHash The password hash to check.
	 * @return True if the password hash is valid; False otherwise.
	 */
	public static boolean validatePasswordHash(String passwordHash)
	{
		return passwordHash != null && passwordHash.matches("^[a-fA-F0-9]{64}$");
	}

	/**
	 * Checks whether the user is an Owner.
	 * @return True if Owner; False if Customer.
	 */
	public boolean isOwner()
	{
		return (this instanceof Owner);
	}

}
