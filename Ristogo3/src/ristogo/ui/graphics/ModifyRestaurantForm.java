package ristogo.ui.graphics;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.DialogLabel;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

final class ModifyRestaurantForm extends VBox
{
	private final TextField nameField = new TextField();
	private final ChoiceBox<Cuisine> cuisineField = new ChoiceBox<Cuisine>();
	private final ChoiceBox<String> stateSelector = new ChoiceBox<String>();
	private final ChoiceBox<String> countrySelector = new ChoiceBox<String>();
	private final ChoiceBox<String> citySelector = new ChoiceBox<String>();
	private final ChoiceBox<Price> priceField = new ChoiceBox<Price>();
	private final TextArea descriptionField = new TextArea();
	private final FormButton commitButton = new FormButton("Commit");
	private final FormButton addNewButton = new FormButton("Add new");
	private final FormButton deleteButton = new FormButton("Delete");
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
		FormLabel cuisineLabel = new FormLabel("Cuisine:");
		FormLabel costLabel = new FormLabel("Cost:");
		FormLabel stateLabel = new FormLabel("Stat: ");
		FormLabel countryLabel = new FormLabel("Country: ");
		FormLabel cityLabel = new FormLabel("City: ");
		FormLabel descriptionLabel = new FormLabel("Description:");

		errorLabel.setFont(GUIConfig.getTextFont());
		errorLabel.setTextFill(GUIConfig.getInvertedFgColor());
		errorLabel.setStyle("-fx-background-color:   red;");
		errorLabel.setVisible(false);

		cuisineField.getItems().addAll(Cuisine.values());
		priceField.getItems().addAll(Price.values());
		stateSelector.getItems().addAll(/* Load from DB*/);
		countrySelector.getItems().addAll(/* Laoad from DB*/);
		citySelector.getItems().addAll(/*Load from DB*/);
		descriptionField.setWrapText(true);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		HBox nameBox = new HBox(20);
		HBox typeBox = new HBox(20);
		HBox costBox = new HBox(20);
		GridPane locationBox = new GridPane();
		HBox buttonBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		typeBox.getChildren().addAll(cuisineLabel, cuisineField);
		costBox.getChildren().addAll(costLabel, priceField);
		locationBox.setHgap(10);
		locationBox.setVgap(10);
		locationBox.setPadding(new Insets(20, 150, 10, 10));
		locationBox.add(stateLabel, 0, 0); locationBox.add(stateSelector, 1, 0);
		locationBox.add(countryLabel, 0, 1); locationBox.add(countrySelector, 1, 1);
		locationBox.add(cityLabel, 0, 2); locationBox.add(citySelector, 1, 2);
		buttonBox.getChildren().addAll(commitButton, addNewButton, deleteButton);

		getChildren().addAll(title, nameBox, typeBox, costBox, locationBox, descriptionLabel, descriptionField, errorLabel, buttonBox);
		setStyle(GUIConfig.getCSSFormBoxStyle());
		setPrefSize(400, 600);

		nameField.textProperty().addListener(this::changeTextListener);

		commitButton.setOnAction(this::handleCommitButtonAction);
		addNewButton.setOnAction(this::handleAddNewButtonAction);
		deleteButton.setOnAction(this::handleDeleteButtonAction);
	}

	private void changeTextListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{
		String name = nameField.getText();
		
		if (name == null || name.isBlank() || name.length() > 45) {
			showError("Invalid name.");
			return;
		}

		hideError();
	}

	private void handleCommitButtonAction(ActionEvent event)
	{
		restaurant.setName(nameField.getText());
		restaurant.setCuisine(cuisineField.getValue());
		restaurant.setPrice(priceField.getValue());
		restaurant.setState(stateSelector.getValue());
		restaurant.setCountry(countrySelector.getValue());
		restaurant.setCity(citySelector.getValue());
		restaurant.setDescription(descriptionField.getText());

		ResponseMessage resMsg = Protocol.getInstance().editRestaurant(restaurant);
		if (!resMsg.isSuccess())
			showError(resMsg.getErrorMsg());
		onAction.run();
	}
	
	private void handleAddNewButtonAction(ActionEvent event)
	{
	
	}
	
	private void handleDeleteButtonAction(ActionEvent event)
	{

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
		stateSelector.setValue(restaurant.getState());
		countrySelector.setValue(restaurant.getCountry());
		citySelector.setValue(restaurant.getCity());
		descriptionField.setText(restaurant.getDescription());
	}
}
