package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.User;

public class CuisineBean extends EntityBean
{
	private final SimpleStringProperty cuisine;

	public CuisineBean(int id, String cuisine)
	{
		super(id);
		this.cuisine = new SimpleStringProperty(cuisine);
	}

	public static CuisineBean fromEntity(Cuisine cuisine)
	{
		return new CuisineBean(cuisine.getId(), cuisine.getName());
	}

	public Cuisine toEntity()
	{
		return new Cuisine(getId(), getCuisine());
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