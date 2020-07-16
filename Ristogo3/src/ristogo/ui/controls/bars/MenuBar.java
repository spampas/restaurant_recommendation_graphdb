package ristogo.ui.controls.bars;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import ristogo.ui.config.GUIConfig;

public class MenuBar extends ToolBar
{
	public MenuBar()
	{
		super();
	}

	public void addMenu(String menu, Runnable handler)
	{
		Hyperlink link = new Hyperlink();
		link.setFont(GUIConfig.getTextFont());
		link.setTextFill(GUIConfig.getFgColor());
		link.setText(menu);
		link.setOnAction((event) -> {
			handler.run();
		});
		if (!getItems().isEmpty())
			getItems().add(new Separator());
		getItems().add(link);
	}
}
