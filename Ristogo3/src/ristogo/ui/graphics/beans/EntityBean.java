package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleIntegerProperty;

public abstract class EntityBean
{
	protected final SimpleIntegerProperty id;

	public EntityBean(int id)
	{
		this.id = new SimpleIntegerProperty(id);
	}

	public int getId()
	{
		return id.get();
	}

	public void setId(int id)
	{
		this.id.set(id);
	}
}
