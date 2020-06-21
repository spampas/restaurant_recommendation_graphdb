package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.City;

public class CityBean extends EntityBean
{
	private final SimpleStringProperty state;
	private final SimpleStringProperty country;
	private final SimpleStringProperty city;

	public CityBean(int id, String state, String country, String city)
	{
		super(id);
		this.state = new SimpleStringProperty(state);
		this.country = new SimpleStringProperty(country);
		this.city = new SimpleStringProperty(city);
	}

	public static CityBean fromEntity(City city)
	{
		return new CityBean(city.getId(), city.getState(), city.getCountry(), city.getCity());
	}

	public City toEntity()
	{
		return new City(getId(), getState(), getCountry(), getCity());
	}
	

	public String getState() {
		return state.get();
	}

	public String getCountry() {
		return country.get();
	}

	public String getCity() {
		return city.get();
	}

	public void setState(String state)
	{
		this.state.set(state);
	}
	
	public void setCountry(String country)
	{
		this.country.set(country);
	}
	
	public void setCity(String city)
	{
		this.city.set(city);
	}
	

	

}