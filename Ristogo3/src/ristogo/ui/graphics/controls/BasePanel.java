package ristogo.ui.graphics.controls;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.ContentBox;

public abstract class BasePanel extends ContentBox
{
	public BasePanel(String title)
	{
		super(title);
	}

	public BasePanel(boolean showBorder)
	{
		super(showBorder);
	}

	public BasePanel()
	{
		super();
	}

	public BasePanel(String title, boolean showBorder)
	{
		super(title, showBorder);
	}
}
