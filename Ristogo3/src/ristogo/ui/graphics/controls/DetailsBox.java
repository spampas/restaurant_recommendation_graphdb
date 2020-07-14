package ristogo.ui.graphics.controls;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.ContentBox;
import ristogo.ui.graphics.controls.base.FormLabel;

public abstract class DetailsBox extends BasePanel
{
	public DetailsBox(String title)
	{
		super(title, false);
		setTitleFont(GUIConfig.getFormLabelFont());
		setTitleColor(GUIConfig.getFgColor());
		setTitleAlignment(Pos.TOP_LEFT);
	}

	public DetailsBox()
	{
		this(null);
	}
}
