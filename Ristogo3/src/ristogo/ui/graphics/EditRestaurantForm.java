package ristogo.ui.graphics;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.CitySelector;
import ristogo.ui.graphics.controls.base.CuisineSelector;
import ristogo.ui.graphics.controls.base.DialogLabel;
import ristogo.ui.graphics.controls.base.FormButton;
import ristogo.ui.graphics.controls.base.FormLabel;

final class EditRestaurantForm extends VBox
{
	private final TextField nameField = new TextField();
	private final CuisineSelector cuisineSelector = new CuisineSelector();
	private final CitySelector citySelector = new CitySelector();
	private final ChoiceBox<Price> priceField = new ChoiceBox<Price>();
	private final TextArea descriptionField = new TextArea();
	private final FormButton commitButton = new FormButton("Commit");
	private final FormButton deleteButton = new FormButton("Delete");

	private RestaurantInfo restaurant;

	private EventHandler<ActionEvent> onCommit;
	private EventHandler<ActionEvent> onDelete;

	EditRestaurantForm()
	{
		super(20);

		Label title = new Label("Modify your Restaurant");
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		FormLabel nameLabel = new FormLabel("Name:");
		FormLabel cuisineLabel = new FormLabel("Cuisine:");
		FormLabel costLabel = new FormLabel("Cost:");
		FormLabel cityLabel = new FormLabel("City: ");
		FormLabel descriptionLabel = new FormLabel("Description:");

		priceField.getItems().addAll(Price.values());
		descriptionField.setWrapText(true);
		descriptionField.setMinSize(480, 100);
		descriptionField.setMaxSize(480, 100);

		nameField.textProperty().addListener(this::textChangeListener);
		cuisineSelector.textProperty().addListener(this::textChangeListener);
		citySelector.textProperty().addListener(this::textChangeListener);

		HBox nameBox = new HBox(20);
		HBox typeBox = new HBox(20);
		HBox costBox = new HBox(20);
		HBox cityBox = new HBox(20);
		HBox buttonBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		typeBox.getChildren().addAll(cuisineLabel, cuisineSelector);
		costBox.getChildren().addAll(costLabel, priceField);
		cityBox.getChildren().addAll(cityLabel, citySelector);

		buttonBox.getChildren().addAll(commitButton, deleteButton);

		getChildren().addAll(title, nameBox, typeBox, costBox, cityBox, descriptionLabel, descriptionField, buttonBox);
		setStyle(GUIConfig.getCSSFormBoxStyle());
		setPrefSize(400, 600);

		commitButton.setOnAction(this::handleCommitButtonAction);
		deleteButton.setOnAction(this::handleDeleteButtonAction);
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		String name = nameField.getText();
		String city = citySelector.getText();
		Price price = priceField.getValue();
		String cuisine = cuisineSelector.getText();
		deleteButton.setDisable(restaurant == null);
		commitButton.setDisable(name == null || name.isBlank() || city == null || city.isEmpty() || price == null || cuisine == null || cuisine.isEmpty());
	}

	private void handleCommitButtonAction(ActionEvent event)
	{
		RestaurantInfo newRestaurant = new RestaurantInfo(nameField.getText(),
			new CuisineInfo(cuisineSelector.getText()),
			priceField.getValue(),
			new CityInfo(citySelector.getText()),
			descriptionField.getText());

		ResponseMessage resMsg;
		if (restaurant == null)
			resMsg = Protocol.getInstance().addRestaurant(newRestaurant);
		else
			resMsg = Protocol.getInstance().editRestaurant(new StringFilter(restaurant.getName()), newRestaurant);
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while committing changes.", resMsg.getErrorMsg()).showAndWait();
			return;
		}
		if (onCommit != null)
			onCommit.handle(event);
		setRestaurant(null);
	}

	private void handleDeleteButtonAction(ActionEvent event)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteRestaurant(new StringFilter(restaurant.getName()));
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while deleting the restaurant.", resMsg.getErrorMsg()).showAndWait();
			return;
		}
		if (onDelete != null)
			onDelete.handle(event);
		setRestaurant(null);
	}

	void setRestaurant(RestaurantInfo restaurant)
	{
		this.restaurant = restaurant;
		if (restaurant == null) {
			nameField.clear();
			cuisineSelector.clear();
			priceField.getSelectionModel().clearSelection();;
			citySelector.clear();
			descriptionField.clear();
			commitButton.setText("Create");
			commitButton.setDisable(true);
			deleteButton.setDisable(true);
			return;
		}
		nameField.setText(restaurant.getName());
		cuisineSelector.setText(restaurant.getCuisine().getName());
		priceField.setValue(restaurant.getPrice());
		citySelector.setText(restaurant.getCity().getName());
		descriptionField.setText(restaurant.getDescription());
		commitButton.setDisable(false);
		commitButton.setText("Commit");
		deleteButton.setDisable(false);
	}

	public void setOnCommit(EventHandler<ActionEvent> handler)
	{
		onCommit = handler;
	}

	public void setOnDelete(EventHandler<ActionEvent> handler)
	{
		onDelete = handler;
	}
}
