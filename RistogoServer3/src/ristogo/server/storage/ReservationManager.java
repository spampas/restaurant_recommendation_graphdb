package ristogo.server.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.enums.ReservationTime;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

public class ReservationManager extends EntityManager
{
	public Reservation_ get(int reservationId)
	{
		return (Reservation_)super.get(Reservation_.class, reservationId);
	}

	public void delete(Reservation_ reservation)
	{
		if (isLevelDBEnabled())
			getLevelDBManager().deleteReservation(reservation);
		super.delete(reservation);
	}

	public void insert(Reservation_ reservation)
	{
		super.insert(reservation);
		reservation = (Reservation_)load(reservation);
		if (isLevelDBEnabled())
			getLevelDBManager().insertReservation(reservation);
	}
	
	public void update(Reservation_ reservation)
	{
		Reservation_ oldReservation = get(reservation.getId());
		if (isLevelDBEnabled())
			getLevelDBManager().updateReservation(oldReservation, reservation);
		super.update(reservation);
	}

	public List<Reservation_> getActiveReservations(User_ user)
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getActiveReservations(user);
		user = (User_)load(user);
		List<Reservation_> reservationsBag = user.getActiveReservations();
		List<Reservation_> reservations = new ArrayList<>();
		for (Reservation_ reservation: reservationsBag)
			reservations.add(reservation);
		detach(user);
		closeEM();
		return reservations;
	}

	public List<Reservation_> getActiveReservations(Restaurant_ restaurant)
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getActiveReservations(restaurant);
		restaurant = (Restaurant_)load(restaurant);
		List<Reservation_> reservationsBag = restaurant.getActiveReservations();
		List<Reservation_> reservations = new ArrayList<>();
		for (Reservation_ reservation: reservationsBag)
			reservations.add(reservation);
		detach(restaurant);
		closeEM();
		return reservations;
	}

	public List<Reservation_> getReservationsByDateTime(int restaurantId, LocalDate date, ReservationTime time)
	{
		Logger.getLogger(ReservationManager.class.getName()).entering(ReservationManager.class.getName(), "getReservationsByDateTime", new Object[]{restaurantId, date, time});
		if (isLevelDBEnabled())
			return getLevelDBManager().getReservationsByDateTime(restaurantId, date, time);
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reservation_> cq = cb.createQuery(Reservation_.class);
		Root<Reservation_> from = cq.from(Reservation_.class);
		CriteriaQuery<Reservation_> select = cq.select(from);
		ParameterExpression<Integer> idPar = cb.parameter(Integer.class);
		ParameterExpression<LocalDate> datePar = cb.parameter(LocalDate.class);
		ParameterExpression<ReservationTime> timePar = cb.parameter(ReservationTime.class);
		select.where(cb.and(
			cb.equal(from.get("date"), datePar),
			cb.equal(from.join("restaurant").get("id"), idPar),
			cb.equal(from.get("time"), timePar)
		));
		TypedQuery<Reservation_> query = em.createQuery(cq);
		query.setParameter(idPar, restaurantId);
		query.setParameter(datePar, date);
		query.setParameter(timePar, time);
		List<Reservation_> reservations;
		Logger.getLogger(ReservationManager.class.getName()).exiting(ReservationManager.class.getName(), "getReservationsByDateTime", new Object[]{restaurantId, date, time});
		try {
			reservations = query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(ReservationManager.class.getName()).info("getResultList() returned no result.");
			reservations = null;
		} finally {
			closeEM();
		}
		return reservations;
	}

	@Override
	public List<Reservation_> getAll()
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getAllReservations();
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reservation_> cq = cb.createQuery(Reservation_.class);
		Root<Reservation_> from = cq.from(Reservation_.class);
		cq.select(from);
		TypedQuery<Reservation_> query = em.createQuery(cq);
		List<Reservation_> reservations;
		try {
			reservations = query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(ReservationManager.class.getName()).info("getResultList() returned no result.");
			reservations = new ArrayList<Reservation_>();
		} finally {
			closeEM();
		}
		return reservations;
	}
}
