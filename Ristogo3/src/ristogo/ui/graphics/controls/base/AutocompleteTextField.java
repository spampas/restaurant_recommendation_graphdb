package ristogo.ui.graphics.controls.base;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AutocompleteTextField extends TextField
{
	private ContextMenu entriesPopup;
	private boolean disabled;

	public AutocompleteTextField(Function<String, List<String>> searchCb)
	{
		super();
		entriesPopup = new ContextMenu();
		textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2)
			{
				if (getText().length() < 2 || disabled) {
					entriesPopup.hide();
					return;
				}
				List<String> entries = searchCb.apply(getText());
				if (entries.size() == 0) {
					entriesPopup.hide();
					return;
				}
				populatePopup(entries);
				if (!entriesPopup.isShowing())
					entriesPopup.show(AutocompleteTextField.this, Side.BOTTOM, 0, 0);
			}
		});
		focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2)
			{
				entriesPopup.hide();
			}
		});
	}

	private void populatePopup(List<String> entries)
	{
		List<CustomMenuItem> menuItems = new LinkedList<CustomMenuItem>();
		int count = Math.min(entries.size(), 10);
		for (int i = 0; i < count; i++) {
			String entry = entries.get(i);
			Label entryLabel = new Label(entry);
			CustomMenuItem menuItem = new CustomMenuItem(entryLabel, true);
			menuItem.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent actionEvent)
				{
					setText(entry);
					entriesPopup.hide();
				}
			});
			menuItems.add(menuItem);
		}
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);
	}

	public void setAutocompleteDisable(boolean value)
	{
		this.disabled = value;
	}
}
