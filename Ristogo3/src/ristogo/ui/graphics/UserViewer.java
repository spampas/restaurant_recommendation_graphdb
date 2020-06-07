package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.ui.graphics.config.GUIConfig;

public class UserViewer extends VBox {
	
	private final Label userTableTitle = new Label();
	private final TextField findField = new TextField();
	private final Button find = new Button();
	private final Button followButton = new Button();
	private final UserTableView userTable = new UserTableView();
	
	private final Runnable onAction;
	private User user;
	
	public UserViewer (Runnable onAction) {
		
		super(10);
		this.onAction = onAction;
		
		userTableTitle.setText("List of Restaurant");
		userTableTitle.setFont(GUIConfig.getFormTitleFont());
		userTableTitle.setTextFill(GUIConfig.getFgColor());
		userTableTitle.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		findField.setPromptText("insert a name of a restaurant");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		followButton.setText("Follow");
		followButton.setFont(GUIConfig.getButtonFont());
		followButton.setTextFill(GUIConfig.getInvertedFgColor());
		followButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());

		HBox findBox = new HBox(10);
		findBox.getChildren().addAll(findField, find);

		
		userTable.refreshRestaurants();

	
		userTable.setOnMouseClicked((event) -> {
			User user = userTable.getSelectedEntity();
			if (user == null)
				return;
		});

		find.setOnAction(this::handleFindButtonAction);
		followButton.setOnAction(this::handleLikeButtonAction);
	}
	
	
	private void handleFindButtonAction(ActionEvent event)
	{
		String name = findField.getText();
		if (name == null)
			return;
		userTable.refreshRestaurants(name);
	}
	
	private void handleLikeButtonAction(ActionEvent event)
	{
		//TODO
	}
	

}
