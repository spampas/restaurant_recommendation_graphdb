package ristogo.ui.menus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class RestaurantListMenu extends Menu
{
	protected Restaurant restaurant;

	public RestaurantListMenu()
	{
		super("Select the restaurant");
	}

	public RestaurantListMenu(Restaurant restaurant)
	{
		this();
		this.restaurant = restaurant;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg;
		if (restaurant == null)
			resMsg = protocol.getRestaurants();
		else
			resMsg = protocol.getRestaurants(restaurant);
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			Console.newLine();
		} else if (resMsg.getEntityCount() < 1) {
			Console.println("No restaurants available.");
			Console.newLine();
		} else {
			for (Entity entity: resMsg.getEntities()) {
				Restaurant restaurant = (Restaurant)entity;
				
				//TODO: Gestire opzioni in base al menù
				
				i++;
			}
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

}
