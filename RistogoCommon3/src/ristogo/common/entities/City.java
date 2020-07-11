package ristogo.common.entities;

public class City extends Entity{

	private static final long serialVersionUID = 3048031564043965549L;
	
	protected String name;
	protected Double latitude;
	protected Double longitude;

	
	public City()
	{
		super();
	}
	
	/**
	 * Creates a city, with the specified id.
	 * @param id City's id.
	 */
	public City(int id)
	{
		super(id);
	}
	
	/**
	 * Creates the city.
	 * @param id City's id.
	 * @param name The name of the city.
	 * @param latitude The latitude where the city is located.
	 * @param latitude The longitude where the city is located.
	 */
	public City(int id, String name, Double latitude, Double longitude)
	{
		super(id);
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	
	/**
	 * Creates the city.
	 * @param latitudeName The name of the latitude.
	 * @param longitudeName The name of the longitude.
	 * @param cityName The name of the city.
	 */
	public City(String name, Double latitude, Double longitude)
	{
		this(0, name, latitude, longitude);
	}
	
	
	
	/**
	 * Creates the city.
	 * @param cityName The name of the city.
	 */
	public City(String cityName) {
		this(0,cityName, null, null);
	}
	
	
	/**
	 * Gets the latitude.
	 * @return The latitude.
	 */
	public Double getLatitude()
	{
		return this.latitude;
	}

	/**
	 * Sets the latitude.
	 * @param latitude The latitude.
	 */
	public void setLatitude(Double latitude)
	{
		this.latitude = latitude;
	}
	/**
	 * Gets the longitude.
	 * @return The longitude.
	 */
	public Double getLongitude()
	{
		return longitude;
	}

	/**
	 * Sets the longitude.
	 * @param longitude The longitude.
	 */
	public void setLongitude(Double longitude)
	{
		this.longitude = longitude;
	}
	/**
	 * Gets the city name.
	 * @return The name of the city.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 * @param city name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	@Override
	public String toString()
	{
		return "latitude: " + fieldToString(getLatitude()) + "\n" +
			"longitude: " + fieldToString(getLongitude()) + "\n" +
			"City: " + fieldToString(getName()) + "\n";
	}
	
	private String fieldToString(Object field)
	{
		return field == null ? "<NOT-SET>" : field.toString();
	}
}
