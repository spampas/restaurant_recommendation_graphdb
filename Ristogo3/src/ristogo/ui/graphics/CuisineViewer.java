package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.User;
import ristogo.ui.graphics.config.GUIConfig;

public class CuisineViewer extends VBox {
	
	private final Label cuisineTableTitle = new Label();
	private final TextField cuisineField = new TextField();
	private final Button operationButton = new Button();
	private final Button flushButton = new Button();
	private final UserTableView userTable = new UserTableView(); //TODO: Inserire la table view corretta
	
	private User user;
	
	public CuisineViewer () {
		
		super(10);
		
		cuisineTableTitle.setText("List of Cuisines");
		cuisineTableTitle.setFont(GUIConfig.getFormTitleFont());
		cuisineTableTitle.setTextFill(GUIConfig.getFgColor());
		cuisineTableTitle.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		cuisineField.setPromptText("insert a name of a cuisine");
		cuisineField.setMinSize(200, 30);
		cuisineField.setMaxSize(200, 30);
		
		operationButton.setText("Add");
		operationButton.setFont(GUIConfig.getButtonFont());
		operationButton.setTextFill(GUIConfig.getInvertedFgColor());
		operationButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		flushButton.setText("Flush");
		flushButton.setFont(GUIConfig.getButtonFont());
		flushButton.setTextFill(GUIConfig.getInvertedFgColor());
		flushButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());

		HBox cuisineBox = new HBox(10);
		cuisineBox.getChildren().addAll(cuisineField, operationButton, flushButton);
		
		this.getChildren().addAll(cuisineTableTitle, cuisineBox, userTable);
	
		//userTable.refreshRestaurants();
	
		userTable.setOnMouseClicked((event) -> {
			User user = userTable.getSelectedEntity();
			if (user == null)
				return;
			operationButton.setText("Remove");
		});
		
		cuisineField.setOnMouseClicked((event) -> {
			operationButton.setText("Add");
		});

		operationButton.setOnAction(this::handleOperationButtonAction);
		flushButton.setOnAction(this::handleFlushButtonAction);
	}
	
	
	private void handleOperationButtonAction(ActionEvent event)
	{
		String name = cuisineField.getText();
		if (name == null)
			return;
		//TODO: getire azioni
	}
	
	private void handleFlushButtonAction(ActionEvent event)
	{
		cuisineField.setText("");
	}
	
	public void changeConfigurationCuisineViewer(int config) {
		
		switch(config) {
		
		case 0:
			operationButton.setText("Add");
			//TODO: refresh-table
			break;
		case 1:
		case 2:
			operationButton.setText("Remove");
			//TODO: refresh-table
		default:
			break;
		}
	}
	

}
