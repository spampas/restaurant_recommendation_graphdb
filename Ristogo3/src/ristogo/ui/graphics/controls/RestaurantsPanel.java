package ristogo.ui.graphics.controls;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.beans.UserBean;
import ristogo.ui.graphics.controls.base.MenuBar;

public class RestaurantsPanel extends TablePanel
{
	public RestaurantsPanel(boolean deletable)
	{
		super("Restaurants");
		MenuBar menuBar = new MenuBar();
		ButtonControlBox controlBox = new ButtonControlBox("Like");
		RestaurantsTableView tv = new RestaurantsTableView();
		PagedTableView<RestaurantBean> ptv = new PagedTableView<RestaurantBean>(tv);
		TextAreaDetailsBox detailsBox = new TextAreaDetailsBox("Description:");
		ptv.setFindHint("Name...");
		controlBox.setButtonDisable(true);
		menuBar.addMenu("All", () -> {
			controlBox.setButtonDisable(true);
			detailsBox.setText("");
			tv.filter(null);
		});
		menuBar.addMenu("Liked", () -> {
			controlBox.setButtonDisable(true);
			detailsBox.setText("");
			tv.filterLiked(null);
		});
		menuBar.addMenu("Recommend", () -> {
			controlBox.setButtonDisable(true);
			detailsBox.setText("");
			tv.filterRecommend(null);
		});
		setMenuBar(menuBar);
		controlBox.setOnClick(() -> {
			RestaurantBean item = ptv.getSelection();
			if (item == null) {
				controlBox.setButtonDisable(true);
				return;
			}
			ResponseMessage resMsg;
			if (!item.isLiked())
				resMsg = Protocol.getInstance().likeRestaurant(new StringFilter(item.getName()));
			else
				resMsg = Protocol.getInstance().unlikeRestaurant(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to (un)like the restaurant.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			item.setLiked(!item.isLiked());
			controlBox.setButtonDisable(true);
			detailsBox.setText("");
			ptv.refresh();
		});
		setControlBox(controlBox);
		ptv.setOnSelect((item) -> {
			controlBox.setButtonDisable(item == null);
			detailsBox.setText("");
			if (item == null)
				return;
			ResponseMessage resMsg = Protocol.getInstance().getRestaurant(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to get restaurant's description.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			controlBox.setText(item.isLiked() ? "Unlike" : "Like");
			detailsBox.setText(resMsg.getEntity(RestaurantInfo.class).getDescription());
		});
		ptv.setDeletable(deletable);
		if (deletable)
			ptv.setOnDelete((item) -> {
				ResponseMessage resMsg = Protocol.getInstance().deleteRestaurant(new StringFilter(item.getName()));
				if (!resMsg.isSuccess()) {
					new ErrorBox("Error", "An error has occured while trying to delete the restaurant.", resMsg.getErrorMsg()).showAndWait();
					return;
				}
				controlBox.setButtonDisable(true);
				detailsBox.setText("");
				ptv.refresh();
			});
		setTableView(ptv);
		setDetailsBox(detailsBox);
		showContent();
	}
}
