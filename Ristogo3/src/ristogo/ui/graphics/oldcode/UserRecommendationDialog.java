package ristogo.ui.graphics.oldcode;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ristogo.common.net.entities.Entity;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.DialogLabel;
import ristogo.ui.graphics.controls.DialogPasswordField;
import ristogo.ui.graphics.controls.DialogTextField;

public class UserRecommendationDialog extends Dialog<UserInfo> {
/*

	private final Button searchButton;
	private final DialogLabel cityLabel = new DialogLabel("City where they live: ");
	private final DialogLabel distanceLabel = new DialogLabel("Distance from the city selected (by default is you city)");
	private final DialogLabel cuisineLabel = new DialogLabel("Cuisine that they like: ");
	private final DialogLabel errorLabel = new DialogLabel("Fill out the form.");
	private final ChoiceBox<String> citySelector = new ChoiceBox<String>();
	private final TextField distanceField = new TextField();
	private final ChoiceBox<String> cuisineSelector = new ChoiceBox<String>();
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
		
		cuisineSelector.getItems().addAll(loadCuisines());
		
		citySelector.getItems().addAll(loadCities());
		
		citySelector.setValue(RistogoGUI.loggedUser.getCity());
		distanceField.setPromptText("Distance(Km)");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(cityLabel, 0, 0); grid.add(citySelector, 1, 0);
		grid.add(distanceLabel, 0, 1); grid.add(distanceField, 1, 1);
		grid.add(cuisineLabel, 0, 2); grid.add(cuisineSelector, 1, 2);
		grid.add(errorLabel, 0, 3, 2, 1);

		ButtonType okButtonType = new ButtonType("Search", ButtonData.OK_DONE);
		dialogPane.getButtonTypes().addAll(okButtonType, ButtonType.CLOSE);

		searchButton = (Button)dialogPane.lookupButton(okButtonType);

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
	
	List<String> loadCities(){
		
		List<String> result = new ArrayList<String>();
		
		ResponseMessage resMsg = Protocol.getInstance().getCities();
		if(resMsg.isSuccess()) {
			for (Entity entity : resMsg.getEntities())
				result.add(((City)entity).getName());
			
			return result;
		}
		else {
			new ErrorBox("Error", "An error has occured while fetching the list of users.", resMsg.getErrorMsg()).showAndWait();
			return null;
		}
	}
	
	List<String> loadCuisines(){
		
		List<String> result = new ArrayList<String>();
		
		ResponseMessage resMsg = Protocol.getInstance().getCuisines();
		if(resMsg.isSuccess()) {
			for (Entity entity : resMsg.getEntities())
				result.add(((Cuisine)entity).getName());
			
			return result;
		}
		else {
			new ErrorBox("Error", "An error has occured while fetching the list of users.", resMsg.getErrorMsg()).showAndWait();
			return null;
		}
	}

	private void filterOkButtonAction(ActionEvent event)
	{
		User filter = new Customer("aaaa", "bbbb");
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
	}*/
}
