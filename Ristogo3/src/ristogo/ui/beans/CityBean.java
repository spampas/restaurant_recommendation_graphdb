package ristogo.ui.beans;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class CityBean
{
	private final SimpleStringProperty name;
	private final SimpleDoubleProperty latitude;
	private final SimpleDoubleProperty longitude;

	public CityBean(String city, double latitude, double longitude)
	{
		this.name = new SimpleStringProperty(city);
		this.latitude = new SimpleDoubleProperty(latitude);
		this.longitude = new SimpleDoubleProperty(longitude);
	}

	public double getLatitude()
	{
		return latitude.get();
	}

	public double getLongitude()
	{
		return longitude.get();
	}

	public String getName()
	{
		return name.get();
	}

	public void setLatitude(double latitude)
	{
		this.latitude.set(latitude);
	}
	
	public void setLongitude(double longitude)
	{
		this.longitude.set(longitude);
	}
	
	public void setName(String name)
	{
		this.name.set(name);
	}
	

	

}