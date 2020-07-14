package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class ErrorBox extends Alert
{
	public ErrorBox(String title, String headerMessage, String message)
	{
		super(AlertType.ERROR);
		setTitle(title);
		setHeaderText(headerMessage);
		setContentText(message);
	}

	public ErrorBox(String title, String message)
	{
		this(title, null, message);
	}

	public ErrorBox(String message)
	{
		this("An error has occured", message);
	}

	public void showAndExit()
	{
		showAndWait();
		Platform.exit();
	}
}
