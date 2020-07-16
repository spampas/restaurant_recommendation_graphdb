package ristogo.ui.tables.base;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.ui.controls.bars.PageBar;
import ristogo.ui.controls.base.FormButton;

public class PagedTableView<T> extends VBox
{
	private RistogoTableView<T> tableView;
	private int page;
	private FormButton deleteButton = new FormButton("Delete");
	private TextField findField = new TextField();
	private FormButton findButton = new FormButton("Filter");
	private PageBar pageBar = new PageBar();
	private Consumer<String> filterFunction;

	public PagedTableView(RistogoTableView<T> tableView)
	{
		super(10);
		this.tableView = tableView;
		filterFunction = tableView::filter;
		findField.setMinSize(200, 30);
		findField.setMaxSize(400, 30);
		deleteButton.setDisable(true);
		deleteButton.setVisible(false);
		findButton.setOnAction(this::handleFindClick);
		pageBar.setOnPrevious(() -> {
			if (tableView.populateTable(page - 1))
				page--;
			if (tableView.hasNext())
				pageBar.setNextVisible(true);
			if (page == 0)
				pageBar.setPreviousVisible(false);
		});
		pageBar.setOnNext(() -> {
			if (tableView.populateTable(page + 1)) {
				page++;
				pageBar.setPreviousVisible(true);
			}
			if (!tableView.hasNext())
				pageBar.setNextVisible(false);
		});
		HBox controlBox = new HBox(10);
		controlBox.getChildren().addAll(findField, findButton, deleteButton);
		getChildren().addAll(controlBox, tableView, pageBar);
	}

	private void handleFindClick(ActionEvent event)
	{
			filterFunction.accept(findField.getText());
			page = 0;
			pageBar.setPreviousVisible(false);
	}

	public void setFilterFunction(Consumer<String> function)
	{
		this.filterFunction = function;
	}

	public RistogoTableView<T> getTableView()
	{
		return tableView;
	}

	public T getSelection()
	{
		return tableView.getSelectionModel().getSelectedItem();
	}

	public void setOnDelete(Consumer<T> handler)
	{
		deleteButton.setOnAction((event) -> {
			handler.accept(getSelection());
			refresh();
		});
	}

	public void setOnSelect(Consumer<T> handler)
	{
		tableView.setOnMouseClicked((event) -> {
			T obj = getSelection();
			deleteButton.setDisable(obj == null);
			handler.accept(obj);
		});
	}

	public void setDeleteDisable(boolean value)
	{
		deleteButton.setDisable(value);
	}

	public void setDeletable(boolean value)
	{
		deleteButton.setVisible(value);
	}

	public void setSearchable(boolean value)
	{
		findField.setVisible(value);
		findButton.setVisible(value);
	}

	public void setFindHint(String text)
	{
		findField.setPromptText(text);
	}

	public void setDeleteButtonText(String text)
	{
		deleteButton.setText(text);
	}

	public void refresh()
	{
		tableView.getSelectionModel().clearSelection();
		deleteButton.setDisable(true);
		pageBar.setPreviousVisible(page > 0);
		tableView.populateTable(page);
		pageBar.setNextVisible(tableView.hasNext());
	}

	public void reset()
	{
		page = 0;
		tableView.getSelectionModel().clearSelection();
		deleteButton.setDisable(true);
		pageBar.setPreviousVisible(false);
		pageBar.setNextVisible(tableView.hasNext());
	}
}
