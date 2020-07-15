package ristogo.ui.graphics.controls.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ristogo.ui.graphics.config.GUIConfig;

public abstract class ContentBox extends VBox
{
	private Label titleLabel = new Label();
	private HBox titleBox = new HBox(10);
	private VBox content = new VBox(10);

	public ContentBox(String title, boolean showBorder)
	{
		super(10);
		if (showBorder)
			content.setStyle(GUIConfig.getCSSFormBoxStyle());
		if (title != null && !title.isEmpty()) {
			titleLabel.setText(title);
			titleLabel.setFont(GUIConfig.getTitleFont());
			titleLabel.setTextFill(GUIConfig.getFgColor());
			titleBox.getChildren().add(titleLabel);
			titleBox.setAlignment(Pos.CENTER);
			getChildren().add(titleBox);
		}
		getChildren().add(content);
	}

	public ContentBox(boolean showBorder)
	{
		this(null, showBorder);
	}

	public ContentBox(String title)
	{
		this(title, true);
	}

	public ContentBox()
	{
		this(true);
	}

	public void setTitleFont(Font font)
	{
		titleLabel.setFont(font);
	}

	public void setTitleColor(Color color)
	{
		titleLabel.setTextFill(color);
	}

	public void setTitleAlignment(Pos pos)
	{
		titleBox.setAlignment(pos);
	}

	public void clearContent()
	{
		content.getChildren().clear();
	}

	public void addContent(Node content)
	{
		if (content == null)
			return;
		this.content.getChildren().add(content);
	}

	public void setTitle(String title)
	{
		titleLabel.setText(title);
	}

	public String getTitle()
	{
		return titleLabel.getText();
	}

	public void addAllContent(Node... contents)
	{
		if (contents == null)
			return;
		for (Node content: contents)
			addContent(content);
	}
}
