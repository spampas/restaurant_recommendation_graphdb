package ristogo.server.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.server.storage.entities.User_;

public class UserManager extends EntityManager
{
	public User_ getUserByUsername(String username)
	{
		Logger.getLogger(UserManager.class.getName()).entering(UserManager.class.getName(), "getUserByUsername", username);
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User_> cq = cb.createQuery(User_.class);
		Root<User_> from = cq.from(User_.class);
		CriteriaQuery<User_> select = cq.select(from);
		ParameterExpression<String> usernamePar = cb.parameter(String.class);
		select.where(cb.equal(from.get("username"), usernamePar));
		TypedQuery<User_> query = em.createQuery(cq);
		query.setParameter(usernamePar, username);
		User_ user;
		Logger.getLogger(UserManager.class.getName()).exiting(UserManager.class.getName(), "getUserByUsername", username);
		try {
			user = query.getSingleResult();
		} catch (NoResultException ex) {
			Logger.getLogger(UserManager.class.getName()).info("getSingleResult() returned no result.");
			user = null;
		} finally {
			closeEM();
		}
		return user;
	}

	@Override
	public List<User_> getAll()
	{
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User_> cq = cb.createQuery(User_.class);
		Root<User_> from = cq.from(User_.class);
		cq.select(from);
		TypedQuery<User_> query = em.createQuery(cq);
		List<User_> users;
		try {
			users = query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(UserManager.class.getName()).info("getResultList() returned no result.");
			users = new ArrayList<User_>();
		} finally {
			closeEM();
		}
		return users;
	}

	public User_ get(int userId)
	{
		return (User_)super.get(User_.class, userId);
	}

	public void delete(int userId)
	{
		super.delete(User_.class, userId);
	}
}
