package ristogo.ui.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ConnectionBean
{
	private SimpleStringProperty city;
	private SimpleIntegerProperty distance;

	public ConnectionBean(String city, int distance)
	{
		this.city = new SimpleStringProperty(city);
		this.distance = new SimpleIntegerProperty(distance);
	}

	public String getCity()
	{
		return city.get();
	}

	public void setCity(String city)
	{
		this.city.set(city);
	}

	public int getDistance()
	{
		return distance.get();
	}

	public void setDistance(int distance)
	{
		this.distance.set(distance);
	}
}
