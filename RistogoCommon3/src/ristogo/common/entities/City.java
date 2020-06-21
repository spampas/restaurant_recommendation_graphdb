package ristogo.common.entities;

public class City extends Entity{

	private static final long serialVersionUID = 3048031564043965549L;
	
	/**
	 * The state where the city is.
	 */
	protected String state;
	/**
	 * The region where the city is.
	 */
	protected String country;
	/**
	 * The name of the city.
	 */
	protected String city;

	
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
	 * @param stateName The name of the state.
	 * @param countryName The name of the country.
	 * @param cityName The name of the city.
	 */
	public City(int id, String state, String country, String city)
	{
		super(id);
		this.state = state;
		this.country = country;
		this.city = city;
	}
	
	/**
	 * Creates the city.
	 * @param stateName The name of the state.
	 * @param countryName The name of the country.
	 * @param cityName The name of the city.
	 */
	public City(String state, String country, String city)
	{
		this(0, state, country, city);
	}
	
	/**
	 * Creates the city.
	 * * @param cityName The name of the city.
	 */
	public City(String city)
	{
		this(0, "Italy", "Tuscany", city);
	}
	
	
	/**
	 * Gets the state.
	 * @return The state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * Sets the state.
	 * @param state The state.
	 */
	public void setState(String state)
	{
		this.state = state;
	}
	/**
	 * Gets the country.
	 * @return The country.
	 */
	public String getCountry()
	{
		return country;
	}

	/**
	 * Sets the country.
	 * @param country The country.
	 */
	public void setCountry(String country)
	{
		this.country = country;
	}
	/**
	 * Gets the city.
	 * @return The city.
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Sets the city.
	 * @param city The city.
	 */
	public void setCity(String city)
	{
		this.city = city;
	}
	
	
	@Override
	public String toString()
	{
		return "State: " + fieldToString(getState()) + "\n" +
			"Country: " + fieldToString(getCountry()) + "\n" +
			"City: " + fieldToString(getCity()) + "\n";
	}
	
	private String fieldToString(Object field)
	{
		return field == null ? "<NOT-SET>" : field.toString();
	}
}
