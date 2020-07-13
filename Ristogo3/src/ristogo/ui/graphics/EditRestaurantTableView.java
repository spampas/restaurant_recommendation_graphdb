package ristogo.ui.graphics;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.entities.enums.Price;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.beans.UserBean;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.RistogoTableView;

public class EditRestaurantTableView extends RistogoTableView<RestaurantBean>
{
	private UserInfo loggedUser;

	@Override
	protected Collection<TableColumn<RestaurantBean, ?>> generateColumns()
	{
		TableColumn<RestaurantBean, String> nameColumn = new TableColumn<RestaurantBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		nameColumn.setMinWidth(150);
		nameColumn.setMaxWidth(200);

		TableColumn<RestaurantBean, String> typeColumn = new TableColumn<RestaurantBean, String>("Cuisine");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
		typeColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		typeColumn.setMinWidth(150);
		nameColumn.setMaxWidth(200);

		TableColumn<RestaurantBean, String> cityColumn = new TableColumn<RestaurantBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		cityColumn.setMinWidth(150);
		nameColumn.setMaxWidth(200);

		TableColumn<RestaurantBean, Price> priceColumn = new TableColumn<RestaurantBean, Price>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		priceColumn.setMinWidth(150);
		nameColumn.setMaxWidth(200);
		return Arrays.asList(nameColumn, typeColumn, cityColumn, priceColumn);
	}

	protected boolean extractItemsFromMessage(ResponseMessage resMsg)
	{
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the list of restaurants.", resMsg.getErrorMsg()).showAndWait();
			return false;
		}
		items.clear();
		List<RestaurantInfo> restaurants = resMsg.getEntities(RestaurantInfo.class);
		if (restaurants.isEmpty())
			return false;
		for (RestaurantInfo restaurant: restaurants)
			items.add(new RestaurantBean(restaurant.getName(), restaurant.getCuisine().getName(), restaurant.getPrice(), restaurant.getCity().getName()));
		return items.size() >= GUIConfig.getMaxRowDisplayable();
	}

	@Override
	protected boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (filter == null)
			resMsg = Protocol.getInstance().listOwnRestaurants(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		else
			resMsg = Protocol.getInstance().listOwnRestaurants(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		return extractItemsFromMessage(resMsg);
	}

	public void setLoggedUser(UserInfo loggedUser)
	{
		this.loggedUser = loggedUser;
	}

}
