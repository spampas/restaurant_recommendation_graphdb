package ristogo.common.net.entities;

public class StringFilter extends Entity
{
	private static final long serialVersionUID = 7066980488279679944L;

	private String value;

	public StringFilter(String value)
	{
		this.value = value;
	}

	public StringFilter()
	{
		this("");
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
