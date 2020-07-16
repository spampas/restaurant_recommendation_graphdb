package ristogo.ui;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.enums.View;
import ristogo.ui.panels.RestaurantsPanel;
import ristogo.ui.panels.UsersPanel;
import ristogo.ui.panels.base.BasePanel;

public class MainPane extends BasePane
{
	public MainPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected GridPane createHeader()
	{
		GridPane grid = super.createHeader();

		Label welcomeLabel = new Label("Welcome");
		welcomeLabel.setFont(GUIConfig.getWelcomeFont());
		welcomeLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername());
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(welcomeLabel, 2, 0);
		grid.add(usernameLabel, 3, 0);

		return grid;
	}

	@Override
	protected UsersPanel createLeft()
	{
		return new UsersPanel(loggedUser.isAdmin());
	}

	@Override
	protected BasePanel createCenter()
	{
		return null;
	}

	@Override
	protected RestaurantsPanel createRight()
	{
		return new RestaurantsPanel(loggedUser.isAdmin());
	}

	@Override
	protected View getView()
	{
		return View.MAIN;
	}
}
