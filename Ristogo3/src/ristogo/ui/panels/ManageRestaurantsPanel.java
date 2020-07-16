package ristogo.ui.panels;

import java.util.function.Consumer;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StatisticInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.beans.RestaurantBean;
import ristogo.ui.boxes.RestaurantStatisticsDetailsBox;
import ristogo.ui.panels.base.TablePanel;
import ristogo.ui.tables.RestaurantsTableView;
import ristogo.ui.tables.base.PagedTableView;

public class ManageRestaurantsPanel extends TablePanel
{

	public ManageRestaurantsPanel(Consumer<RestaurantInfo> onSelect)
	{
		super("Your Restaurants");
		RestaurantsTableView tv = new RestaurantsTableView(true);
		PagedTableView<RestaurantBean> ptv = new PagedTableView<RestaurantBean>(tv);
		RestaurantStatisticsDetailsBox detailsBox = new RestaurantStatisticsDetailsBox();
		ptv.setFindHint("Name...");
		ptv.setDeleteDisable(true);
		ptv.setOnSelect((item) -> {
			if (item == null)
				return;
			ResponseMessage resMsg = Protocol.getInstance().getStatisticRestaurant(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to get restaurant's stats.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			detailsBox.setStats(resMsg.getEntity(StatisticInfo.class));
			onSelect.accept(resMsg.getEntity(RestaurantInfo.class));
		});
		ptv.setDeletable(true);
		ptv.setOnDelete((item) -> {
			ResponseMessage resMsg = Protocol.getInstance().deleteRestaurant(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to delete the restaurant.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			ptv.setDeleteDisable(true);
			detailsBox.clear();
			ptv.refresh();
		});
		setTableView(ptv);
		setDetailsBox(detailsBox);
		showContent();
	}
}
