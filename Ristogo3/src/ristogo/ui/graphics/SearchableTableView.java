package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormLabel;
import ristogo.ui.graphics.controls.RistogoTableView;

public class SearchableTableView<T> extends VBox
{
	protected TextField findField = new TextField();
	protected Button findButton = new Button("Filter");
	protected Button actionButton = new Button();
	protected Button deleteButton = new Button("Delete");
	protected RistogoTableView<T> tableView;
	protected FormLabel detailsLabel = new FormLabel();
	protected TextArea detailsArea = new TextArea();
	protected Hyperlink previous = new Hyperlink("<<");
	protected Hyperlink next = new Hyperlink(">>");
	protected HBox controlBox = new HBox(10);
	protected int page = 0;

	public SearchableTableView(RistogoTableView<T> tableNode)
	{
		super(10);
		tableView = tableNode;
		findField.setMinSize(200, 30);
		findField.setMaxSize(400, 30);
		findButton.setFont(GUIConfig.getButtonFont());
		findButton.setTextFill(GUIConfig.getInvertedFgColor());
		findButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		actionButton.setFont(GUIConfig.getButtonFont());
		actionButton.setTextFill(GUIConfig.getInvertedFgColor());
		actionButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		actionButton.setDisable(true);
		deleteButton.setFont(GUIConfig.getButtonFont());
		deleteButton.setTextFill(GUIConfig.getInvertedFgColor());
		deleteButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		deleteButton.setDisable(true);
		deleteButton.setVisible(false);
		detailsArea.setWrapText(true);
		detailsArea.setEditable(false);
		detailsArea.setMinSize(480, 100);
		detailsArea.setMaxSize(880, 100);
		findButton.setOnAction((event) -> {
			page = 0;
			tableView.filter(findField.getText());
		});
		previous.setVisible(false);
		previous.setOnAction((event) -> {
			if (tableView.setPage(page - 1)) {
				page--;
				next.setVisible(true);
			}
			if (page == 0)
				previous.setVisible(false);
		});
		next.setOnAction((event) -> {
			if (tableView.setPage(page + 1)) {
				page++;
				previous.setVisible(true);
			} else {
				next.setVisible(false);
			}
		});
		controlBox.getChildren().addAll(findField, findButton, actionButton, deleteButton);
		ToolBar pageControls = new ToolBar();
		HBox spacer = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		pageControls.getItems().addAll(previous, spacer, next);
		getChildren().addAll(controlBox, tableView, pageControls, detailsLabel, detailsArea);
	}

	public void setControlsVisible(boolean value)
	{
		controlBox.setVisible(value);
	}

	public void setPromptText(String text)
	{
		findField.setPromptText(text);
	}

	public void setActionButtonText(String text)
	{
		actionButton.setText(text);
	}

	public void setOnAction(EventHandler<ActionEvent> handler)
	{
		actionButton.setOnAction(handler);
	}

	public void setDeletable(boolean value)
	{
		deleteButton.setVisible(value);
	}

	public void setDeleteDisable(boolean value)
	{
		deleteButton.setDisable(value);
	}

	public void setOnDelete(EventHandler<ActionEvent> handler)
	{
		deleteButton.setOnAction(handler);
	}

	public void setOnSelect(EventHandler<? super MouseEvent> handler)
	{
		tableView.setOnMouseClicked(handler);
	}

	public void setActionDisable(boolean value)
	{
		actionButton.setDisable(value);
	}

	public T getSelection()
	{
		return tableView.getSelectionModel().getSelectedItem();
	}

	public void setDetailsLabel(String text)
	{
		detailsLabel.setText(text);
	}

	public void setDetails(String text)
	{
		detailsArea.setText(text);
	}

	public void refresh()
	{
		tableView.getSelectionModel().clearSelection();
		if (page == 0)
			previous.setVisible(false);
		setDeleteDisable(true);
		setActionDisable(true);
		setDetails("");
		next.setVisible(tableView.setPage(page));
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public void reset()
	{
		setPage(0);
		refresh();
	}
}
