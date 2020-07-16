package ristogo.ui.graphics.controls;

import java.util.Optional;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.RecommendRestaurantInfo;
import ristogo.common.net.entities.RecommendUserInfo;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.RestaurantRecommendDialog;
import ristogo.ui.graphics.UserRecommendDialog;
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
		ptv.setDeleteDisable(true);
		menuBar.addMenu("All", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			ptv.setFilterFunction(tv::filter);
			tv.filter(null);
			ptv.reset();
		});
		menuBar.addMenu("Liked", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			ptv.setFilterFunction(tv::filterLiked);
			tv.filterLiked(null);
			ptv.reset();
		});
		menuBar.addMenu("Recommend", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			ptv.reset();
			RestaurantRecommendDialog login = new RestaurantRecommendDialog();
			Optional<RecommendRestaurantInfo> result = login.showAndWait();
			result.ifPresentOrElse(
				(data) -> { ptv.setFilterFunction(tv::filter); tv.filterRecommend(null, data); }, //TODO
				() -> { }
			);
		});
		setMenuBar(menuBar);
		controlBox.setOnClick(() -> {
			RestaurantBean item = ptv.getSelection();
			if (item == null) {
				controlBox.setButtonDisable(true);
				ptv.setDeleteDisable(true);
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
			ptv.setDeleteDisable(true);
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
				ptv.setDeleteDisable(true);
				detailsBox.setText("");
				ptv.refresh();
			});
		setTableView(ptv);
		setDetailsBox(detailsBox);
		showContent();
	}
}
