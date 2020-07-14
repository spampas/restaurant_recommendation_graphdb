package ristogo.ui.graphics.controls.base;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ristogo.ui.graphics.config.GUIConfig;

public abstract class RistogoTableView<S> extends TableView<S>
{
	protected String filter;
	protected final ObservableList<S> items;

	public RistogoTableView()
	{
		items = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(800);
		setMinHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		getColumns().addAll(generateColumns());
		setItems(items);
	}

	protected abstract Collection<TableColumn<S, ?>> generateColumns();
	public abstract boolean populateTable(int page);

	public void filter(String filter)
	{
		if (filter != null && filter.isBlank())
			filter = null;
		this.filter = filter;
		populateTable(0);
	}
}
