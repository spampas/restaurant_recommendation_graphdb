package ristogo.ui.graphics;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.ReservationBean;
import ristogo.ui.graphics.config.GUIConfig;

final class TableViewReservation extends TableView<ReservationBean>
{
	private final ObservableList<ReservationBean> reservationList;
	private final Restaurant restaurant;

	TableViewReservation()
	{
		this(null);
	}

	@SuppressWarnings("unchecked")
	TableViewReservation(Restaurant restaurant)
	{
		this.restaurant = restaurant;
		reservationList = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable(restaurant != null) * getFixedCellSize());

		TableColumn<ReservationBean, String> nameColumn = new TableColumn<ReservationBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>(restaurant != null ? "userName" : "restaurantName"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		nameColumn.setMinWidth(300);
		nameColumn.setMaxWidth(300);

		TableColumn<ReservationBean, LocalDate> dateColumn = new TableColumn<ReservationBean, LocalDate>("Date");
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		dateColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		dateColumn.setMinWidth(150);
		dateColumn.setMaxWidth(150);

		TableColumn<ReservationBean, ReservationTime> hourColumn = new TableColumn<ReservationBean, ReservationTime>("Hour");
		hourColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
		hourColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		hourColumn.setMinWidth(100);
		hourColumn.setMaxWidth(100);

		TableColumn<ReservationBean, Integer> seatsColumn = new TableColumn<ReservationBean, Integer>("Seats");
		seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seats"));
		seatsColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		seatsColumn.setMinWidth(50);
		seatsColumn.setMaxWidth(50);

		getColumns().addAll(nameColumn, dateColumn, hourColumn, seatsColumn);
		setItems(reservationList);
	}

	Reservation getSelectedEntity()
	{
		ReservationBean reservationBean = getSelectionModel().getSelectedItem();
		return reservationBean == null ? null : reservationBean.toEntity();
	}

	void refreshReservations()
	{
		reservationList.clear();
		ResponseMessage resMsg;
		if (restaurant != null)
			resMsg = Protocol.getInstance().getReservations(restaurant);
		else
			resMsg = Protocol.getInstance().getOwnActiveReservations();
		if (resMsg.isSuccess())
			for (Entity entity : resMsg.getEntities())
				reservationList.add(ReservationBean.fromEntity((Reservation)entity));
		else
			new ErrorBox("Error", "An error has occured while fetching the list of reservations.", resMsg.getErrorMsg()).showAndWait();
	}
}
