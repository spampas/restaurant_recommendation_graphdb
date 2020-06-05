package ristogo.server.storage.kvdb;

import java.time.LocalDate;

import org.iq80.leveldb.DBComparator;

import ristogo.common.entities.enums.ReservationTime;

public class EntityDBComparator implements DBComparator
{
	@Override
	public int compare(byte[] arg0, byte[] arg1)
	{
		String[] key1Components = new String(arg0).split(":", 6);
		String[] key2Components = new String(arg1).split(":", 6);
		if (key1Components.length < 2)
			return Integer.MIN_VALUE;
		if (key2Components.length < 2)
			return Integer.MAX_VALUE;
		int userId1 = Integer.parseInt(key1Components[1]);
		int userId2 = Integer.parseInt(key2Components[1]);
		if (userId1 != userId2)
			return userId1 - userId2;
		if (key1Components.length < 3)
			return Integer.MIN_VALUE;
		if (key2Components.length < 3)
			return Integer.MAX_VALUE;
		int restaurantId1 = Integer.parseInt(key1Components[2]);
		int restaurantId2 = Integer.parseInt(key2Components[2]);
		if (restaurantId1 != restaurantId2)
			return restaurantId1 - restaurantId2;
		if (key1Components.length < 4)
			return Integer.MIN_VALUE;
		if (key2Components.length < 4)
			return Integer.MAX_VALUE;
		LocalDate date1 = LocalDate.parse(key1Components[3]);
		LocalDate date2 = LocalDate.parse(key2Components[3]);
		if (date1.isBefore(date2))
			return Integer.MIN_VALUE;
		if (date1.isAfter(date2))
			return Integer.MAX_VALUE;
		if (key1Components.length < 5)
			return Integer.MIN_VALUE;
		if (key2Components.length < 5)
			return Integer.MAX_VALUE;
		ReservationTime time1 = ReservationTime.valueOf(key1Components[4]);
		ReservationTime time2 = ReservationTime.valueOf(key2Components[4]);
		if (time1 == ReservationTime.LUNCH && time2 == ReservationTime.DINNER)
			return Integer.MIN_VALUE;
		if (time1 == ReservationTime.DINNER && time2 == ReservationTime.LUNCH)
			return Integer.MAX_VALUE;
		if (key1Components.length < 6)
			return Integer.MIN_VALUE;
		if (key2Components.length < 6)
			return Integer.MAX_VALUE;
		return key1Components[5].compareTo(key2Components[5]);
	}

	@Override
	public String name()
	{
		return "ristogo.server.storage.kvdb.EntityDBComparator";
	}

	@Override
	public byte[] findShortestSeparator(byte[] start, byte[] limit)
	{
		return start;
	}

	@Override
	public byte[] findShortSuccessor(byte[] key)
	{
		return key;
	}
}
