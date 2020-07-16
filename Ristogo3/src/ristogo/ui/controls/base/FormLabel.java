package ristogo.ui.controls.base;

import javafx.scene.control.Label;
import ristogo.ui.config.GUIConfig;

public class FormLabel extends Label
{
	public FormLabel(String labelText)
	{
		super(labelText);
		setFont(GUIConfig.getFormLabelFont());
		setTextFill(GUIConfig.getFgColor());
	}

	public FormLabel()
	{
		this("");
	}
}
