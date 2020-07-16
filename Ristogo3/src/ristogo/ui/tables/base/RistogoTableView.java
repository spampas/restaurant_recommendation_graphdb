package ristogo.ui.tables.base;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ristogo.ui.config.GUIConfig;

public abstract class RistogoTableView<S> extends TableView<S>
{
	protected String filter;
	protected final ObservableList<S> items;
	protected boolean hasNext = true;

	public RistogoTableView()
	{
		items = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(800);
		setMinHeight((GUIConfig.getMaxRowDisplayable() + 1) * getFixedCellSize());
		setMaxHeight((GUIConfig.getMaxRowDisplayable() + 1) * getFixedCellSize());

		getColumns().addAll(generateColumns());
		setItems(items);
	}

	protected abstract Collection<TableColumn<S, ?>> generateColumns();
	public abstract boolean populateTable(int page);

	public boolean hasNext()
	{
		return hasNext;
	}

	public void filter(String filter)
	{
		if (filter != null && filter.isBlank())
			filter = null;
		this.filter = filter;
		populateTable(0);
	}
}
