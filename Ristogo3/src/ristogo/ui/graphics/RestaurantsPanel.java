package ristogo.ui.graphics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.beans.UserBean;

public class RestaurantsPanel extends MenuPanel
{
	private static SearchableTableView<RestaurantBean> AllTable = new SearchableTableView<RestaurantBean>(new RestaurantTableView(false));
	private static SearchableTableView<RestaurantBean> LikedTable = new SearchableTableView<RestaurantBean>(new RestaurantTableView(true));
	private static SearchableTableView<RestaurantBean> ReccomendTable = new SearchableTableView<RestaurantBean>(new RestaurantTableView(false));
	private static LinkedHashMap<String, SearchableTableView<?>> map;
	static {
		map = new LinkedHashMap<String, SearchableTableView<?>>();
		map.put("All", AllTable);
		map.put("Liked", LikedTable);
		map.put("Reccomend", ReccomendTable);
	}

	public RestaurantsPanel()
	{
		super("Restaurants Menu", map);
		AllTable.setActionButtonText("Like");
		LikedTable.setActionButtonText("Disike");
		ReccomendTable.setActionButtonText("Like");
		AllTable.setPromptText("Search restaurants by name");
		LikedTable.setPromptText("Search restaurants by name");
		ReccomendTable.setPromptText("Search restaurants by name");
		AllTable.setDetailsLabel("Description of selected restaurant:");
		LikedTable.setDetailsLabel("Description of selected restaurant:");
		ReccomendTable.setDetailsLabel("Description of selected restaurant:");
		AllTable.setOnSelect((event) -> {
			handleSelection(AllTable);
		});
		LikedTable.setOnSelect((event) -> {
			handleSelection(LikedTable);
		});
		ReccomendTable.setOnSelect((event) -> {
			handleSelection(ReccomendTable);
		});
	}

	private void handleSelection(SearchableTableView<RestaurantBean> table)
	{
		RestaurantBean restaurant = table.getSelection();
		if (restaurant == null) {
			table.setActionDisable(true);
			table.setDeleteDisable(true);
			table.setDetails("");
			return;
		}
		ResponseMessage resMsg = Protocol.getInstance().getRestaurant(new StringFilter(restaurant.getName()));
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the user details.", resMsg.getErrorMsg()).showAndWait();
			return;
		}
		RestaurantInfo r = resMsg.getEntity(RestaurantInfo.class);
		table.setDetails(r.getDescription());
		table.setDeleteDisable(false);
		table.setActionDisable(false);
		table.setActionButtonText(r.isLiked() ? "Unfollow" : "Follow");
	}
}
