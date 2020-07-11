package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.User;

public class UserBean extends EntityBean
{
	private final SimpleStringProperty username;
	private final SimpleBooleanProperty owner;
	private final SimpleStringProperty city;

	public UserBean(int id, String username, boolean owner, String city)
	{
		super(id);
		this.username = new SimpleStringProperty(username);
		this.owner = new SimpleBooleanProperty(owner);
		this.city = new SimpleStringProperty(city);
	}

	public static UserBean fromEntity(User user)
	{
		return new UserBean(user.getId(), user.getUsername(), user.isOwner(), user.getCity());
	}

	public User toEntity()
	{
		return isOwner() ? new Owner(getId(), getUsername()) : new Customer(getId(), getUsername());
	}

	public String getUsername()
	{
		return username.get();
	}
	
	public boolean isOwner() {
		return owner.get();
	}

	public String getCity()
	{
		return city.get();
	}

	public void setUsername(String username)
	{
		this.username.set(username);
	}

	public void setRestaurateur(String city)
	{
		this.city.set(city);
	}
	
	public void setOwner(boolean owner) {
		this.owner.set(owner);
	}


	
}