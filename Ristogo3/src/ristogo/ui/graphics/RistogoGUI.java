package ristogo.ui.graphics;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ristogo.common.entities.City;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

public final class RistogoGUI extends Application
{
	private static Stage stage;
	private VBox applicationInterface;
	
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
		loggedUser = new Owner("admin", "admin");
		applicationInterface = buildCustomerInterface();
		Scene scene = new Scene(new Group(applicationInterface));
		scene.setFill(GUIConfig.getBgColor());

		primaryStage.setTitle("RistoGo");
		primaryStage.setResizable(true);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/resources/logo.png"));
		primaryStage.show();
		stage = primaryStage;
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
		GridPane title = generateCustomerTitle();
		
		VBox leftPart = new VBox(10);
		VBox leftMenu = new VBox(10);
		Label subtitleUser = new Label("Users Menu");
		subtitleUser.setFont(GUIConfig.getTitleFont());
		subtitleUser.setTextFill(GUIConfig.getFgColor());
		ButtonForm buttonFormLeft = new ButtonForm("restaurant");
		UserViewer userTable = new UserViewer();
		
		HBox optionMenu = generateOptionMenu();
		optionMenu.setAlignment(Pos.CENTER);
		
		leftMenu.getChildren().addAll(buttonFormLeft, userTable);
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
		
		applicationInterface.getChildren().addAll(title, menu, optionMenu);
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
			userTable.getUserTableTitle().setText("List of Users that you follow");
		});
	
		buttonFormLeft.getFindButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
			userTable.getUserTableTitle().setText("List of Users");
		});
		
		buttonFormLeft.getRecommendedButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
			userTable.getUserTableTitle().setText("List of Users recommended to you");
			UserRecommendationDialog recommendationDialog = new UserRecommendationDialog();
			Optional<User> result = recommendationDialog.showAndWait();
			result.ifPresentOrElse(
				data -> { loggedUser = data; },
				() -> { }
			);
		});
		
		buttonFormRight.getMyButton().setOnMouseClicked((event) -> {
				restaurantTable.getLikeButton().setText("Remove Like");
				restaurantTable.getRestaurantTableTitle().setText("List of Restaurants that you like");
		});
		
		buttonFormRight.getFindButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
			restaurantTable.getRestaurantTableTitle().setText("List of Restaurants");
		});
		
		buttonFormRight.getRecommendedButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
			restaurantTable.getRestaurantTableTitle().setText("List of Restaurants recommended to you");
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
		
		leftMenu.getChildren().addAll(cuisineTable);
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

		leftMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		rightMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		leftMenu.setPrefSize(500, 600);
		rightMenu.setPrefSize(500, 600);
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		menu.setPrefSize(1000, 600);	
	
		return adminInterface;
	}

	private VBox buildOwnerInterface()
	{
		VBox ownerInterface = new VBox(20);
		
		HBox titleBox = new HBox(10);
		GridPane title = generateOwnerTitle();
		Button returnButton = new Button("Return to the main page");
		returnButton.setTextFill(GUIConfig.getInvertedFgColor());
		returnButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		returnButton.setOnMouseClicked((event) -> {
			applicationInterface = buildCustomerInterface();
			Scene newScene = new Scene(new Group(applicationInterface));
			stage.setScene(newScene);
			
		});
		
		titleBox.getChildren().addAll(title, returnButton);
		
		HBox restaurantBox = new HBox(10);
		Label restaurantLabel = new Label("Choice one of your restaurant");
		ChoiceBox<String> restaurantSelector = new ChoiceBox<String>();
		restaurantSelector.getItems().addAll(/*TODO request my restaurant name from DB*/);
		restaurantForm = new ModifyRestaurantForm(this::getOwnRestaurant);
		//getOwnRestaurant();

		VBox leftPart = new VBox(10);

		leftPart.getChildren().addAll(restaurantBox, restaurantForm);
		
		
		RestaurantViewer restaurantTable = new RestaurantViewer(true);
		
		RestaurantStatistic statistic = new RestaurantStatistic();
		
		VBox rightPart = new VBox(10);
		
		rightPart.getChildren().addAll(restaurantTable, statistic);
		
		
		HBox menu = new HBox(10);
		menu.getChildren().addAll(leftPart, rightPart);
		
		
		ownerInterface.getChildren().addAll(titleBox, menu);
	
		leftPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		ownerInterface.setPrefSize(1000, 600);
		title.setAlignment(Pos.CENTER);
		ownerInterface.setAlignment(Pos.CENTER);
		
		return ownerInterface;
		
	}
	
	private VBox buildPreferencesInterface()
	{
		VBox preferenceInterface = new VBox(10);
		
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
		
		VBox cuisineMenu = new VBox(10);
		Label subtitleCuisine = new Label("Choose you favourite Cuisines and/or change your City!");
		subtitleCuisine.setFont(GUIConfig.getTitleFont());
		subtitleCuisine.setTextFill(GUIConfig.getFgColor());
		
		CustomerCuisineViewer cuisineTable = new CustomerCuisineViewer();
		
		cuisineMenu.getChildren().addAll(cuisineTable);
		
		
		Label cityLabel = new Label("Select your new city");
		cityLabel.setFont(GUIConfig.getFormTitleFont());
		cityLabel.setTextFill(GUIConfig.getFgColor());
		cityLabel.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		ChoiceBox<City> cityChoice = new ChoiceBox<City>();
		Button commitButton = new Button("Commit");
		
		commitButton.setFont(GUIConfig.getButtonFont());
		commitButton.setTextFill(GUIConfig.getInvertedFgColor());
		commitButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		

		preferenceInterface.getChildren().addAll(titleBox, subtitleCuisine, cityLabel, cityChoice, commitButton, cuisineMenu);
		
		preferenceInterface.setStyle(GUIConfig.getCSSFormBoxStyle());

		cuisineMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		cuisineMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		
		preferenceInterface.setPrefSize(600, 600);	
	
		return preferenceInterface;
	}
	
	
	private GridPane generateTitle() {
		
		Label title = new Label("RistoGo - Recommendations");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		ImageView icon = new ImageView(getClass().getResource("/resources/logo.png").toString());
		icon.setFitHeight(30);
		icon.setFitWidth(30);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 5, 1));
		grid.setMaxWidth(500);

		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		
		return grid;
		
	}
	

	private GridPane generateCustomerTitle()
	{
		GridPane grid = generateTitle();

		Label welcomeLabel = new Label("Welcome");
		welcomeLabel.setFont(GUIConfig.getWelcomeFont());
		welcomeLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername());
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(welcomeLabel, 2, 0);
		grid.add(usernameLabel, 3, 0);

		return grid;
	}
	
	private GridPane generateOwnerTitle()
	{
		GridPane grid = generateTitle();

		Label restaurantsLabel = new Label("Restaurants");
		restaurantsLabel.setFont(GUIConfig.getWelcomeFont());
		restaurantsLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername() + "'s");
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(usernameLabel, 2, 0);
		grid.add(restaurantsLabel, 3, 0);

		return grid;
	}
	
	HBox generateOptionMenu () {
		
		HBox optionMenu = new HBox(10);
		FormButton preferences = new FormButton("Your Preferences");
		FormButton owner = new FormButton("My Restaurants");
		FormButton admin = new FormButton("Admin Settings");
		
		if(loggedUser.isAdmin()) {
			optionMenu.getChildren().addAll(admin, preferences);
		}
		else {
			if(loggedUser.isOwner()) {
				optionMenu.getChildren().addAll(owner, preferences);
			}
			else {
				optionMenu.getChildren().addAll(preferences);
			}
		}
		
		owner.setOnMouseClicked((event) -> {
			applicationInterface = buildOwnerInterface();
			Scene newScene = new Scene(new Group(applicationInterface));
			stage.setScene(newScene);
			
		});
		
		admin.setOnMouseClicked((event) -> {
			applicationInterface = buildAdminInterface();
			Scene newScene = new Scene(new Group(applicationInterface));
			stage.setScene(newScene);
			
		});
		
		preferences.setOnMouseClicked((event) -> {
			applicationInterface = buildPreferencesInterface();
			Scene newScene = new Scene(new Group(applicationInterface));
			stage.setScene(newScene);
			
		});
		
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
