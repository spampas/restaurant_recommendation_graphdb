package ristogo.ui.graphics;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
		
		HBox applicationInterface = new HBox(20);

		//GridPane title = generateTitle();
		HBox titleBox = new HBox(10);
		GridPane title = new GridPane();
		Button returnButton = new Button("Return to the main page");
		returnButton.setTextFill(GUIConfig.getInvertedFgColor());
		returnButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		titleBox.getChildren().addAll(title, returnButton);
		
		HBox restaurantBox = new HBox(10);
		Label restaurantLabel = new Label("Choice one of your restaurant");
		ChoiceBox<String> restaurantSelector = new ChoiceBox<String>();
		restaurantSelector.getItems().addAll(/*TODO request my restaurant name from DB*/);
		restaurantForm = new ModifyRestaurantForm(this::getOwnRestaurant);
		getOwnRestaurant();

		VBox leftPart = new VBox(10);
		VBox rightPart = new VBox(10);

		leftPart.getChildren().addAll(titleBox, restaurantBox, restaurantForm);
		
		
		
		VBox buttonBox = new VBox(10);
		Button addButton = new Button("Add new Restaurant");
		addButton.setTextFill(GUIConfig.getInvertedFgColor());
		addButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		addButton.setPrefWidth(300);
		Button deleteButton = new Button("Delete selected Restaurant");
		deleteButton.setTextFill(GUIConfig.getInvertedFgColor());
		deleteButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		deleteButton.setPrefWidth(300);
		
		leftPart.getChildren().addAll(buttonBox);
		
		applicationInterface.getChildren().addAll(leftPart, rightPart);
		
	
		leftPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightPart.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftPart.setPrefSize(400, 600);
		rightPart.setPrefSize(600, 600);
		rightPart.setAlignment(Pos.CENTER);
		applicationInterface.setPrefSize(1000, 600);
		title.setAlignment(Pos.CENTER);
		applicationInterface.setAlignment(Pos.CENTER);
		
		

		
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
