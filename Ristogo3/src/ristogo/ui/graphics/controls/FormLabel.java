package ristogo.ui.graphics.controls;

import javafx.scene.control.Label;
import ristogo.ui.graphics.config.GUIConfig;

public class FormLabel extends Label
{
	public FormLabel(String labelText)
	{
		super(labelText);
		setFont(GUIConfig.getFormLabelFont());
		setTextFill(GUIConfig.getFgColor());
	}
}
