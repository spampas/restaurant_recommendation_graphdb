package ristogo.ui.controls.bars;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PageBar extends ToolBar
{
	private Hyperlink previous = new Hyperlink();
	private Hyperlink next = new Hyperlink();

	public PageBar()
	{
		HBox spacer = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		previous.setText("<<");
		next.setText(">>");
		previous.setVisible(false);
		getItems().addAll(previous, spacer, next);
	}

	public void setOnPrevious(Runnable handler)
	{
		previous.setOnAction((event) -> {
			handler.run();
		});
	}

	public void setOnNext(Runnable handler)
	{
		next.setOnAction((event) -> {
			handler.run();
		});
	}

	public void setPreviousVisible(boolean value)
	{
		previous.setVisible(value);
	}

	public void setNextVisible(boolean value)
	{
		next.setVisible(value);
	}
}
