package ristogo.ui.graphics;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.ui.graphics.config.GUIConfig;

public class RestaurantViewer extends VBox {
	
	
	private final Label restaurantTableTitle = new Label();
	private final TextField findField = new TextField();
	private final Button find = new Button();
	private final Button likeButton = new Button();
	private final RestaurantTableView restaurantsTable = new RestaurantTableView();
	private final Label descriptionLabel = new Label();
	private final TextArea descriptionField = new TextArea();
	
	private HBox findBox = new HBox(10);
	

	private Restaurant restaurant;
	
	public RestaurantViewer () {
		
		super(10);
		
		restaurantTableTitle.setText("List of Restaurants");
		restaurantTableTitle.setFont(GUIConfig.getFormTitleFont());
		restaurantTableTitle.setTextFill(GUIConfig.getFgColor());
		restaurantTableTitle.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		findField.setPromptText("insert a name of a restaurant");
		findField.setMinSize(200, 30);
		findField.setMaxSize(200, 30);
		
		find.setText("Find");
		find.setFont(GUIConfig.getButtonFont());
		find.setTextFill(GUIConfig.getInvertedFgColor());
		find.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		
		likeButton.setText("Put Like");
		likeButton.setFont(GUIConfig.getButtonFont());
		likeButton.setTextFill(GUIConfig.getInvertedFgColor());
		likeButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		likeButton.setDisable(true);

		
		findBox.getChildren().addAll(findField, find, likeButton);
		

		
		//restaurantsTable.refreshRestaurants();

		descriptionLabel.setText("Description: ");
		descriptionLabel.setFont(GUIConfig.getBoldVeryTinyTextFont());
		descriptionLabel.setTextFill(GUIConfig.getFgColor());
		
		descriptionField.setWrapText(true);
		descriptionField.setEditable(false);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		HBox descriptionBox = new HBox(20);
		descriptionBox.getChildren().addAll(descriptionLabel, descriptionField);
		
		
		this.getChildren().addAll(restaurantTableTitle,findBox, restaurantsTable, descriptionBox);

	
		restaurantsTable.setOnMouseClicked((event) -> {
			Restaurant restaurant = restaurantsTable.getSelectedEntity();
			if (restaurant == null)
				return;
			
			likeButton.setDisable(false);
			descriptionField.setText(restaurant.getDescription());
		});

		find.setOnAction(this::handleFindButtonAction);
		likeButton.setOnAction(this::handleLikeButtonAction);
	}
	
	public RestaurantViewer (boolean isRestaurantInterface) {
		this();
		if(isRestaurantInterface) {
			findBox.getChildren().remove(2);
			this.getChildren().remove(3);
		}
	}
	
	
	private void handleFindButtonAction(ActionEvent event)
	{
		String name = findField.getText();
		if (name == null)
			return;
		//restaurantsTable.refreshRestaurants(name);
	}
	
	private void handleLikeButtonAction(ActionEvent event)
	{
		//TODO
	}
	
	public void changeConfigurationRestaurantViewer(int config) {
		
		switch(config) {
		
		case 0:
			likeButton.setText("Remove Like");
			likeButton.setVisible(false);
			//TODO: refresh-table
			break;
		case 1:
		case 2:
			likeButton.setText("Put Like");
			likeButton.setVisible(false);
			//TODO: refresh-table
		default:
			break;
		}
	}


	public Button getLikeButton() {
		return likeButton;
	}

	public Label getRestaurantTableTitle() {
		return restaurantTableTitle;
	}
}
