package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.City;

public class CityBean extends EntityBean
{
	private final SimpleStringProperty name;
	private final SimpleDoubleProperty latitude;
	private final SimpleDoubleProperty longitude;

	public CityBean(int id, String city, Double latitude, Double longitude)
	{
		super(id);
		this.name = new SimpleStringProperty(city);
		this.latitude = new SimpleDoubleProperty(latitude);
		this.longitude = new SimpleDoubleProperty(longitude);
	}

	public static CityBean fromEntity(City city)
	{
		return new CityBean(city.getId(), city.getName(), city.getLatitude(), city.getLongitude());
	}

	public City toEntity()
	{
		return new City(getId(), getName(), getLatitude(), getLongitude());
	}
	

	public Double getLatitude() {
		return latitude.get();
	}

	public Double getLongitude() {
		return longitude.get();
	}

	public String getName() {
		return name.get();
	}

	public void setLatitude(Double latitude)
	{
		this.latitude.set(latitude);
	}
	
	public void setLongitude(Double longitude)
	{
		this.longitude.set(longitude);
	}
	
	public void setName(String name)
	{
		this.name.set(name);
	}
	

	

}