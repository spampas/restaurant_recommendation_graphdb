package ristogo.ui.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.RestaurantsPanel;
import ristogo.ui.graphics.controls.UsersPanel;
import ristogo.ui.graphics.controls.base.FormButton;

public class MainPane extends BasePane
{
	public MainPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected Node createHeader()
	{
		GridPane grid = createHeaderBase();

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
	protected Node createLeft()
	{
		return new UsersPanel(loggedUser.isAdmin());
	}

	@Override
	protected Node createCenter()
	{
		return null;
	}

	@Override
	protected Node createRight()
	{
		return new RestaurantsPanel(loggedUser.isAdmin());
	}

	@Override
	protected Node createFooter()
	{
		ToolBar toolBar = new ToolBar();
		FormButton restaurantButton = new FormButton("My Restaurants");
		restaurantButton.setOnAction((event) -> {
			changeView.accept(View.RESTAURANTS);
		});
		toolBar.getItems().add(restaurantButton);
		FormButton prefButton = new FormButton("My Preferences");
		prefButton.setOnAction((event) -> {
			changeView.accept(View.PREFERENCES);
		});
		toolBar.getItems().add(prefButton);
		if (!loggedUser.isAdmin())
			return toolBar;
		FormButton adminButton = new FormButton("Admin Panel");
		adminButton.setOnAction((event) -> {
			changeView.accept(View.ADMIN);
		});
		toolBar.getItems().add(adminButton);
		return toolBar;
	}

}
