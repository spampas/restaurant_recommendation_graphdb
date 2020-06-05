package ristogo.ui.graphics;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

final class ModifyRestaurantForm extends VBox
{
	private final TextField nameField = new TextField();
	private final ChoiceBox<Cuisine> cuisineField = new ChoiceBox<Cuisine>();
	private final ChoiceBox<Price> priceField = new ChoiceBox<Price>();
	private final TextField cityField = new TextField();
	private final TextArea descriptionField = new TextArea();
	private final FormButton commitButton = new FormButton("Commit");
	private final Label errorLabel = new Label();

	private final Runnable onAction;
	private Restaurant restaurant;

	ModifyRestaurantForm(Runnable onAction)
	{
		super(20);
		this.onAction = onAction;

		Label title = new Label("Modify your Restaurant");
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		FormLabel nameLabel = new FormLabel("Name:");
		FormLabel typeLabel = new FormLabel("Type:");
		FormLabel costLabel = new FormLabel("Cost:");
		FormLabel cityLabel = new FormLabel("City:");
		FormLabel descriptionLabel = new FormLabel("Description:");

		errorLabel.setFont(GUIConfig.getTextFont());
		errorLabel.setTextFill(GUIConfig.getInvertedFgColor());
		errorLabel.setStyle("-fx-background-color:   red;");
		errorLabel.setVisible(false);

		cuisineField.getItems().addAll(Cuisine.values());
		priceField.getItems().addAll(Price.values());
		descriptionField.setWrapText(true);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		HBox nameBox = new HBox(20);
		HBox typeBox = new HBox(20);
		HBox costBox = new HBox(20);
		HBox cityBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		typeBox.getChildren().addAll(typeLabel, cuisineField);
		costBox.getChildren().addAll(costLabel, priceField);
		cityBox.getChildren().addAll(cityLabel, cityField);

		getChildren().addAll(title, nameBox, typeBox, costBox, cityBox, descriptionLabel, descriptionField, errorLabel, commitButton);
		setStyle(GUIConfig.getCSSFormBoxStyle());
		setPrefSize(400, 600);

		nameField.textProperty().addListener(this::changeTextListener);
		cityField.textProperty().addListener(this::changeTextListener);

		commitButton.setOnAction(this::handleCommitButtonAction);
	}

	private void changeTextListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{
		String name = nameField.getText();
		String city = cityField.getText();
		
		if (name == null || name.isBlank() || name.length() > 45) {
			showError("Invalid name.");
			return;
		}
		if (city != null && city.length() > 32) {
			showError("Invalid city.");
			return;
		}

		hideError();
	}

	private void handleCommitButtonAction(ActionEvent event)
	{
		restaurant.setName(nameField.getText());
		restaurant.setCuisine(cuisineField.getValue());
		restaurant.setPrice(priceField.getValue());
		restaurant.setCity(cityField.getText());
		restaurant.setDescription(descriptionField.getText());

		ResponseMessage resMsg = Protocol.getInstance().editRestaurant(restaurant);
		if (!resMsg.isSuccess())
			showError(resMsg.getErrorMsg());
		onAction.run();
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		commitButton.setDisable(true);
	}

	private void hideError()
	{
		errorLabel.setVisible(false);
		commitButton.setDisable(false);
	}

	void setRestaurant(Restaurant restaurant)
	{
		this.restaurant = restaurant;
		nameField.setText(restaurant.getName());
		cuisineField.setValue(restaurant.getCuisine());
		priceField.setValue(restaurant.getPrice());
		cityField.setText(restaurant.getCity());
		descriptionField.setText(restaurant.getDescription());
	}
}
