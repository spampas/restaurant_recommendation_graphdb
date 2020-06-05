package ristogo.common.entities;

import java.io.Serializable;

/**
 * This is a superclass from which inherits Customer, Owner, Restaurant and Reservation
 * basic common fields and methods. This "family" of classes is used to represent
 * objects that can be exchanged (Serialized) between Client and Server application.
 */
public abstract class Entity implements Serializable
{
	private static final long serialVersionUID = -2895261003278803706L;

	protected int id;

	Entity(int id)
	{
		setId(id);
	}

	Entity()
	{
		this(0);
	}

	/**
	 * Sets the id field of the Entity.
	 * @param id The id of the Entity.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Returns the value of the id field of the Entity.
	 * @return The id of the Entity.
	 */
	public int getId()
	{
		return id;
	}
}
