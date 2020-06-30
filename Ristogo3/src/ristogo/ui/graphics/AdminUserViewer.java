package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.User;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormLabel;

public class AdminUserViewer extends VBox {
	
	private final Label userTableTitle = new Label();
	private final TextField userField = new TextField();
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
		
		userField.setPromptText("insert a name of a user");
		userField.setMinSize(200, 30);
		userField.setMaxSize(200, 30);
		
		removeButton.setText("Remove");
		removeButton.setFont(GUIConfig.getButtonFont());
		removeButton.setTextFill(GUIConfig.getInvertedFgColor());
		removeButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		removeButton.setDisable(true);
		
		flushButton.setText("Flush");
		flushButton.setFont(GUIConfig.getButtonFont());
		flushButton.setTextFill(GUIConfig.getInvertedFgColor());
		flushButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		


		HBox cuisineBox = new HBox(30);
		cuisineBox.getChildren().addAll(userTableTitle,userField, removeButton, flushButton);
		
		findField.setPromptText("search Cuisines");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		HBox findBox = new HBox(10);
		findBox.setAlignment(Pos.CENTER);
		findBox.getChildren().addAll(findField, find);
		
		this.getChildren().addAll(userTableTitle, cuisineBox, userTable, findBox);
		this.setStyle(GUIConfig.getCSSInterfacePartStyle());
		this.setStyle(GUIConfig.getCSSFormBoxStyle());
	
		userTable.setOnMouseClicked((event) -> {
			User user = userTable.getSelectedEntity();
			if (user == null)
				return;
			removeButton.setDisable(false);;
		});
		


		removeButton.setOnAction(this::handleOperationButtonAction);
		flushButton.setOnAction(this::handleFlushButtonAction);
	}
	
	
	private void handleOperationButtonAction(ActionEvent event)
	{
		String name = userField.getText();
		if (name == null)
			return;
		//TODO: gestire azioni
	}
	
	private void handleFlushButtonAction(ActionEvent event)
	{
		userField.setText("");
	}
	

	

}
