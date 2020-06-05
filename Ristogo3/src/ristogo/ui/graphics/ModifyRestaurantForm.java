package ristogo.ui.graphics;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

final class ModifyRestaurantForm extends VBox
{
	private final TextField nameField = new TextField();
	private final ChoiceBox<Genre> genreField = new ChoiceBox<Genre>();
	private final ChoiceBox<Price> priceField = new ChoiceBox<Price>();
	private final TextField cityField = new TextField();
	private final TextField addressField = new TextField();
	private final TextArea descriptionField = new TextArea();
	private final Spinner<Integer> seatsField = new Spinner<Integer>(0, Integer.MAX_VALUE, 0, 1);
	private final ChoiceBox<OpeningHours> hourField = new ChoiceBox<OpeningHours>();
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
		FormLabel addressLabel = new FormLabel("Address:");
		FormLabel descriptionLabel = new FormLabel("Description:");
		FormLabel seatsLabel = new FormLabel("Seats:");
		FormLabel hourLabel = new FormLabel("Opening Hours:");

		errorLabel.setFont(GUIConfig.getTextFont());
		errorLabel.setTextFill(GUIConfig.getInvertedFgColor());
		errorLabel.setStyle("-fx-background-color:   red;");
		errorLabel.setVisible(false);

		genreField.getItems().addAll(Genre.values());
		priceField.getItems().addAll(Price.values());
		descriptionField.setWrapText(true);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);
		hourField.getItems().addAll(OpeningHours.values());

		HBox nameBox = new HBox(20);
		HBox typeBox = new HBox(20);
		HBox costBox = new HBox(20);
		HBox cityBox = new HBox(20);
		HBox addressBox = new HBox(20);
		HBox seatsBox = new HBox(20);
		HBox hourBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		typeBox.getChildren().addAll(typeLabel, genreField);
		costBox.getChildren().addAll(costLabel, priceField);
		cityBox.getChildren().addAll(cityLabel, cityField);
		addressBox.getChildren().addAll(addressLabel, addressField);
		seatsBox.getChildren().addAll(seatsLabel, seatsField);
		hourBox.getChildren().addAll(hourLabel, hourField);

		getChildren().addAll(title, nameBox, typeBox, costBox, cityBox, addressBox, descriptionLabel, descriptionField,
			seatsBox, hourBox, errorLabel, commitButton);
		setStyle(GUIConfig.getCSSFormBoxStyle());
		setPrefSize(400, 600);

		nameField.textProperty().addListener(this::changeTextListener);
		cityField.textProperty().addListener(this::changeTextListener);
		addressField.textProperty().addListener(this::changeTextListener);
		seatsField.valueProperty().addListener(this::changeSeatsListener);

		commitButton.setOnAction(this::handleCommitButtonAction);
	}

	private void changeTextListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void changeSeatsListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
	{
		validate();
	}

	private void validate()
	{
		String name = nameField.getText();
		String city = cityField.getText();
		String address = addressField.getText();
		Integer seats = seatsField.getValue();
		if (name == null || name.isBlank() || name.length() > 45) {
			showError("Invalid name.");
			return;
		}
		if (city != null && city.length() > 32) {
			showError("Invalid city.");
			return;
		}
		if (address != null && address.length() > 32) {
			showError("Invalid address.");
			return;
		}
		if (seats == null || seats < 0) {
			showError("Invalid seats.");
			return;
		}
		hideError();
	}

	private void handleCommitButtonAction(ActionEvent event)
	{
		restaurant.setName(nameField.getText());
		restaurant.setGenre(genreField.getValue());
		restaurant.setPrice(priceField.getValue());
		restaurant.setCity(cityField.getText());
		restaurant.setAddress(addressField.getText());
		restaurant.setDescription(descriptionField.getText());
		restaurant.setSeats(seatsField.getValue());
		restaurant.setOpeningHours(hourField.getValue());

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
		genreField.setValue(restaurant.getGenre());
		priceField.setValue(restaurant.getPrice());
		cityField.setText(restaurant.getCity());
		addressField.setText(restaurant.getAddress());
		descriptionField.setText(restaurant.getDescription());
		seatsField.getValueFactory().setValue(restaurant.getSeats());
		hourField.setValue(restaurant.getOpeningHours());
	}
}
