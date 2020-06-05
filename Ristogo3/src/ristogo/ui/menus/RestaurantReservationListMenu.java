package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class RestaurantReservationListMenu extends Menu
{
	protected Restaurant restaurant;

	public RestaurantReservationListMenu(Restaurant restaurant)
	{
		super("Select reservation");
		this.restaurant = restaurant;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = protocol.getReservations(restaurant);
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
		} else if (resMsg.getEntityCount() < 1) {
			Console.println("No reservation to show.");
			Console.newLine();
		} else {
			for (Entity entity: resMsg.getEntities()) {
				Reservation reservation = (Reservation)entity;
				menu.add(new MenuEntry(i, getReservationMenuName(reservation), this::handleViewReservation, reservation));
				i++;
			}
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	protected static String getReservationMenuName(Reservation reservation)
	{
		return reservation.getRestaurantName() + " " + reservation.getDate() + " " + reservation.getTime() + " x" + reservation.getSeats();
	}

	private void handleViewReservation(MenuEntry entry)
	{
		Reservation reservation = (Reservation)entry.getHandlerData();
		Console.println("Reservation details:");
		Console.println(reservation.toString());
	}
}
