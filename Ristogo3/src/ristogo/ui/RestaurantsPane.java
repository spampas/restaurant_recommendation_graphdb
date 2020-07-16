package ristogo.ui;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.enums.View;
import ristogo.ui.panels.ManageRestaurantsPanel;
import ristogo.ui.panels.RestaurantFormPanel;
import ristogo.ui.panels.base.BasePanel;

public class RestaurantsPane extends BasePane
{
	protected RestaurantFormPanel form;
	protected ManageRestaurantsPanel panel;

	public RestaurantsPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected GridPane createHeader()
	{
		GridPane grid = super.createHeader();

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
	protected RestaurantFormPanel createLeft()
	{
		form = new RestaurantFormPanel(this::handleCommitRestaurant);
		return form;
	}

	@Override
	protected BasePanel createCenter()
	{
		return null;
	}

	@Override
	protected ManageRestaurantsPanel createRight()
	{
		panel = new ManageRestaurantsPanel(this::handleSelectRestaurant);
		return panel;
	}

	private void handleSelectRestaurant(RestaurantInfo restaurant)
	{
		form.setRestaurant(restaurant);
	}

	private void handleCommitRestaurant(RestaurantInfo restaurant)
	{
		panel.refresh();
	}

	@Override
	protected View getView()
	{
		return View.RESTAURANTS;
	}

}
