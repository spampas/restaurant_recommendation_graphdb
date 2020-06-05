package ristogo.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import ristogo.common.entities.Entity;

/**
 * Represent a message exchanged between server and client.
 */
public class Message implements Serializable
{
	private static final long serialVersionUID = -5181705765357502182L;

	protected final List<Entity> entities = new ArrayList<>();

	protected Message(List<Entity> entities)
	{
		if (entities == null)
			return;
		for (Entity entity : entities)
			this.entities.add(entity);
	}
	
	protected Message(Entity... entities)
	{
		if (entities != null)
			for (Entity entity: entities)
				this.entities.add(entity);
	}

	protected String toXML()
	{
		XStream xs = new XStream();
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xs.toXML(this);
	}

	protected static Message fromXML(String xml)
	{
		XStream xs = new XStream();
		xs.addPermission(NoTypePermission.NONE);
		xs.addPermission(NullPermission.NULL);
		xs.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xs.allowTypeHierarchy(Collection.class);
		xs.allowTypesByWildcard(new String[] {
			"ristogo.common.entities.**",
			"ristogo.common.net.**"
		});
		return (Message)xs.fromXML(xml);
	}

	/**
	 * Sends the XML-serialized message through the specified output stream.
	 * @param output The output stream.
	 */
	public void send(DataOutputStream output)
	{
		String xml = this.toXML();
		Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": SENDING\n" + xml);
		try {
			output.writeUTF(xml);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).warning("Failure in sending message.");
		}
	}

	/**
	 * Receives an XML-serialized message from the specified input stream.
	 * @param input The input stream.
	 * @return An instance of Message representing the deserialized message.
	 */
	public static Message receive(DataInputStream input)
	{
		String xml;
		try {
			xml = input.readUTF();
			Logger.getLogger(Message.class.getName()).fine(Thread.currentThread().getName() + ": RECEIVED\n" + xml);
			return fromXML(xml);
		} catch (IOException ex) {
			Logger.getLogger(Message.class.getName()).warning("Failure in receiving message. Probably counterpart terminated.");
			return null;
		}
	}

	/**
	 * Returns the list of entities attached to the message.
	 * @return The list of entities attached to the message.
	 */
	public List<Entity> getEntities()
	{
		return entities;
	}

	/**
	 * Returns the attached entity at the specified index inside the list.
	 * <p>
	 * NOTE: it's not guaranteed that entities' order is maintained after
	 * deserialization.
	 * </p>
	 * @param index The index of the entity.
	 * @return The entity.
	 */
	public Entity getEntity(int index)
	{
		if (index < 0 || index >= getEntityCount())
			return null;
		return entities.get(index);
	}

	/**
	 * Returns the first attached entity.
	 * @return The entity.
	 */
	public Entity getEntity()
	{
		return getEntity(0);
	}
	
	public <E extends Entity> E getEntity(Class<E> clazz)
	{
		for (Entity entity: entities)
			if (clazz.isAssignableFrom(entity.getClass()))
				return clazz.cast(entity);
		return null;
	}

	public <E extends Entity> List<E> getEntities(Class<E> clazz)
	{
		List<E> entityList = new ArrayList<E>();
		for (Entity entity: entities)
			if (clazz.isAssignableFrom(entity.getClass()))
				entityList.add(clazz.cast(entity));
		return entityList;
	}

	public boolean hasEntity(Class<?> clazz)
	{
		for (Entity entity: entities)
			if (clazz.isAssignableFrom(entity.getClass()))
				return true;
		return false;
	}

	/**
	 * Attach an entity to the message.
	 * @param entity The entity to attach.
	 */
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}

	/**
	 * Returns the number of entities attached to this message.
	 * @return The number of entities attached.
	 */
	public int getEntityCount()
	{
		return entities.size();
	}
}
