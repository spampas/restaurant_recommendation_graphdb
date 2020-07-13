package ristogo.common.net.entities;

public class CityInfo extends Entity
{
	private static final long serialVersionUID = 3048031564043965549L;

	protected String name;
	protected double latitude;
	protected double longitude;

	public CityInfo(String name, double latitude, double longitude)
	{
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public CityInfo(String name)
	{
		this(name, Double.NaN, Double.NaN);
	}

	public double getLatitude()
	{
		return this.latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
