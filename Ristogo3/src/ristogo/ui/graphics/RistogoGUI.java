package ristogo.ui.graphics;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;

public final class RistogoGUI extends Application
{
	public static User loggedUser;
	private static Restaurant restaurant;

	private static ModifyRestaurantForm restaurantForm;

	@Override
	public void start(Stage primaryStage)
	{
/*		LoginDialog login = new LoginDialog();
		Optional<User> result = login.showAndWait();
		result.ifPresentOrElse(
			data -> { loggedUser = data; },
			() -> { System.exit(0); }
		);
*/
		loggedUser = new Customer("ciccio", "ciccio");
		VBox applicationInterface;
		//applicationInterface = loggedUser.isOwner() ? buildOwnerInterface() : buildCustomerInterface();
		applicationInterface = buildAdminInterface();
		Scene scene = new Scene(new Group(applicationInterface));
		scene.setFill(GUIConfig.getBgColor());

		primaryStage.setTitle("RistoGo");
		primaryStage.setResizable(true);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/resources/logo.png"));
		primaryStage.show();
	}

	@Override
	public void stop()
	{
		//Protocol.getInstance().performLogout();
		System.exit(0);
	}

	private VBox buildCustomerInterface()
	{
		VBox applicationInterface = new VBox(10);
		HBox menu = new HBox();
		GridPane title = generateTitle();
		
		VBox leftPart = new VBox(10);
		VBox leftMenu = new VBox(10);
		Label subtitleUser = new Label("Users Menu");
		subtitleUser.setFont(GUIConfig.getTitleFont());
		subtitleUser.setTextFill(GUIConfig.getFgColor());
		ButtonForm buttonFormLeft = new ButtonForm("restaurant");
		UserViewer userTable = new UserViewer();
		
		HBox optionMenu = generateOptionMenu();
		
		leftMenu.getChildren().addAll(buttonFormLeft, userTable, optionMenu);
		leftPart.getChildren().addAll(subtitleUser, leftMenu);
		leftPart.setAlignment(Pos.CENTER);
		
		VBox rightPart = new VBox(10);
		VBox rightMenu = new VBox(10);
		Label subtitleRestaurant = new Label("Restaurants Menu");
		subtitleRestaurant.setFont(GUIConfig.getTitleFont());
		subtitleRestaurant.setTextFill(GUIConfig.getFgColor());
		ButtonForm buttonFormRight = new ButtonForm("friend");
		RestaurantViewer restaurantTable = new RestaurantViewer();
		
		rightMenu.getChildren().addAll(buttonFormRight,restaurantTable);
		rightPart.getChildren().addAll(subtitleRestaurant, rightMenu);
		rightPart.setAlignment(Pos.CENTER);
		
		menu.getChildren().addAll(leftPart, rightPart);
		
		applicationInterface.getChildren().addAll(title, menu);
		applicationInterface.setAlignment(Pos.CENTER);
		
		applicationInterface.setStyle(GUIConfig.getCSSFormBoxStyle());

		leftMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		rightMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		leftMenu.setPrefSize(500, 600);
		rightMenu.setPrefSize(500, 600);
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		menu.setPrefSize(1000, 600);
		
		
		buttonFormLeft.getMyButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Unfollow");
		});
	
		buttonFormLeft.getFindButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
		});
		
		buttonFormLeft.getRecommendedButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
			UserRecommendationDialog recommendationDialog = new UserRecommendationDialog();
			Optional<User> result = recommendationDialog.showAndWait();
			result.ifPresentOrElse(
				data -> { loggedUser = data; },
				() -> { }
			);
		});
		
		buttonFormRight.getMyButton().setOnMouseClicked((event) -> {
				restaurantTable.getLikeButton().setText("Remove Like");
		});
		
		buttonFormRight.getFindButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
		});
		
		buttonFormRight.getRecommendedButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
			RestaurantRecommendationDialog recommendationDialog = new RestaurantRecommendationDialog();
			Optional<Restaurant> result = recommendationDialog.showAndWait();
			result.ifPresentOrElse(
				data -> { restaurant = data; },
				() -> { }
			);
		});
		
		return applicationInterface;
	}
	
	private VBox buildAdminInterface()
	{
		VBox applicationInterface = new VBox(10);
		HBox menu = new HBox();
		GridPane title = generateTitle();
		
		VBox leftPart = new VBox(10);
		VBox leftMenu = new VBox(10);
		Label subtitleCuisine = new Label("Cuisines Menu");
		subtitleCuisine.setFont(GUIConfig.getTitleFont());
		subtitleCuisine.setTextFill(GUIConfig.getFgColor());
		
		CuisineViewer cuisineTable = new CuisineViewer();
		
		leftMenu.getChildren().addAll(cuisineTable);
		leftPart.getChildren().addAll(subtitleCuisine, leftMenu);
		leftPart.setAlignment(Pos.CENTER);
		
		VBox rightPart = new VBox(10);
		VBox rightMenu = new VBox(10);
		Label subtitleCity = new Label("Cities Menu");
		CityForm cityform = new CityForm();
		subtitleCity.setFont(GUIConfig.getTitleFont());
		subtitleCity.setTextFill(GUIConfig.getFgColor());
		
		CityViewer cityTable = new CityViewer();
		
		rightMenu.getChildren().addAll(cityTable);
		rightPart.getChildren().addAll(subtitleCity, rightMenu);
		rightPart.setAlignment(Pos.CENTER);
		
		menu.getChildren().addAll(leftPart, rightPart);
		
		applicationInterface.getChildren().addAll(title, menu);
		applicationInterface.setAlignment(Pos.CENTER);
		
		applicationInterface.setStyle(GUIConfig.getCSSFormBoxStyle());

		leftMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		rightMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		leftMenu.setPrefSize(500, 600);
		rightMenu.setPrefSize(500, 600);
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		menu.setPrefSize(1000, 600);	
	
		return applicationInterface;
	}

	private HBox buildOwnerInterface()
	{
		HBox applicationInterface = new HBox(20);

		GridPane title = generateTitle();
		restaurantForm = new ModifyRestaurantForm(this::getOwnRestaurant);
		getOwnRestaurant();

		VBox leftPart = new VBox(10);
		VBox rightPart = new VBox(10);

		leftPart.getChildren().addAll(title, restaurantForm);
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
		
	
		leftPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftPart.setPrefSize(400, 600);
		rightPart.setPrefSize(600, 600);
		rightPart.setAlignment(Pos.CENTER);
		applicationInterface.setPrefSize(1000, 600);
		title.setAlignment(Pos.CENTER);
		applicationInterface.setAlignment(Pos.CENTER);
		

		return applicationInterface;
	}
	
	

	private GridPane generateTitle()
	{
		Label title = new Label("RistoGo - Recommendations");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		ImageView icon = new ImageView(getClass().getResource("/resources/logo.png").toString());
		icon.setFitHeight(30);
		icon.setFitWidth(30);

		Label welcomeLabel = new Label("Welcome");
		welcomeLabel.setFont(GUIConfig.getWelcomeFont());
		welcomeLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername());
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 5, 1));
		grid.setMaxWidth(500);

		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		grid.add(welcomeLabel, 2, 0);
		grid.add(usernameLabel, 3, 0);

		return grid;
	}
	
	HBox generateOptionMenu () {
		
		HBox optionMenu = new HBox(10);
		Button option = new Button("Options");
		option.setFont(GUIConfig.getButtonFont());
		option.setTextFill(GUIConfig.getInvertedFgColor());
		option.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		if(loggedUser.isOwner()) {
			Button owner = new Button("My Restaurants");
			owner.setFont(GUIConfig.getButtonFont());
			owner.setTextFill(GUIConfig.getInvertedFgColor());
			owner.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
			optionMenu.getChildren().addAll(owner,option);
		}
		else {
			optionMenu.getChildren().addAll(option);
		}
		
		return optionMenu;
		
	}

	public static void launch(String... args)
	{
		Application.launch(args);
	}

	private void getOwnRestaurant()
	{
		if (restaurantForm == null)
			return;
		ResponseMessage resMsg = Protocol.getInstance().getOwnRestaurant();
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the informations about your restaurant.", resMsg.getErrorMsg()).showAndExit();
			restaurant = null;
			return;
		}
		restaurant = (Restaurant)resMsg.getEntity();
		restaurantForm.setRestaurant(restaurant);
	}
}
