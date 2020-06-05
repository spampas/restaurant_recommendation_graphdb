package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.scene.control.Alert;

final class ErrorBox extends Alert
{
	ErrorBox(String title, String headerMessage, String message)
	{
		super(AlertType.ERROR);
		setTitle(title);
		setHeaderText(headerMessage);
		setContentText(message);
	}

	ErrorBox(String title, String message)
	{
		this(title, null, message);
	}

	ErrorBox(String message)
	{
		this("An error has occured", message);
	}

	void showAndExit()
	{
		showAndWait();
		Platform.exit();
	}
}
