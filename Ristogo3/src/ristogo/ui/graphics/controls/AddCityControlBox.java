package ristogo.ui.graphics.controls;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ristogo.common.net.entities.CityInfo;
import ristogo.ui.graphics.controls.base.CitySelector;
import ristogo.ui.graphics.controls.base.ControlBox;
import ristogo.ui.graphics.controls.base.CuisineSelector;
import ristogo.ui.graphics.controls.base.FormButton;

public class AddCityControlBox extends ControlBox
{
	private final CitySelector nameField = new CitySelector();
	private final TextArea latField = new TextArea();
	private final TextArea lonField = new TextArea();
	private final FormButton addButton;

	public AddCityControlBox()
	{
		super();
		nameField.setPromptText("City name");
		latField.setPromptText("Latitude");
		lonField.setPromptText("Longitude");
		addButton = new FormButton("Add");
		addButton.setDisable(true);

		nameField.textProperty().addListener(this::textChangeListener);
		latField.textProperty().addListener(this::textChangeListener);
		latField.textProperty().addListener(this::textChangeListener);

		addControl(nameField);
		addControl(latField);
		addControl(lonField);
		addControl(addButton);
	}

	public void setButtonText(String text)
	{
		addButton.setText(text);
	}

	public void setOnClick(Consumer<String> handler)
	{
		addButton.setOnAction((event) -> {
			handler.accept(nameField.getText());
			nameField.clear();
			addButton.setDisable(true);
		});
	}

	public void setButtonDisable(boolean value)
	{
		addButton.setDisable(value);
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		//TODO check int conversion lat lon
		addButton.setDisable(nameField.getText() == null || nameField.getText().isEmpty() || latField.getText() == null || latField.getText().isEmpty() || lonField.getText() == null || lonField.getText().isEmpty());
	}

	public void setCity(CityInfo city)
	{
		nameField.setText(city.getName());
		latField.setText(Double.toString(city.getLatitude()));
		lonField.setText(Double.toString(city.getLongitude()));
		addButton.setDisable(false);
		addButton.setText("Save");
	}
}
