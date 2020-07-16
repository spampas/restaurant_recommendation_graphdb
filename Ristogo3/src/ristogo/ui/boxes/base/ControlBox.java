package ristogo.ui.boxes.base;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

public abstract class ControlBox extends HBox
{
	public ControlBox()
	{
		super(10);
	}

	public void addControl(Node control)
	{
		getChildren().add(control);
	}
}
