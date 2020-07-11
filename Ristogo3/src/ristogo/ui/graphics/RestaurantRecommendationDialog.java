package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Price;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.DialogLabel;
import ristogo.ui.graphics.controls.DialogTextField;
import ristogo.ui.menus.forms.FormField;

public class RestaurantRecommendationDialog extends Dialog<Restaurant> {

	private final Button searchButton;
	private final DialogLabel likeFilterLabel = new DialogLabel("Consider likes coming from: ");
	private final DialogLabel cityLabel = new DialogLabel("City where they are placed: ");
	private final DialogLabel distanceLabel = new DialogLabel("Distance from the city selected (by default is you city)");
	private final DialogLabel cuisineLabel = new DialogLabel("Cuisine that you like: ");
	private final DialogLabel priceFilterLabel = new DialogLabel("Price:");
	private final DialogLabel errorLabel = new DialogLabel("Fill out the form.");
	private final ChoiceBox<String> likeFilterSelector = new ChoiceBox<String>();
	private final TextField distanceField = new TextField();
	private final ChoiceBox<String> citySelector = new ChoiceBox<String>();
	private final ChoiceBox<Cuisine> cuisineSelector = new ChoiceBox<Cuisine>();
	private final ChoiceBox<Price> priceFilterSelector = new ChoiceBox<Price>();
	private Restaurant filter;

	RestaurantRecommendationDialog()
	{
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
		dialogPane.setStyle(GUIConfig.getCSSDialogBgColor());
		dialogPane.getStyleClass().remove("alert");

		setTitle("RistoGo - Recommendations Restaurant Form");
		setHeaderText("Select your preferences");
		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getCSSDialogHeaderStyle());
		header.lookup(".label").setStyle(GUIConfig.getCSSDialogFgColor());
		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);

		errorLabel.setStyle("-fx-background-color: red;");
		errorLabel.setVisible(false);
		
		likeFilterSelector.getItems().addAll("Friends", "Friends of Friends");
		cuisineSelector.getItems().addAll(/* TODO load cuisine from DB */);
		priceFilterSelector.getItems().addAll(Price.ECONOMIC, Price.LOW, Price.MIDDLE, Price.HIGH, Price.LUXURY);
		
		citySelector.getItems().addAll(/*//TODO Load cities from DB*/);
		
		citySelector.setValue(RistogoGUI.loggedUser.getCity());
		distanceField.setPromptText("Distance(Km)");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(likeFilterLabel, 0, 0); grid.add(likeFilterSelector, 1, 0);
		grid.add(cityLabel, 0, 1); grid.add(citySelector, 1, 1);
		grid.add(distanceLabel, 0, 2); grid.add(distanceField, 1, 2);
		grid.add(cuisineLabel, 0, 3); grid.add(cuisineSelector, 1, 3);
		grid.add(priceFilterLabel, 0, 4); grid.add(priceFilterSelector, 1, 4);
		grid.add(errorLabel, 0, 5, 2, 1);

		ButtonType okButtonType = new ButtonType("Search", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CLOSE);

		searchButton = (Button)dialogPane.lookupButton(okButtonType);

		searchButton.addEventFilter(ActionEvent.ACTION, this::filterOkButtonAction);
		

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSDialogButtonStyle()));

		dialogPane.setContent(grid);

		//Platform.runLater(() -> distanceCityField.requestFocus());

		this.setResultConverter(button -> {
			if (button == okButtonType)
				return filter;
			return null;
		});
	}

	private void filterOkButtonAction(ActionEvent event)
	{
		filter = new Restaurant("fffddge");
		//TODO construct the object to return to main interface
	}
	
	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{	
	}
	
	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		searchButton.setDisable(true);
	}

	private void hideError()
	{
		errorLabel.setVisible(false);
		searchButton.setDisable(false);
	}
}
