package ristogo.ui.graphics.controls;

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
		setPrefHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		getColumns().addAll(generateColumns());
		setItems(items);
	}

	protected abstract Collection<TableColumn<S, ?>> generateColumns();
	protected abstract boolean populateTable(int page);

	public boolean setPage(int page)
	{
		if (page < 0)
			return false;
		return populateTable(page);
	}

	public void filter(String filter)
	{
		if (filter != null && filter.isBlank())
			filter = null;
		this.filter = filter;
		populateTable(0);
	}
}
