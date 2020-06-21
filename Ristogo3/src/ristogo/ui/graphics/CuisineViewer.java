package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Cuisine;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormLabel;

public class CuisineViewer extends VBox {
	
	private final Label cuisineTableTitle = new Label();
	private final FormLabel cuisineLabel = new FormLabel ("New Cuisine");
	private final TextField cuisineField = new TextField();
	private final Button operationButton = new Button();
	private final Button flushButton = new Button();
	private final CuisineTableView cuisineTable = new CuisineTableView(); //TODO: Inserire la table view corretta
	
	private final TextField findField = new TextField();
	private final Button find = new Button();
	
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
		


		HBox cuisineBox = new HBox(30);
		cuisineBox.getChildren().addAll(cuisineLabel,cuisineField, operationButton, flushButton);
		
		findField.setPromptText("search Cuisines");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		HBox findBox = new HBox(10);
		findBox.getChildren().addAll(findField, find);
		
		this.getChildren().addAll(cuisineTableTitle, cuisineBox, cuisineTable, findBox);
	
		cuisineTable.setOnMouseClicked((event) -> {
			Cuisine cuisine = cuisineTable.getSelectedEntity();
			if (cuisine == null)
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
		//TODO: gestire azioni
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
