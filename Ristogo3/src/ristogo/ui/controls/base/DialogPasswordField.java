package ristogo.ui.controls.base;

import javafx.scene.control.PasswordField;
import ristogo.ui.config.GUIConfig;

public class DialogPasswordField extends PasswordField
{
	public DialogPasswordField(String promptText)
	{
		super();
		setPromptText(promptText);
		setFont(GUIConfig.getTextFont());
		setMaxWidth(300);
	}
}
