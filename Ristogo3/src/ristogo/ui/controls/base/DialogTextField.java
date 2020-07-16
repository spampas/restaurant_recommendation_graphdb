package ristogo.ui.controls.base;

import javafx.scene.control.TextField;
import ristogo.ui.config.GUIConfig;

public class DialogTextField extends TextField
{
	public DialogTextField(String promptText)
	{
		super();
		setPromptText(promptText);
		setFont(GUIConfig.getTextFont());
		setMaxWidth(300);
	}
}
