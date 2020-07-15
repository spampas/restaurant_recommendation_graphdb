package ristogo.ui.graphics.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RecommendRestaurantInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.entities.enums.Price;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.beans.UserBean;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.RistogoTableView;

public class RestaurantsTableView extends RistogoTableView<RestaurantBean>
{
	enum FilterType {
		ALL,
		LIKED,
		RECOMMEND
	}

	private FilterType filterType = FilterType.ALL;
	private boolean owned;
	private RecommendRestaurantInfo recommendFilter;

	public RestaurantsTableView(boolean owned)
	{
		super();
		this.owned = owned;
	}

	public RestaurantsTableView()
	{
		this(false);
	}

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
	public boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (!owned)
			switch(filterType) {
			case LIKED:
				if (filter == null)
					resMsg = Protocol.getInstance().listLikedRestaurants(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
				else
					resMsg = Protocol.getInstance().listLikedRestaurants(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
				break;
			case RECOMMEND:
			if (filter == null)
				resMsg = Protocol.getInstance().recommendRestaurant(recommendFilter, new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().recommendRestaurant(new StringFilter(filter), recommendFilter, new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			break;
			case ALL:
			default:
				if (filter == null)
					resMsg = Protocol.getInstance().listRestaurants(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
				else
					resMsg = Protocol.getInstance().listRestaurants(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			}
		else
			if (filter == null)
				resMsg = Protocol.getInstance().listOwnRestaurants(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listOwnRestaurants(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		return extractItemsFromMessage(resMsg);
	}

	public void filter(String filter)
	{
		filterType = FilterType.ALL;
		super.filter(filter);
	}

	public void filterLiked(String filter)
	{
		filterType = FilterType.LIKED;
		super.filter(filter);
	}

	public void filterRecommend(String filter, RecommendRestaurantInfo recommend) //TODO
	{
		filterType = FilterType.RECOMMEND;
		recommendFilter = recommend;
		super.filter(filter);
	}
}
