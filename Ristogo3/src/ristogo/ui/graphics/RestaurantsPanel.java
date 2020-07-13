package ristogo.ui.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

import ristogo.ui.graphics.beans.RestaurantBean;

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
	}
}
