package ristogo.ui.graphics.controls.base;

import javafx.scene.control.Label;
import ristogo.ui.graphics.config.GUIConfig;

public class DialogLabel extends Label
{
	public DialogLabel(String labelText)
	{
		super(labelText);
		setFont(GUIConfig.getDialogLabelFont());
		setTextFill(GUIConfig.getDialogFgColor());
	}
}
