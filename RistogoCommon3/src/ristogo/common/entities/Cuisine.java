package ristogo.common.entities;

public class Cuisine extends Entity{

	private static final long serialVersionUID = 2899103085490670483L;

	/**
	 * The name of the cuisine.
	 */
	protected String cuisineName;


	public Cuisine()
	{
		super();
	}
	/**
	 * Creates a cuisine, with the specified id.
	 * @param id Cuisine's id.
	 */

	public Cuisine(int id)
	{
		super(id);
	}

	/**
	 * Creates the type of cuisine.
	 * @param id Restaurant's id.
	 * @param cuisineName The name of the cuisine.
	 */
	public Cuisine(int id, String cuisineName)
	{
		super(id);
		this.cuisineName = cuisineName;
	}
	
	/**
	 * Creates the type of cuisine.
	 * @param cuisineName The name of the cuisine.
	 */
	public Cuisine(String cuisineName)
	{
		this(0, cuisineName);
	}

	/**
	 * Gets the name of the cuisine.
	 * @return The cuisineName.
	 */
	public String getName()
	{
		return cuisineName;
	}

	/**
	 * Sets the description.
	 * @param description The description.
	 */
	public void setDescription(String cuisineName)
	{
		this.cuisineName = cuisineName;
	}
}
