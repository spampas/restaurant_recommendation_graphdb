package ristogo.ui;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ristogo.common.net.entities.UserInfo;
import ristogo.net.Protocol;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.dialogs.LoginDialog;
import ristogo.ui.enums.View;

public final class RistogoGUI extends Application
{
	private Stage primaryStage;
	public static UserInfo loggedUser;

	@Override
	public void start(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		LoginDialog login = new LoginDialog();
		Optional<UserInfo> result = login.showAndWait();
		result.ifPresentOrElse(
			(data) -> { loggedUser = data; },
			() -> { System.exit(0); }
		);

		primaryStage.setTitle("RistoGo");
		primaryStage.setResizable(true);
		primaryStage.getIcons().add(new Image("/resources/logo.png"));
		changeView(View.MAIN);
	}

	private void setScene(BasePane content)
	{
		Scene scene = new Scene(content);
		scene.setFill(GUIConfig.getBgColor());
		primaryStage.setScene(scene);
		if (!primaryStage.isShowing())
			primaryStage.show();
	}

	protected void changeView(View view)
	{
		switch (view) {
		case ADMIN:
			setScene(new AdminPane(this::changeView, loggedUser));
			break;
		case PREFERENCES:
			setScene(new PreferencesPane(this::changeView, loggedUser));
			break;
		case RESTAURANTS:
			setScene(new RestaurantsPane(this::changeView, loggedUser));
			break;
		case MAIN:
		default:
			setScene(new MainPane(this::changeView, loggedUser));
			break;
		}
	}

	@Override
	public void stop()
	{
		Protocol.getInstance().performLogout();
		System.exit(0);
	}

	public static void launch(String... args)
	{
		Application.launch(args);
	}
}
