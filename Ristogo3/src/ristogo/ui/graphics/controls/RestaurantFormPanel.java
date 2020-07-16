package ristogo.ui.graphics.controls;

import java.util.function.Consumer;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.enums.Price;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.controls.base.CitySelector;
import ristogo.ui.graphics.controls.base.CuisineSelector;
import ristogo.ui.graphics.controls.base.FormButton;
import ristogo.ui.graphics.controls.base.FormLabel;
import ristogo.ui.graphics.controls.base.PriceSelector;

public class RestaurantFormPanel extends BasePanel
{
	private final TextField nameField = new TextField();
	private final CuisineSelector cuisineSelector = new CuisineSelector();
	private final CitySelector citySelector = new CitySelector();
	private final PriceSelector priceField = new PriceSelector();
	private final TextArea descriptionField = new TextArea();
	private final FormButton commitButton = new FormButton("Create");

	private RestaurantInfo restaurant;

	private Consumer<RestaurantInfo> onCommit;

	public RestaurantFormPanel(Consumer<RestaurantInfo> onCommit)
	{
		super("Modify Restaurant");
		this.onCommit = onCommit;

		FormLabel nameLabel = new FormLabel("Name:");
		FormLabel cuisineLabel = new FormLabel("Cuisine:");
		FormLabel costLabel = new FormLabel("Price:");
		FormLabel cityLabel = new FormLabel("City: ");
		FormLabel descriptionLabel = new FormLabel("Description:");

		nameField.textProperty().addListener(this::textChangeListener);
		cuisineSelector.textProperty().addListener(this::textChangeListener);
		citySelector.textProperty().addListener(this::textChangeListener);

		HBox nameBox = new HBox(20);
		HBox cuisineBox = new HBox(20);
		HBox priceBox = new HBox(20);
		HBox cityBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		cuisineBox.getChildren().addAll(cuisineLabel, cuisineSelector);
		priceBox.getChildren().addAll(costLabel, priceField);
		cityBox.getChildren().addAll(cityLabel, citySelector);

		commitButton.setDisable(true);
		commitButton.setOnAction(this::handleCommitButtonAction);

		addAllContent(nameBox, cuisineBox, priceBox, cityBox, descriptionLabel, descriptionField, commitButton);
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		String name = nameField.getText();
		String city = citySelector.getText();
		Price price = priceField.getValue();
		String cuisine = cuisineSelector.getText();
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
		onCommit.accept(newRestaurant);
		setRestaurant(null);
	}

	public void setRestaurant(RestaurantInfo restaurant)
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
			return;
		}
		nameField.setText(restaurant.getName());
		cuisineSelector.setValue(restaurant.getCuisine().getName());
		priceField.setValue(restaurant.getPrice());
		citySelector.setValue(restaurant.getCity().getName());
		descriptionField.setText(restaurant.getDescription());
		commitButton.setDisable(false);
		commitButton.setText("Commit");
	}
}
