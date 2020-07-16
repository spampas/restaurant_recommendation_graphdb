package ristogo.ui.boxes;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import ristogo.ui.boxes.base.ControlBox;
import ristogo.ui.controls.CuisineSelector;
import ristogo.ui.controls.base.FormButton;

public class AddCuisineControlBox extends ControlBox
{
	private final CuisineSelector addField = new CuisineSelector();
	private final FormButton addButton;

	public AddCuisineControlBox(boolean autocomplete)
	{
		super();
		addField.setPromptText("Cuisine name");
		addField.setAutocompleteDisable(!autocomplete);
		addButton = new FormButton("Add");
		addButton.setDisable(true);

		addField.textProperty().addListener(this::textChangeListener);

		addControl(addField);
		addControl(addButton);
	}

	public void setButtonText(String text)
	{
		addButton.setText(text);
	}

	public void setOnClick(Consumer<String> handler)
	{
		addButton.setOnAction((event) -> {
			handler.accept(addField.getText());
			addField.clear();
			addButton.setDisable(true);
		});
	}

	public void setButtonDisable(boolean value)
	{
		addButton.setDisable(value);
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		addButton.setDisable(addField.getText() == null || addField.getText().isEmpty());
	}

	public void setText(String text)
	{
		addField.setText(text);
	}
}
