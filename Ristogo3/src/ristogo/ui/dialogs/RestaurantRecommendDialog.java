package ristogo.ui.dialogs;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RecommendRestaurantInfo;
import ristogo.common.net.entities.enums.LikesFrom;
import ristogo.common.net.entities.enums.Price;
import ristogo.net.Protocol;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.controls.CitySelector;
import ristogo.ui.controls.CuisineSelector;
import ristogo.ui.controls.LikesFromSelector;
import ristogo.ui.controls.PriceSelector;
import ristogo.ui.controls.base.DialogLabel;;

public class RestaurantRecommendDialog extends Dialog<RecommendRestaurantInfo>
{
	private final Button searchButton;
	private final DialogLabel likeFilterLabel = new DialogLabel("Order by likes from:");
	private final DialogLabel cityLabel = new DialogLabel("City of restaurants:");
	private final DialogLabel distanceLabel = new DialogLabel("Max distance from the city selected (km):");
	private final DialogLabel cuisineLabel = new DialogLabel("Cuisine:");
	private final DialogLabel priceFilterLabel = new DialogLabel("Maximum price:");
	private final DialogLabel errorLabel = new DialogLabel("Fill out the form.");
	private final LikesFromSelector likeFilterSelector = new LikesFromSelector();
	private final TextField distanceField = new TextField();
	private final CitySelector citySelector = new CitySelector();
	private final CuisineSelector cuisineSelector = new CuisineSelector();
	private final PriceSelector priceFilterSelector = new PriceSelector();
	private final CheckBox airDistanceField = new CheckBox("Air distance");
	private RecommendRestaurantInfo filter;

	public RestaurantRecommendDialog()
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

		ButtonType okButtonType = new ButtonType("Search", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CLOSE);

		searchButton = (Button)dialogPane.lookupButton(okButtonType);

		ResponseMessage resMsg = Protocol.getInstance().getCity();
		if (!resMsg.isSuccess())
			showError(resMsg.getErrorMsg());
		else
			citySelector.setValue(resMsg.getEntity(CityInfo.class).getName());

		distanceField.setPromptText("Distance (km)...");
		distanceField.setText("0");
		likeFilterSelector.setValue(LikesFrom.FRIENDS);
		priceFilterSelector.setValue(Price.LUXURY);
		airDistanceField.setSelected(true);

		distanceField.textProperty().addListener(this::textChangeListener);
		citySelector.textProperty().addListener(this::textChangeListener);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(likeFilterLabel, 0, 0); grid.add(likeFilterSelector, 1, 0);
		grid.add(cityLabel, 0, 1); grid.add(citySelector, 1, 1);
		grid.add(distanceLabel, 0, 2); grid.add(distanceField, 1, 2);
		grid.add(airDistanceField, 1, 3);
		grid.add(cuisineLabel, 0, 4); grid.add(cuisineSelector, 1, 4);
		grid.add(priceFilterLabel, 0, 5); grid.add(priceFilterSelector, 1, 5);
		grid.add(errorLabel, 0, 6, 2, 1);

		searchButton.addEventFilter(ActionEvent.ACTION, this::filterOkButtonAction);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSDialogButtonStyle()));

		dialogPane.setContent(grid);

		this.setResultConverter(button -> {
			if (button == okButtonType)
				return filter;
			return null;
		});
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{
		String distance = distanceField.getText();
		String city = citySelector.getText();
		if (distance == null || distance.isEmpty())
			showError("Distance can not be empty.");
		else if (city == null || city.isEmpty())
			showError("City can not be empty.");
		else try {
			Integer.parseInt(distance);
			hideError();
		} catch (NumberFormatException ex) {
			showError("Distance must be an integer.");
			return;
		}

	}

	private void filterOkButtonAction(ActionEvent event)
	{
		filter = new RecommendRestaurantInfo(
			new CuisineInfo(cuisineSelector.getText()),
			new CityInfo(citySelector.getText()),
			Integer.parseInt(distanceField.getText()),
			airDistanceField.isSelected(),
			likeFilterSelector.getValue(),
			priceFilterSelector.getValue()
		);
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
