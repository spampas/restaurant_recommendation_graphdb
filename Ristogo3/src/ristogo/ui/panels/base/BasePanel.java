package ristogo.ui.panels.base;

import ristogo.ui.boxes.base.ContentBox;

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
