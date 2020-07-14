package ristogo.ui.graphics.controls;

import ristogo.ui.graphics.controls.base.ControlBox;
import ristogo.ui.graphics.controls.base.FormButton;

public class ButtonControlBox extends ControlBox
{
	public ButtonControlBox(String text)
	{
		super();
		addControl(new FormButton(text));
	}

	public ButtonControlBox()
	{
		this("");
	}

	public void setText(String text)
	{
		((FormButton)getChildren().get(0)).setText(text);
	}

	public void setOnClick(Runnable handler)
	{
		((FormButton)getChildren().get(0)).setOnAction((event) -> {
			handler.run();
		});
	}

	public void setButtonDisable(boolean value)
	{
		((FormButton)getChildren().get(0)).setDisable(value);
	}
}
