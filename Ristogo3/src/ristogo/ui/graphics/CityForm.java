package ristogo.ui.graphics;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CityForm extends VBox {

	/*private final Label cityLabel = new Label("City");
	private final Label countryLabel = new Label("Country");
	private final Label stateLabel = new Label("State");*/
	private final TextField cityField = new TextField();
	private final TextField countryField = new TextField();
	private final TextField stateField = new TextField();
	
	public CityForm()
	{
		cityField.setPromptText("insert a name of a city");
		cityField.setMinSize(200, 30);
		cityField.setMaxSize(200, 30);
		
		countryField.setPromptText("insert a name of a city");
		countryField.setMinSize(200, 30);
		countryField.setMaxSize(200, 30);
		
		stateField.setPromptText("insert a name of a city");
		stateField.setMinSize(200, 30);
		stateField.setMaxSize(200, 30);
	}
	
	public String getCity() {return cityField.getText();}
	public String getCountry() {return countryField.getText();}
	public String getState() {return stateField.getText();}
	
	public void setCity(String city) {cityField.setText(city);}
	public void setCountry(String country) {countryField.setText(country);}
	public void setState(String state) {stateField.setText(state);}
	
}
