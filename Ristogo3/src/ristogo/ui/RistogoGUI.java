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

	/*

	private VBox buildAdminInterface()
	{
		VBox adminInterface = new VBox(10);
		HBox menu = new HBox();

		HBox titleBox = new HBox(10);
		GridPane title = generateTitle();
		Button returnButton = new Button("Return to the main page");
		returnButton.setTextFill(GUIConfig.getInvertedFgColor());
		returnButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());

		returnButton.setOnMouseClicked((event) -> {
			applicationInterface = buildCustomerInterface();
			Scene newScene = new Scene(new Group(applicationInterface));
			stage.setScene(newScene);
		});

		titleBox.getChildren().addAll(title, returnButton);

		VBox leftPart = new VBox(10);
		VBox leftMenu = new VBox(10);
		Label subtitleCuisine = new Label("Cuisines Settings");
		subtitleCuisine.setFont(GUIConfig.getTitleFont());
		subtitleCuisine.setTextFill(GUIConfig.getFgColor());

		AdminCuisineViewer cuisineTable = new AdminCuisineViewer();
		AdminUserViewer userTable = new AdminUserViewer();

		leftMenu.getChildren().addAll(cuisineTable, userTable);
		leftPart.getChildren().addAll(subtitleCuisine, leftMenu);
		leftPart.setAlignment(Pos.CENTER);

		VBox rightPart = new VBox(10);
		VBox rightMenu = new VBox(10);
		Label subtitleCity = new Label("Cities Settings");
		subtitleCity.setFont(GUIConfig.getTitleFont());
		subtitleCity.setTextFill(GUIConfig.getFgColor());

		CityViewer cityTable = new CityViewer();

		rightMenu.getChildren().addAll(cityTable);
		rightPart.getChildren().addAll(subtitleCity, rightMenu);
		rightPart.setAlignment(Pos.CENTER);

		menu.getChildren().addAll(leftPart, rightPart);

		adminInterface.getChildren().addAll(titleBox, menu);

		adminInterface.setStyle(GUIConfig.getCSSFormBoxStyle());

		rightMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		leftMenu.setPrefSize(500, 700);
		rightMenu.setPrefSize(500, 700);
		leftPart.setPrefSize(500, 700);
		rightPart.setPrefSize(500, 700);
		menu.setPrefSize(1000, 700);

		return adminInterface;
	}

	}*/

	public static void launch(String... args)
	{
		Application.launch(args);
	}
}
