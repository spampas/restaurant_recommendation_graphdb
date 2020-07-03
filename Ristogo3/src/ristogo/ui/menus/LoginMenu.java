package ristogo.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.UserType;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.LoginForm;
import ristogo.ui.menus.forms.RegisterForm;

public class LoginMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Log-In", this::handleLogin));
		menu.add(new MenuEntry(2, "Register", this::handleRegister));
		menu.add(new MenuEntry(0, "Exit", true));
		return menu;
	}

	private void handleLogin(MenuEntry entry)
	{
		HashMap<Integer, String> response = new LoginForm().show();
		doLogin(response.get(0), response.get(1));
	}

	private void handleRegister(MenuEntry entry)
	{
		HashMap<Integer, String> response = new RegisterForm().show();
		String username = response.get(0);
		String password = response.get(1);
		UserType type = UserType.valueOf(response.get(2).toUpperCase());
		ResponseMessage resMsg;
		switch (type) {
		case CUSTOMER:
			resMsg = protocol.registerUser(new Customer(username, password));
			break;
		case OWNER:
			resMsg = protocol.registerRestaurant(new Owner(username, password), new Restaurant(username));
			break;
		default:
			Console.println("Invalid user type selected. Please try again.");
			return;
		}
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("User " + ((User)resMsg.getEntity()).getUsername() + " successfully created!");
		Console.newLine();
		doLogin(username, password);
	}

	private void doLogin(String username, String password)
	{
		ResponseMessage resMsg = protocol.performLogin(new Customer(username, password));
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		loggedUser = (User)resMsg.getEntity();
		Console.println("Successfully logged in as " + loggedUser.getUsername() + "!");
		new UserMenu().show();
	}
}
