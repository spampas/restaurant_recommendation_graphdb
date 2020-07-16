package ristogo.ui.boxes;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import ristogo.common.net.entities.CityInfo;
import ristogo.ui.boxes.base.ControlBox;
import ristogo.ui.controls.CitySelector;
import ristogo.ui.controls.base.FormButton;

public class AddCityControlBox extends ControlBox
{
	private final CitySelector nameField = new CitySelector();
	private final TextField latField = new TextField();
	private final TextField lonField = new TextField();
	private final FormButton addButton;
	private CityInfo city;

	public AddCityControlBox()
	{
		super();
		nameField.setPromptText("City name");
		nameField.setAutocompleteDisable(true);
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

	public void setOnClick(BiConsumer<CityInfo, CityInfo> handler)
	{
		addButton.setOnAction((event) -> {
			handler.accept(city, new CityInfo(nameField.getText(), Double.parseDouble(latField.getText()), Double.parseDouble(lonField.getText())));
			clearCity();
		});
	}

	public void setButtonDisable(boolean value)
	{
		addButton.setDisable(value);
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		boolean invalid = nameField.getText() == null || nameField.getText().isEmpty() || latField.getText() == null || latField.getText().isEmpty() || lonField.getText() == null || lonField.getText().isEmpty();
		if (!invalid)
			try {
				Double.parseDouble(latField.getText());
				Double.parseDouble(lonField.getText());
			} catch (NumberFormatException ex) {
				invalid = true;
			}
		addButton.setDisable(invalid);
	}

	public void setCity(CityInfo city)
	{
		if (city == null) {
			clearCity();
			return;
		}
		this.city = city;
		nameField.setValue(city.getName());
		latField.setText(Double.toString(city.getLatitude()));
		lonField.setText(Double.toString(city.getLongitude()));
		addButton.setDisable(false);
		addButton.setText("Save");
	}

	public void clearCity()
	{
		this.city = null;
		nameField.clear();
		latField.clear();
		lonField.clear();
		addButton.setDisable(true);
		addButton.setText("Add");
	}
}
