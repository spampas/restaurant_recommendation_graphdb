package ristogo.ui.boxes.base;

import javafx.geometry.Pos;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.panels.base.BasePanel;

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
