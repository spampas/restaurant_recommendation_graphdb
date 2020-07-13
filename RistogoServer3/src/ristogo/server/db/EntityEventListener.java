package ristogo.server.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.neo4j.ogm.session.event.Event;
import org.neo4j.ogm.session.event.EventListener;

import ristogo.server.db.entities.annotations.PostDelete;
import ristogo.server.db.entities.annotations.PostSave;
import ristogo.server.db.entities.annotations.PreDelete;
import ristogo.server.db.entities.annotations.PreSave;

public class EntityEventListener implements EventListener
{
	private void callListeners(Class<? extends Annotation> type, Object obj)
	{
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (Method method: methods) {
			method.setAccessible(true);
			if (method.isAnnotationPresent(type)) {
				try {
					method.invoke(obj);
				} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onPreSave(Event event)
	{
		callListeners(PreSave.class, event.getObject());
	}

	@Override
	public void onPostSave(Event event)
	{
		callListeners(PostSave.class, event.getObject());
	}

	@Override
	public void onPreDelete(Event event)
	{
		callListeners(PreDelete.class, event.getObject());
	}

	@Override
	public void onPostDelete(Event event)
	{
		callListeners(PostDelete.class, event.getObject());
	}
}
