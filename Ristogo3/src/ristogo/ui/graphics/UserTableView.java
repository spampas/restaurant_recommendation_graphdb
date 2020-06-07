package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.*;
import ristogo.ui.graphics.config.GUIConfig;


public class UserTableView extends TableView<UserBean>{
	
	
	private final ObservableList<UserBean> userList;

	@SuppressWarnings("unchecked")
	UserTableView()
	{
		userList = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		TableColumn<UserBean, String> nameColumn = new TableColumn<UserBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		nameColumn.setMinWidth(150);
		nameColumn.setMaxWidth(150);

		TableColumn<UserBean, String> typeColumn = new TableColumn<UserBean, String>("Birth");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("birth"));
		typeColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		typeColumn.setMinWidth(100);
		typeColumn.setMaxWidth(100);

		TableColumn<UserBean, String> priceColumn = new TableColumn<UserBean, String>("City");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		priceColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		priceColumn.setMinWidth(100);
		priceColumn.setMaxWidth(100);


		getColumns().addAll(nameColumn, typeColumn, priceColumn);
		setItems(userList);

	}

	User getSelectedEntity()
	{
		UserBean userBean = getSelectionModel().getSelectedItem();
		return userBean == null ? null : userBean.toEntity();
	}

	void refreshRestaurants()
	{
		refreshRestaurants(null);
	}

	void refreshRestaurants(String findCity)
	{
		userList.clear();
		ResponseMessage resMsg;
		if(findCity == null || findCity.isBlank()) {
			resMsg = Protocol.getInstance().getRestaurants();
		} else {
			Restaurant restaurant = new Restaurant();
			restaurant.setCity(findCity);
			resMsg = Protocol.getInstance().getRestaurants(restaurant);
		}
		if (resMsg.isSuccess())
			for (Entity entity : resMsg.getEntities())
				userList.add(UserBean.fromEntity((User)entity));
		else
			new ErrorBox("Error", "An error has occured while fetching the list of users.", resMsg.getErrorMsg()).showAndWait();
	}

}
