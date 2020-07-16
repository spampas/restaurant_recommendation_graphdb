package ristogo.ui.controls.base;

import javafx.scene.control.TextField;
import ristogo.ui.config.GUIConfig;

public class NotEditableTextField extends TextField
{
	public NotEditableTextField()
	{
		super();
		setFont(GUIConfig.getTextFont());
		setEditable(false);
		setMinSize(50, 30);
		setMaxSize(50, 30);
	}
}
