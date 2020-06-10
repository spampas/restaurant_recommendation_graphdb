package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.DialogLabel;
import ristogo.ui.graphics.controls.DialogPasswordField;
import ristogo.ui.graphics.controls.DialogTextField;

public class UserRecommendationDialog extends Dialog<User> {


	private final Button searchButton;
	private final DialogLabel distanceCityLabel = new DialogLabel("Distance from your city: ");
	private final DialogLabel cuisineLabel = new DialogLabel("Cuisine that he/she likes: ");
	private final DialogLabel errorLabel = new DialogLabel("Fill out the form.");
	private final DialogTextField distanceCityField = new DialogTextField("Distance");
	private final ChoiceBox<Cuisine> cuisineSelector = new ChoiceBox<Cuisine>();
	private User filter;

	UserRecommendationDialog()
	{
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
		dialogPane.setStyle(GUIConfig.getCSSDialogBgColor());
		dialogPane.getStyleClass().remove("alert");

		setTitle("RistoGo - Recommendations User Form");
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
		cuisineSelector.getItems().addAll(Cuisine.ITALIAN, Cuisine.MEXICAN);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(distanceCityLabel, 0, 0); grid.add(distanceCityField, 1, 0);
		grid.add(cuisineLabel, 0, 1); grid.add(cuisineSelector, 1, 1);
		grid.add(errorLabel, 0, 2, 2, 1);

		ButtonType okButtonType = new ButtonType("Search", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CLOSE);

		searchButton = (Button)dialogPane.lookupButton(okButtonType);

		searchButton.addEventFilter(ActionEvent.ACTION, this::filterOkButtonAction);
		
		distanceCityField.textProperty().addListener(this::textChangeListener);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSDialogButtonStyle()));

		dialogPane.setContent(grid);

		Platform.runLater(() -> distanceCityField.requestFocus());

		this.setResultConverter(button -> {
			if (button == okButtonType)
				return filter;
			return null;
		});
	}

	private void filterOkButtonAction(ActionEvent event)
	{
		filter = new Customer("aaaa", "bbbb");
	}
	
	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{
		String distance = distanceCityField.getText();
		if ((distance == null || distance.isEmpty()))
			return;
		
		try{
			Integer.parseInt(distance);
			hideError();
		}catch(NumberFormatException e) {
			showError("Invalid distance: must be a integer.");
		}		
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
