package ristogo.ui.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;

public abstract class MenuPanel extends VBox
{
	private VBox content = new VBox(10);
	private ToolBar toolBar = new ToolBar();
	private LinkedHashMap<String, SearchableTableView<?>> menus;

	public MenuPanel(String title, LinkedHashMap<String, SearchableTableView<?>> menus)
	{
		super(10);
		this.menus = menus;
		Label titleLabel = new Label(title);
		titleLabel.setFont(GUIConfig.getTitleFont());
		titleLabel.setTextFill(GUIConfig.getFgColor());
		HBox titleBox = new HBox(10);
		titleBox.getChildren().add(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		content.setStyle(GUIConfig.getCSSInterfacePartStyle());
		content.setStyle(GUIConfig.getCSSFormBoxStyle());
		content.setMinSize(600, 600);
		content.setMaxSize(900, 800);
		String first = null;
		for (String menuName: menus.keySet()) {
			Hyperlink link = new Hyperlink();
			link.setFont(GUIConfig.getTextFont());
			link.setTextFill(GUIConfig.getFgColor());
			link.setText(menuName);
			link.setOnAction((event) -> {
				changeContent(((Hyperlink)event.getSource()).getText());
			});
			if (first == null)
				first = menuName;
			else
				toolBar.getItems().add(new Separator());
			toolBar.getItems().add(link);
		}
		content.getChildren().add(toolBar);
		getChildren().addAll(titleBox, content);
		changeContent(first);
	}

	private void changeContent(String key)
	{
		setContent(menus.get(key));
	}

	private void setContent(SearchableTableView<?> content)
	{
		content.reset();
		this.content.getChildren().clear();
		this.content.getChildren().addAll(toolBar, content);
	}
}
