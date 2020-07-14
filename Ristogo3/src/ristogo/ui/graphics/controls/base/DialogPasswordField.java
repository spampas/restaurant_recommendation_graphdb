package ristogo.ui.graphics.controls.base;

import javafx.scene.control.PasswordField;
import ristogo.ui.graphics.config.GUIConfig;

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
