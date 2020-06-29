package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.City;

public class CityBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleStringProperty latitude;
	private final SimpleStringProperty longitude;

	public CityBean(int id, String latitude, String longitude, String city)
	{
		super(id);
		this.name = new SimpleStringProperty(latitude);
		this.latitude = new SimpleStringProperty(longitude);
		this.longitude = new SimpleStringProperty(city);
	}

	public static CityBean fromEntity(City city)
	{
		return new CityBean(city.getId(), city.getLatitude(), city.getLongitude(), city.getName());
	}

	public City toEntity()
	{
		return new City(getId(), getLatitude(), getLongitude(), getName());
	}
	

	public String getLatitude() {
		return latitude.get();
	}

	public String getLongitude() {
		return longitude.get();
	}

	public String getName() {
		return name.get();
	}

	public void setLatitude(String latitude)
	{
		this.latitude.set(latitude);
	}
	
	public void setLongitude(String longitude)
	{
		this.longitude.set(longitude);
	}
	
	public void setName(String name)
	{
		this.name.set(name);
	}
	

	

}