package ristogo.ui.graphics;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.entities.enums.Price;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;

public class EditRestaurantsPanel extends MenuPanel
{
	/*private static SearchableTableView<RestaurantBean> table = new SearchableTableView<RestaurantBean>(new EditRestaurantTableView());
	private static LinkedHashMap<String, SearchableTableView<?>> map;
	static {
		map = new LinkedHashMap<String, SearchableTableView<?>>();
		map.put("", table);
	}

	public EditRestaurantsPanel(String title, UserInfo loggedUser, Consumer<RestaurantInfo> onSelect)
	{
		super(title, map);
		table.setActionButtonVisible(false);
		table.setPromptText("Search restaurants by name");
		table.setDetailsLabel("Description of selected restaurant:");
		((EditRestaurantTableView)table.getTableView()).setLoggedUser(loggedUser);
		table.setOnSelect((event) -> {
			RestaurantBean bean = table.getSelection();
			if (bean == null) {
				table.setDetails("");
				onSelect.accept(null);
				return;
			}
			ResponseMessage resMsg = Protocol.getInstance().getRestaurant(new StringFilter(bean.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while fetching the restaurant details.", resMsg.getErrorMsg()).showAndWait();
				table.setDetails("");
				onSelect.accept(null);
				return;
			}
			RestaurantInfo restaurant = resMsg.getEntity(RestaurantInfo.class);
			table.setDetails(restaurant.getDescription());
			onSelect.accept(restaurant);
		});
	}

	public void refresh()
	{
		table.refresh();
	}*/

}
