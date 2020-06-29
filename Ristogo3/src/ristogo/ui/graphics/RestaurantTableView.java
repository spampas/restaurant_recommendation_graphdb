package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Cuisine;
import ristogo.common.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;

final class RestaurantTableView extends TableView<RestaurantBean>
{
	private final ObservableList<RestaurantBean> restaurantList;

	@SuppressWarnings("unchecked")
	RestaurantTableView()
	{
		restaurantList = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		TableColumn<RestaurantBean, String> nameColumn = new TableColumn<RestaurantBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		nameColumn.setMinWidth(150);
		nameColumn.setMaxWidth(150);

		TableColumn<RestaurantBean, Cuisine> typeColumn = new TableColumn<RestaurantBean, Cuisine>("Cuisine");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
		typeColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		typeColumn.setMinWidth(150);
		typeColumn.setMaxWidth(150);
		
		TableColumn<RestaurantBean, String> cityColumn = new TableColumn<RestaurantBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		cityColumn.setMinWidth(150);
		cityColumn.setMaxWidth(150);


		TableColumn<RestaurantBean, Price> priceColumn = new TableColumn<RestaurantBean, Price>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		priceColumn.setMinWidth(150);
		priceColumn.setMaxWidth(150);
		


		getColumns().addAll(nameColumn, typeColumn, priceColumn, cityColumn);
		setItems(restaurantList);

	}

	Restaurant getSelectedEntity()
	{
		RestaurantBean restaurantBean = getSelectionModel().getSelectedItem();
		return restaurantBean == null ? null : restaurantBean.toEntity();
	}

	void refreshRestaurants()
	{
		refreshRestaurants(null);
	}

	void refreshRestaurants(String findCity)
	{
		restaurantList.clear();
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
				restaurantList.add(RestaurantBean.fromEntity((Restaurant)entity));
		else
			new ErrorBox("Error", "An error has occured while fetching the list of restaurants.", resMsg.getErrorMsg()).showAndWait();
	}
}
