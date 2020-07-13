package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleStringProperty;

public class CuisineBean
{
	private final SimpleStringProperty cuisine;

	public CuisineBean(String cuisine)
	{
		this.cuisine = new SimpleStringProperty(cuisine);
	}

	public String getCuisine()
	{
		return cuisine.get();
	}

	public void setCuisine(String cuisine)
	{
		this.cuisine.set(cuisine);
	}

}