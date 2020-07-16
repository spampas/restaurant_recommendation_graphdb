package ristogo.ui.boxes;

import javafx.scene.control.TextArea;
import ristogo.ui.boxes.base.DetailsBox;

public class TextAreaDetailsBox extends DetailsBox
{
	private TextArea textArea;

	public TextAreaDetailsBox(String title)
	{
		super(title);
		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setMinSize(480, 100);
		textArea.setMaxSize(880, 100);
		getChildren().add(textArea);
	}

	public String getText()
	{
		return textArea.getText();
	}

	public void setText(String text)
	{
		textArea.setText(text);
	}
}
