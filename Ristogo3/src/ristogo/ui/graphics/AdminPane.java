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

public class AdminPane extends BasePane
{

	public AdminPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected Node createHeader()
	{
		GridPane grid = createHeaderBase();

		Label adminLabel = new Label("Admin Panel");
		adminLabel.setFont(GUIConfig.getWelcomeFont());
		adminLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(adminLabel, 2, 0);

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
		FormButton prefButton = new FormButton("My Preferences");
		prefButton.setOnAction((event) -> {
			changeView.accept(View.PREFERENCES);
		});
		toolBar.getItems().add(prefButton);
		return toolBar;
	}

}
