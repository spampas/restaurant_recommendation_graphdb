package ristogo.ui.graphics;

import java.time.LocalDate;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;
import ristogo.ui.graphics.controls.FormLabel;

final class BookForm extends VBox
{
	private final TextField nameField = new TextField();
	private final DatePicker dateField = new DatePicker();
	private final ChoiceBox<ReservationTime> timeField = new ChoiceBox<ReservationTime>();
	private final ChoiceBox<Integer> seatsField = new ChoiceBox<Integer>();
	private final FormButton bookButton = new FormButton("Book");
	private final FormButton deleteButton = new FormButton("Delete");
	private final Label errorLabel = new Label();

	private Runnable onAction;
	private boolean seatsSelected;
	private Restaurant restaurant;
	private Reservation reservation;

	BookForm(Runnable onAction)
	{
		super(20);
		this.onAction = onAction;

		Label title = new Label("Book a table");
		Label subTitle = new Label("To select a restaurant click on the table on the side");
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());
		subTitle.setFont(GUIConfig.getFormSubtitleFont());
		subTitle.setTextFill(GUIConfig.getFgColor());

		FormLabel nameLabel = new FormLabel("Name of Restaurant:");
		FormLabel dateLabel = new FormLabel("Date of Reservation:");
		FormLabel timeLabel = new FormLabel("Booking Time:");
		FormLabel seatsLabel = new FormLabel("Seats:");

		errorLabel.setFont(GUIConfig.getTextFont());
		errorLabel.setTextFill(GUIConfig.getInvertedFgColor());
		errorLabel.setStyle("-fx-background-color: red;");
		errorLabel.setVisible(false);

		nameField.setEditable(false);
		dateField.setDayCellFactory(picker -> new DateCell() { // Disable all past dates
			@Override
			public void updateItem(LocalDate date, boolean empty)
			{
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) < 0);
			}
		});
		timeField.setMinSize(70, 25);
		timeField.setMaxSize(70, 25);
		seatsField.setDisable(true);
		bookButton.setDisable(true);
		deleteButton.setDisable(true);

		HBox nameBox = new HBox(20);
		HBox dateBox = new HBox(20);
		HBox timeBox = new HBox(20);
		HBox seatsBox = new HBox(20);
		HBox buttonBox = new HBox(20);

		nameBox.getChildren().addAll(nameLabel, nameField);
		dateBox.getChildren().addAll(dateLabel, dateField);
		timeBox.getChildren().addAll(timeLabel, timeField);
		seatsBox.getChildren().addAll(seatsLabel, seatsField);
		buttonBox.getChildren().addAll(bookButton, deleteButton);

		getChildren().addAll(title, subTitle, nameBox, dateBox, timeBox,
			seatsBox, errorLabel, buttonBox);
		setStyle(GUIConfig.getCSSFormBoxStyle());

		bookButton.setOnAction(this::handleBookButtonAction);
		deleteButton.setOnAction(this::handleDeleteButtonAction);

		dateField.valueProperty().addListener(this::dateChangeListener);
		timeField.valueProperty().addListener(this::timeChangeListener);
		seatsField.valueProperty().addListener(this::seatsChangeListener);
	}

	private void dateChangeListener(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue)
	{
		if (newValue == null || newValue.isBefore(LocalDate.now())) {
			showError("Invalid date.");
			return;
		}
		hideError();
		checkSeats();
	}

	private void timeChangeListener(ObservableValue<? extends ReservationTime> observable, ReservationTime oldValue, ReservationTime newValue)
	{
		if (newValue == null) {
			showError("Invalid time.");
			return;
		}
		hideError();
		checkSeats();
	}

	private void seatsChangeListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
	{
		if (newValue == null) {
			showError("Select a number of seats.");
			return;
		}
		if (newValue < 1) {
			showError("The number of seats must be > 0");
			return;
		}
		hideError();
	}

	private void checkSeats()
	{
		bookButton.setDisable(true);
		if (errorLabel.isVisible() || dateField.getValue() == null || timeField.getValue() == null)
			return;
		ResponseMessage resMsg;
		if (restaurant == null)
			resMsg = Protocol.getInstance().checkSeats(getReservation());
		else
			resMsg = Protocol.getInstance().checkSeats(getReservation(), restaurant);
		if (!resMsg.isSuccess()) {
			showError(resMsg.getErrorMsg());
			return;
		}
		int maxSeats = ((Restaurant)resMsg.getEntity()).getSeats();
		seatsField.getItems().clear();
		seatsSelected = false;
		if (maxSeats > 0) {
			for (int i = 1; i <= maxSeats; i++)
				seatsField.getItems().add(i);
			seatsField.setDisable(false);
		} else {
			showError("The restaurant does not have any available seat for this date/time.");
			return;
		}
		bookButton.setDisable(false);
		hideError();
	}

	private void handleDeleteButtonAction(ActionEvent event)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteReservation(reservation);
		if (!resMsg.isSuccess()) {
			showError(resMsg.getErrorMsg());
			return;
		}
		reset();
		onAction.run();
	}

	private void handleBookButtonAction(ActionEvent event)
	{
		ResponseMessage resMsg;
		if (restaurant == null)
			resMsg = Protocol.getInstance().editReservation(getReservation());
		else
			resMsg = Protocol.getInstance().reserve(getReservation(), restaurant);
		if (!resMsg.isSuccess()) {
			showError(resMsg.getErrorMsg());
			return;
		}
		reset();
		onAction.run();
	}

	private void reset()
	{
		nameField.setText("");
		dateField.setValue(null);
		timeField.getItems().clear();
		seatsField.getSelectionModel().clearSelection();
		seatsField.setValue(null);
		seatsField.setDisable(true);
		seatsSelected = false;
		bookButton.setText("Book");
		deleteButton.setDisable(true);
		bookButton.setDisable(true);
		restaurant = null;
		reservation = null;
		hideError();
	}

	private void showError(String message)
	{
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		bookButton.setDisable(true);
	}

	private void hideError()
	{
		errorLabel.setVisible(false);
		if (seatsSelected)
			bookButton.setDisable(false);
	}

	private Reservation getReservation()
	{
		if (reservation == null)
			reservation = new Reservation();
		reservation.setRestaurantName(nameField.getText());
		reservation.setDate(dateField.getValue());
		reservation.setTime(timeField.getValue());
		Integer seats = seatsField.getValue();
		if (seats != null)
			reservation.setSeats(seats);
		return reservation;
	}

	void fill(Restaurant restaurant)
	{
		reset();
		this.restaurant = restaurant;
		nameField.setText(restaurant.getName());
		switch(restaurant.getOpeningHours()) {
		case BOTH:
			timeField.getItems().addAll(ReservationTime.LUNCH, ReservationTime.DINNER);
			break;
		default:
			timeField.getItems().add(restaurant.getOpeningHours().toReservationTime());
		}
	}

	void fill(Reservation reservation)
	{
		reset();
		this.reservation = reservation;
		nameField.setText(reservation.getRestaurantName());
		dateField.setValue(reservation.getDate());
		timeField.getItems().addAll(ReservationTime.values());
		timeField.setValue(reservation.getTime());
		checkSeats();
		seatsField.setValue(reservation.getSeats());
		bookButton.setText("Save");
		deleteButton.setDisable(false);
	}
}