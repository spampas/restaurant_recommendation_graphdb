package ristogo.common.net.entities;

public class CuisineInfo extends Entity
{
	private static final long serialVersionUID = 2899103085490670483L;

	protected String name;

	public CuisineInfo(String name)
	{
		this.name = name;
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
