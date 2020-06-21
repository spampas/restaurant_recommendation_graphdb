package ristogo.ui.graphics;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormLabel;

public class CityForm extends GridPane {

	private final FormLabel cityLabel = new FormLabel("City");
	private final FormLabel countryLabel = new FormLabel("Country");
	private final FormLabel stateLabel = new FormLabel("State");
	private final TextField cityField = new TextField();
	private final TextField countryField = new TextField();
	private final TextField stateField = new TextField();
	
	public CityForm()
	{
		cityField.setPromptText("insert a name of a city");
		cityField.setMinSize(200, 30);
		cityField.setMaxSize(200, 30);
		
		countryField.setPromptText("insert a name of a country");
		countryField.setMinSize(200, 30);
		countryField.setMaxSize(200, 30);
		
		stateField.setPromptText("insert a name of a state");
		stateField.setMinSize(200, 30);
		stateField.setMaxSize(200, 30);
		
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(1, 1, 5, 1));
		setMaxWidth(500);

		add(stateLabel, 0, 0); add(stateField, 1, 0);
		add(countryLabel, 0, 1); add(countryField, 1, 1);
		add(cityLabel, 0, 2); add(cityField, 1, 2);
		
	}
	
	public String getCity() {return cityField.getText();}
	public String getCountry() {return countryField.getText();}
	public String getState() {return stateField.getText();}
	
	public void setCity(String city) {cityField.setText(city);}
	public void setCountry(String country) {countryField.setText(country);}
	public void setState(String state) {stateField.setText(state);}
	
}
