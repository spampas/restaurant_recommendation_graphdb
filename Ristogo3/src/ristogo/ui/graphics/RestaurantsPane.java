package ristogo.ui.graphics;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.FormButton;

public class RestaurantsPane extends BasePane
{
	protected EditRestaurantForm form;
	protected EditRestaurantsPanel list;

	public RestaurantsPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected Node createHeader()
	{
		GridPane grid = createHeaderBase();

		Label restaurantsLabel = new Label("Restaurants");
		restaurantsLabel.setFont(GUIConfig.getWelcomeFont());
		restaurantsLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername() + "'s");
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(usernameLabel, 2, 0);
		grid.add(restaurantsLabel, 3, 0);

		return grid;
	}

	@Override
	protected Node createLeft()
	{
		form = new EditRestaurantForm();
		form.setOnCommit(this::handleFormAction);
		form.setOnDelete(this::handleFormAction);
		return form;
	}

	@Override
	protected Node createCenter()
	{
		return null;
	}

	@Override
	protected Node createRight()
	{
		//list = new EditRestaurantsPanel("Your restaurants", loggedUser, this::handleSelectRestaurant);
		return list;
	}

	private void handleSelectRestaurant(RestaurantInfo restaurant)
	{
		form.setRestaurant(restaurant);
	}

	private void handleFormAction(ActionEvent event)
	{
		//list.refresh();
	}

	@Override
	protected Node createFooter()
	{
		ToolBar toolBar = new ToolBar();
		FormButton mainButton = new FormButton("Home");
		mainButton.setOnAction((event) -> {
			changeView.accept(View.MAIN);
		});
		toolBar.getItems().add(mainButton);
		FormButton prefButton = new FormButton("My Preferences");
		prefButton.setOnAction((event) -> {
			changeView.accept(View.PREFERENCES);
		});
		if (!loggedUser.isAdmin())
			return toolBar;
		FormButton adminButton = new FormButton("Admin Panel");
		adminButton.setOnAction((event) -> {
			changeView.accept(View.ADMIN);
		});
		toolBar.getItems().add(adminButton);
		toolBar.getItems().add(prefButton);
		return toolBar;
	}

}
