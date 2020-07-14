package ristogo.ui.graphics.oldcode;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.FormLabel;

public class CityForm extends GridPane {

	private final FormLabel nameLabel = new FormLabel("City");
	private final FormLabel latitudeLabel = new FormLabel("Latitude");
	private final FormLabel longitudeLabel = new FormLabel("Longitude");
	private final TextField nameField = new TextField();
	private final TextField latitudeField = new TextField();
	private final TextField longitudeField = new TextField();
	
	public CityForm()
	{
		nameField.setPromptText("insert the name of the city");
		nameField.setMinSize(200, 30);
		nameField.setMaxSize(200, 30);
		
		latitudeField.setPromptText("insert the latitude where the city is located");
		latitudeField.setMinSize(200, 30);
		latitudeField.setMaxSize(200, 30);
		
		longitudeField.setPromptText("insert the longitude where the city is located");
		longitudeField.setMinSize(200, 30);
		longitudeField.setMaxSize(200, 30);
		
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(1, 1, 5, 1));
		setMaxWidth(500);

		add(nameLabel, 0, 0); add(nameField, 1, 0);
		add(latitudeLabel, 0, 1); add(latitudeField, 1, 1);
		add(longitudeLabel, 0, 2); add(longitudeField, 1, 2);
		
	}
	
	public String getName() {return nameField.getText();}
	public String getLatitude() {return latitudeField.getText();}
	public String getLongitude() {return longitudeField.getText();}
	
	public void setName(String city) {nameField.setText(city);}
	public void setLatitude(String latitude) {latitudeField.setText(latitude);}
	public void setLongitude(String longitude) {longitudeField.setText(longitude);}
	
}
