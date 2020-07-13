package ristogo.ui.graphics;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;

public class PreferencesPane extends BasePane
{

	public PreferencesPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected Node createHeader()
	{
		GridPane grid = createHeaderBase();

		Label prefLabel = new Label("Preferences");
		prefLabel.setFont(GUIConfig.getWelcomeFont());
		prefLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(prefLabel, 2, 0);

		return grid;
	}

	@Override
	protected Node createLeft()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Node createCenter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Node createRight()
	{
		// TODO Auto-generated method stub
		return null;
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
		FormButton restaurantButton = new FormButton("My Restaurants");
		restaurantButton.setOnAction((event) -> {
			changeView.accept(View.RESTAURANTS);
		});
		toolBar.getItems().add(restaurantButton);
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
