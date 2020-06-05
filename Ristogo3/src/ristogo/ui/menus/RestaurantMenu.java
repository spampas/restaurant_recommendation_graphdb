package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.RestaurantForm;

public class RestaurantMenu extends Menu
{
	protected Restaurant restaurant;

	public RestaurantMenu(Restaurant restaurant)
	{
		super(restaurant.getName() + " | select an action");
		this.restaurant = restaurant;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "View details", this::handleViewRestaurant));
		menu.add(new MenuEntry(2, "Edit restaurant", this::handleEditRestaurant));
		menu.add(new MenuEntry(3, "Delete restaurant", true, this::handleDeleteRestaurant));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleViewRestaurant(MenuEntry entry)
	{
		Console.println("Restaurant details:");
		Console.println(restaurant.toString());
	}

	private void handleEditRestaurant(MenuEntry entry)
	{
		Restaurant old = restaurant;
		new RestaurantForm(restaurant).show();
		ResponseMessage resMsg = protocol.editRestaurant(restaurant);
		if (resMsg.isSuccess()) {
			Console.println("Restaurant successfully saved!");
			restaurant = (Restaurant)resMsg.getEntity();
		} else {
			Console.println(resMsg.getErrorMsg());
			restaurant = old;
		}
		Console.newLine();
	}

	private void handleDeleteRestaurant(MenuEntry entry)
	{
		boolean confirm = Console.askConfirm("This will demote your OWNER account to CUSTOMER. Are you sure?");
		if (confirm) {
			ResponseMessage resMsg = protocol.deleteRestaurant(restaurant);
			if (resMsg.isSuccess()) {
				Console.println("Restaurant " + restaurant.getName() + " successfully deleted!");
				loggedUser = new Customer(loggedUser.getId(), loggedUser.getUsername());
			} else {
				Console.println(resMsg.getErrorMsg());
			}
		}
		Console.newLine();
	}

}
