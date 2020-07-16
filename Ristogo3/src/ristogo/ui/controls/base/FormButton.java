package ristogo.ui.controls.base;

import javafx.scene.control.Button;
import ristogo.ui.config.GUIConfig;

public class FormButton extends Button
{
	public FormButton(String buttonText)
	{
		super(buttonText);
		setFont(GUIConfig.getButtonFont());
		setTextFill(GUIConfig.getInvertedFgColor());
		setStyle(GUIConfig.getInvertedCSSButtonBgColor());
	}
}
