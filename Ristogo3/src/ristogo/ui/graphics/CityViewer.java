package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.City;
import ristogo.ui.graphics.config.GUIConfig;

public class CityViewer extends VBox {
	
	private final Label cityTableTitle = new Label();
	private final Button operationButton = new Button();
	private final Button flushButton = new Button();
	private final CityForm cityForm = new CityForm();
	private final CityTableView cityTable = new CityTableView();
	
	private final TextField findField = new TextField();
	private final Button find = new Button();
	
	public CityViewer () {
		
		super(20);
		
		cityTableTitle.setText("List of Cities");
		cityTableTitle.setFont(GUIConfig.getFormTitleFont());
		cityTableTitle.setTextFill(GUIConfig.getFgColor());
		cityTableTitle.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		operationButton.setText("Add");
		operationButton.setFont(GUIConfig.getButtonFont());
		operationButton.setTextFill(GUIConfig.getInvertedFgColor());
		operationButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		flushButton.setText("Flush");
		flushButton.setFont(GUIConfig.getButtonFont());
		flushButton.setTextFill(GUIConfig.getInvertedFgColor());
		flushButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		findField.setPromptText("search City by name");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		HBox findBox = new HBox(10);
		findBox.setAlignment(Pos.CENTER);
		findBox.getChildren().addAll(findField, find);
		
		HBox buttonBox = new HBox(60);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setMargin(operationButton, new Insets(0, 0, 0, 100));
		buttonBox.getChildren().addAll(operationButton, flushButton);

		HBox cityBox = new HBox(10);
		cityBox.getChildren().addAll(cityForm, buttonBox);
		
		this.getChildren().addAll(cityTableTitle, cityBox, cityTable, findBox);
		
		cityTable.setOnMouseClicked((event) -> {
			City city = cityTable.getSelectedEntity();
			if (city == null)
				return;
			operationButton.setText("Remove");
		});
		
		cityForm.setOnMouseClicked((event) -> {
			operationButton.setText("Add");
		});

		operationButton.setOnAction(this::handleOperationButtonAction);
		flushButton.setOnAction(this::handleFlushButtonAction);
	}
	
	
	private void handleOperationButtonAction(ActionEvent event)
	{
		String cityName = cityForm.getName();
		String cityLatitude = cityForm.getLatitude();
		String cityLongitude = cityForm.getLongitude();
		
		if (cityName == null  || cityLatitude == null || cityLongitude == null)
			return;
		//TODO: gestire azioni
	}
	
	private void handleFlushButtonAction(ActionEvent event)
	{
		cityForm.setName("");
		cityForm.setLatitude("");
		cityForm.setLongitude("");
	}
	
	public void changeConfigurationCityViewer(int config) {
		
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
