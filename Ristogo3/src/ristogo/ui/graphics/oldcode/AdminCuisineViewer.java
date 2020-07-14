package ristogo.ui.graphics.oldcode;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.FormLabel;

public class AdminCuisineViewer extends VBox {
	/*
	private final Label cuisineTableTitle = new Label();
	private final FormLabel cuisineLabel = new FormLabel ("New Cuisine");
	private final TextField cuisineField = new TextField();
	private final Button operationButton = new Button();
	private final Button flushButton = new Button();
	private final CuisineTableView cuisineTable = new CuisineTableView();
	private final TextField findField = new TextField();
	private final Button find = new Button();
	
	public AdminCuisineViewer () {
		
		super(20);
		
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
		findBox.setAlignment(Pos.CENTER);
		findBox.getChildren().addAll(findField, find);
		
		cuisineTable.loadCuisines();
		
		this.getChildren().addAll(cuisineTableTitle, cuisineBox, cuisineTable, findBox);
		this.setStyle(GUIConfig.getCSSInterfacePartStyle());
		this.setStyle(GUIConfig.getCSSFormBoxStyle());
	
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
		find.setOnAction(this::handleFindButtonAction);
	}
	
	
	private void handleOperationButtonAction(ActionEvent event)
	{
		String name = cuisineField.getText();
		if(operationButton.getText().equals("Add"))
		{
			if (name == null)
				return;
			Cuisine cuisine = new Cuisine(name);
			Protocol.getInstance().addCuisine(cuisine);
			cuisineTable.loadCuisines();
		}		
		
		else
		{
			Cuisine cuisine = cuisineTable.getSelectedEntity();
			if (cuisine == null)
				return;
			Protocol.getInstance().deleteCuisine(cuisine);
			cuisineTable.loadCuisines();
		}
	}
	
	private void handleFlushButtonAction(ActionEvent event)
	{
		cuisineField.setText("");
	}
	
	private void handleFindButtonAction(ActionEvent event)
	{
		String name = findField.getText();
		if (name == null)
			cuisineTable.findCuisine();
		else
			cuisineTable.findCuisine(name);
	}
	
	public void changeConfigurationCuisineViewer(int config) {
		
		switch(config) {
		
		case 0:
			operationButton.setText("Add");
			cuisineTable.loadCuisines();
			break;
		case 1:
		case 2:
			operationButton.setText("Remove");
			cuisineTable.loadCuisines();
		default:
			break;
		}
	}
	
	public CuisineTableView getTable()
	{
		return cuisineTable;
	}
*/
}
