package ristogo.ui.graphics;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;

public class Prova {
	public static User loggedUser;
	private static Restaurant restaurant;

	private static ModifyRestaurantForm restaurantForm;
	
	public Prova () {
		
VBox applicationInterface = new VBox(20);
		
		HBox titleBox = new HBox(10);
		GridPane title = generateOwnerTitle();
		Button returnButton = new Button("Return to the main page");
		returnButton.setTextFill(GUIConfig.getInvertedFgColor());
		returnButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
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
		
		//TODO : add statistics of restaurant
		
		VBox rightPart = new VBox(10);
		
		rightPart.getChildren().addAll(restaurantTable);
		
		
		HBox menu = new HBox(10);
		menu.getChildren().addAll(leftPart, rightPart);
		
		
		applicationInterface.getChildren().addAll(titleBox, menu);
	
		leftPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		applicationInterface.setPrefSize(1000, 600);
		title.setAlignment(Pos.CENTER);
		applicationInterface.setAlignment(Pos.CENTER);
		
		return applicationInterface;
		
	}
	
	private GridPane generateOwnerTitle()
	{
		Label title = new Label("RistoGo - Recommendations");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		ImageView icon = new ImageView(getClass().getResource("/resources/logo.png").toString());
		icon.setFitHeight(30);
		icon.setFitWidth(30);

		Label restaurantsLabel = new Label("Restaurants");
		restaurantsLabel.setFont(GUIConfig.getWelcomeFont());
		restaurantsLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername() + "'s");
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 5, 1));
		grid.setMaxWidth(500);

		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);
		grid.add(usernameLabel, 2, 0);
		grid.add(restaurantsLabel, 3, 0);

		return grid;
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
