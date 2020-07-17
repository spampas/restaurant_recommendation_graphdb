package ristogo.ui.boxes;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import ristogo.ui.boxes.base.ControlBox;
import ristogo.ui.controls.CitySelector;
import ristogo.ui.controls.base.FormButton;
import ristogo.ui.controls.base.FormLabel;

public class CitySelectorControlBox extends ControlBox
{
	private final CitySelector addField = new CitySelector();
	private final FormButton addButton;

	public CitySelectorControlBox()
	{
		super();
		FormLabel cityLabel = new FormLabel("Set your city:");
		addField.setPromptText("Your city");
		addButton = new FormButton("Save");
		addButton.setDisable(true);

		addField.textProperty().addListener(this::textChangeListener);

		addControl(cityLabel);
		addControl(addField);
		addControl(addButton);
	}

	public void setCity(String city)
	{
		addField.setValue(city);
	}

	public void setButtonText(String text)
	{
		addButton.setText(text);
	}

	public void setOnClick(Consumer<String> handler)
	{
		addButton.setOnAction((event) -> {
			handler.accept(addField.getText());
		});
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		addButton.setDisable(addField.getText() == null || addField.getText().isEmpty());
	}
}
