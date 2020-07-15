package ristogo.common.net.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserInfo extends Entity
{
	private static final long serialVersionUID = -1609868778409848632L;

	protected String username;
	protected String password;
	protected CityInfo city;
	protected boolean admin;
	protected boolean following;

	public UserInfo(String username)
	{
		this(username, null, null);
	}

	public UserInfo(String username, String password)
	{
		this(username, password, false);
	}

	public UserInfo(String username, CityInfo city)
	{
		this(username, city, false);
	}

	public UserInfo(String username, CityInfo city, boolean following)
	{
		this(username, null, city, following);
	}

	public UserInfo(String username, boolean admin)
	{
		this(username, null, null, false, admin);
	}

	public UserInfo(String username, String password, boolean admin)
	{
		this(username, password, null, false, admin);
	}

	public UserInfo(String username, String password, CityInfo city, boolean following)
	{
		this(username, password, city, following, false);
	}

	public UserInfo(String username, String password, CityInfo city)
	{
		this(username, password, city, false);
	}

	public UserInfo(String username, String password, CityInfo city, boolean following, boolean admin)
	{
		this.username = username;
		this.password = password;
		this.city = city;
		this.following = following;
		this.admin = admin;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getPassword()
	{
		return this.password;
	}

	public CityInfo getCity()
	{
		return city;
	}

	public void setCity(CityInfo city)
	{
		this.city = city;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setFollowing(boolean following)
	{
		this.following = following;
	}

	public boolean isFollowing()
	{
		return following;
	}

}
