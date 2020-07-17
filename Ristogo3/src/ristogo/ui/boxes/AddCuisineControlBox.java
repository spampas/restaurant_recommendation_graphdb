package ristogo.ui.boxes;

import java.util.function.BiConsumer;

import javafx.beans.value.ObservableValue;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.ui.boxes.base.ControlBox;
import ristogo.ui.controls.CuisineSelector;
import ristogo.ui.controls.base.FormButton;

public class AddCuisineControlBox extends ControlBox
{
	private final CuisineSelector addField = new CuisineSelector();
	private final FormButton addButton;
	private CuisineInfo cuisine;

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

	public void setOnClick(BiConsumer<CuisineInfo, CuisineInfo> handler)
	{
		addButton.setOnAction((event) -> {
			handler.accept(cuisine, new CuisineInfo(addField.getText()));
			clearCuisine();
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

	public void setCuisine(CuisineInfo cuisine)
	{
		if (cuisine == null) {
			clearCuisine();
			return;
		}
		this.cuisine = cuisine;
		addField.setText(cuisine.getName());
		addButton.setDisable(false);
		addButton.setText("Save");
	}

	public void clearCuisine()
	{
		this.cuisine = null;
		addField.clear();
		addButton.setDisable(true);
		addButton.setText("Add");
	}
}
