package ristogo.ui.graphics;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.DialogLabel;
import ristogo.ui.graphics.controls.DialogPasswordField;
import ristogo.ui.graphics.controls.DialogTextField;

final class LoginDialog extends Dialog<User>
{
	private boolean registering;
	private final Button okButton;
	private final Button switchButton;
	private final DialogLabel birthLabel = new DialogLabel("Date of Birth: ");
	private final DialogLabel confirmLabel = new DialogLabel("Confirm: ");
	private final DialogLabel typeLabel = new DialogLabel("Type: ");
	private final DialogLabel errorLabel = new DialogLabel("Fill out the form.");
	private final DialogTextField usernameField = new DialogTextField("Username");
	private final DatePicker birthField = new DatePicker();
	private final DialogPasswordField passwordField = new DialogPasswordField("Password");
	private final DialogPasswordField confirmField = new DialogPasswordField("Confirm password");
	private final ChoiceBox<String> typeSelector = new ChoiceBox<String>();
	private User loggedUser;

	LoginDialog()
	{
		DialogPane dialogPane = getDialogPane();
		Stage stage = (Stage)dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
		dialogPane.setStyle(GUIConfig.getCSSDialogBgColor());
		dialogPane.getStyleClass().remove("alert");

		setTitle("RistoGo - Login");
		setHeaderText("Welcome to Ristogo!\nThe application that allows you to book tables at your favorite restaurants!");
		GridPane header = (GridPane)dialogPane.lookup(".header-panel");
		header.setStyle(GUIConfig.getCSSDialogHeaderStyle());
		header.lookup(".label").setStyle(GUIConfig.getCSSDialogFgColor());
		ImageView img = new ImageView(this.getClass().getResource("/resources/whiteLogo.png").toString());
		img.setFitHeight(50);
		img.setFitWidth(50);
		setGraphic(img);

		errorLabel.setStyle("-fx-background-color: red;");
		errorLabel.setVisible(false);
		typeSelector.getItems().addAll("Customer", "Owner");
		birthLabel.setVisible(false); birthField.setVisible(false);
		confirmLabel.setVisible(false); confirmField.setVisible(false);
		typeLabel.setVisible(false); typeSelector.setVisible(false);
		typeSelector.setValue("Customer");

		DialogLabel usernameLabel = new DialogLabel("Username: ");
		DialogLabel passwordLabel = new DialogLabel("Password: ");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(usernameLabel, 0, 0); grid.add(usernameField, 1, 0);
		grid.add(birthLabel, 0, 1); grid.add(birthField, 1, 1);
		grid.add(passwordLabel, 0, 2); grid.add(passwordField, 1, 2);
		grid.add(confirmLabel, 0, 3); grid.add(confirmField, 1, 3);
		grid.add(typeLabel, 0, 4); grid.add(typeSelector, 1, 4);
		grid.add(errorLabel, 0, 5, 2, 1);

		ButtonType okButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType switchButtonType = new ButtonType("Register", ButtonData.OTHER);
		dialogPane.getButtonTypes().addAll(okButtonType, switchButtonType, ButtonType.CLOSE);

		okButton = (Button)dialogPane.lookupButton(okButtonType);
		switchButton = (Button)dialogPane.lookupButton(switchButtonType);

		okButton.addEventFilter(ActionEvent.ACTION, this::filterOkButtonAction);
		switchButton.addEventFilter(ActionEvent.ACTION, this::filterSwitchButtonAction);
		okButton.setDisable(true);

		usernameField.textProperty().addListener(this::textChangeListener);
		passwordField.textProperty().addListener(this::textChangeListener);
		confirmField.textProperty().addListener(this::textChangeListener);

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.setStyle(GUIConfig.getCSSDialogButtonStyle()));

		dialogPane.setContent(grid);

		Platform.runLater(() -> usernameField.requestFocus());

		this.setResultConverter(button -> {
			if (button == okButtonType)
				return loggedUser;
			return null;
		});
	}

	private void filterOkButtonAction(ActionEvent event)
	{
		Protocol protocol = Protocol.getInstance();
		ResponseMessage resMsg;
		String username = usernameField.getText();
		String password = passwordField.getText();
		if (registering) {
			String birth = birthField.getValue().toString();
			if (typeSelector.getValue().equals("Owner"))
				resMsg = protocol.registerUser(new Owner(username, password), new Restaurant(username));
			else
				resMsg = protocol.registerUser(new Customer(username, password));
			if (!resMsg.isSuccess()) {
				showError(resMsg.getErrorMsg());
				event.consume();
				return;
			}
		}
		resMsg = protocol.performLogin(new Customer(username, password));
		if (!resMsg.isSuccess()) {
			showError(resMsg.getErrorMsg());
			event.consume();
			return;
		}
		loggedUser = (User)resMsg.getEntity();
	}

	private void textChangeListener(ObservableValue<? extends String> observable, String oldValue, String newValue)
	{
		validate();
	}

	private void validate()
	{
		String username = usernameField.getText();
		String password = passwordField.getText();
		String confirm = confirmField.getText();
		if ((username == null || username.isEmpty()) && (password == null || password.isEmpty()))
			return;
		if (!User.validateUsername(username))
			showError("Invalid username.");
		else if (!User.validatePassword(password))
			showError("Invalid password.");
		else if (registering && !confirm.equals(password))
			showError("Passwords do not match.");
		else
			hideError();
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		okButton.setDisable(true);
	}

	private void hideError()
	{
		errorLabel.setVisible(false);
		okButton.setDisable(false);
	}

	private void filterSwitchButtonAction(ActionEvent event)
	{
		registering = !registering;
		if (registering) {
			switchButton.setText("Login");
			okButton.setText("Register");
			birthLabel.setVisible(true); birthField.setVisible(true);
			confirmLabel.setVisible(true); confirmField.setVisible(true);
			typeLabel.setVisible(true); typeSelector.setVisible(true);
		} else {
			switchButton.setText("Register");
			okButton.setText("Login");
			birthLabel.setVisible(false); birthField.setVisible(false);
			confirmLabel.setVisible(false); confirmField.setVisible(false);
			typeLabel.setVisible(false); typeSelector.setVisible(false);
		}
		validate();
		event.consume();
	}
}
