package ristogo.ui.menus;

import java.util.SortedSet;
import ristogo.common.entities.User;
import ristogo.net.Protocol;
import ristogo.ui.Console;

public abstract class Menu
{
	protected String prompt;
	protected abstract SortedSet<MenuEntry> getMenu();
	protected static Protocol protocol = Protocol.getInstance();
	protected static User loggedUser;

	protected MenuEntry printMenu()
	{
		Console.newLine();
		MenuEntry selection = Console.printMenu(prompt, getMenu());
		selection.triggerHandler();
		return selection;
	}

	protected Menu()
	{
		this("Select an action");
	}

	protected Menu(String prompt)
	{
		this.prompt = prompt;
	}

	public void show()
	{
		while (!printMenu().isExit());
	}

	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}

}
