package ristogo.ui.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserBean
{
	private final SimpleStringProperty username;
	private final SimpleStringProperty city;
	private final SimpleBooleanProperty following;

	public UserBean(String username, String city, boolean following)
	{
		this.username = new SimpleStringProperty(username);
		this.city = new SimpleStringProperty(city);
		this.following = new SimpleBooleanProperty(following);
	}

	public String getUsername()
	{
		return username.get();
	}

	public String getCity()
	{
		return city.get();
	}

	public void setUsername(String username)
	{
		this.username.set(username);
	}

	public void setCity(String city)
	{
		this.city.set(city);
	}

	public void setFollowing(boolean following)
	{
		this.following.set(following);
	}

	public boolean isFollowing()
	{
		return following.get();
	}
}