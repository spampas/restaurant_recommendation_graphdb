package ristogo.server.storage.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Represents a JPA entity.
 */
@MappedSuperclass
public abstract class Entity_
{
	/**
	 * The id of the entity.
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	protected int id;

	/**
	 * Creates a new entity, with the specified id.
	 * @param id Entity's id.
	 */
	protected Entity_(int id)
	{
		setId(id);
	}

	/**
	 * Creates a new entity, with id=0.
	 */
	protected Entity_()
	{
		this(0);
	}

	/**
	 * Sets entity's id.
	 * @param id The new entity's id.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Gets entity's id.
	 * @return The entity's id.
	 */
	public int getId()
	{
		return id;
	}
}
