package ristogo.ui.graphics.controls;

import ristogo.ui.graphics.controls.base.ControlBox;
import ristogo.ui.graphics.controls.base.MenuBar;

public abstract class TableDetailsBox extends DetailsBox
{
	private MenuBar menuBar;
	private ControlBox controlBox;
	private PagedTableView<?> tableView;
	private DetailsBox detailsBox;

	public TableDetailsBox(String title)
	{
		super(title);
		//setMinSize(600, 600);
		//setMaxSize(900, 800);
	}

	public void setMenuBar(MenuBar menuBar)
	{
		this.menuBar = menuBar;
	}

	public void setControlBox(ControlBox controlBox)
	{
		this.controlBox = controlBox;
	}

	public void setTableView(PagedTableView<?> tableView)
	{
		this.tableView = tableView;
	}

	public void setDetailsBox(DetailsBox detailsBox)
	{
		this.detailsBox = detailsBox;
	}

	public void showContent()
	{
		clearContent();
		addContent(menuBar);
		addContent(controlBox);
		addContent(tableView);
		addContent(detailsBox);
		if (tableView != null)
			tableView.refresh();

	}

	public void refresh()
	{
		if (tableView != null)
			tableView.refresh();
	}
}
