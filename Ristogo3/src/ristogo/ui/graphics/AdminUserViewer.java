package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormLabel;

public class AdminUserViewer extends VBox {
	/*
	private final Label userTableTitle = new Label();
	private final Button removeButton = new Button();
	private final Button flushButton = new Button();
	private final UserTableView userTable = new UserTableView();
	private final TextField findField = new TextField();
	private final Button find = new Button();
	
	public AdminUserViewer () {
		
		super(20);
		
		userTableTitle.setText("List of Users");
		userTableTitle.setFont(GUIConfig.getFormTitleFont());
		userTableTitle.setTextFill(GUIConfig.getFgColor());
		userTableTitle.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		removeButton.setText("Remove");
		removeButton.setFont(GUIConfig.getButtonFont());
		removeButton.setTextFill(GUIConfig.getInvertedFgColor());
		removeButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		removeButton.setDisable(true);
		
		flushButton.setText("Flush");
		flushButton.setFont(GUIConfig.getButtonFont());
		flushButton.setTextFill(GUIConfig.getInvertedFgColor());
		flushButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		findField.setPromptText("search Users");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		HBox findBox = new HBox(10);
		findBox.setAlignment(Pos.CENTER);
		findBox.getChildren().addAll(findField, find, flushButton, removeButton);
		
		HBox cuisineBox = new HBox(30);
		cuisineBox.getChildren().addAll(userTableTitle, findBox);
		
		
		this.getChildren().addAll(userTableTitle, cuisineBox, userTable);
		this.setStyle(GUIConfig.getCSSInterfacePartStyle());
		this.setStyle(GUIConfig.getCSSFormBoxStyle());
	
		userTable.loadUser();
		
		userTable.setOnMouseClicked((event) -> {
			User user = userTable.getSelectedEntity();
			if (user == null)
				return;
			removeButton.setDisable(false);;
		});
		
		removeButton.setOnAction(this::handleOperationButtonAction);
		flushButton.setOnAction(this::handleFlushButtonAction);
		find.setOnAction(this::handleFindButtonAction);
	}
	
	
	private void handleOperationButtonAction(ActionEvent event)
	{
		User user = userTable.getSelectedEntity();
		if(user == null)
			return;
		Protocol.getInstance().deleteUser(user);
		removeButton.setDisable(true);	
		userTable.loadUser();
	}
	
	private void handleFindButtonAction(ActionEvent event)
	{
		String name = findField.getText();
		if (name == null)
			userTable.loadUser();
		else
			userTable.loadUser(name);
	}
	
	private void handleFlushButtonAction(ActionEvent event)
	{
		findField.setText("");
	}
	
	public UserTableView getTable()
	{
		return userTable;
	}
*/
}
