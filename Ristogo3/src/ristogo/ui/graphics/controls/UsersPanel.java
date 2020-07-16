package ristogo.ui.graphics.controls;

import java.util.List;
import java.util.Optional;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.RecommendUserInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.UserRecommendDialog;
import ristogo.ui.graphics.beans.UserBean;
import ristogo.ui.graphics.controls.base.MenuBar;

public class UsersPanel extends TablePanel
{
	public UsersPanel(boolean deletable)
	{
		super("Users");
		MenuBar menuBar = new MenuBar();
		ButtonControlBox controlBox = new ButtonControlBox("Follow");
		UsersTableView tv = new UsersTableView();
		PagedTableView<UserBean> ptv = new PagedTableView<UserBean>(tv);
		TextAreaDetailsBox detailsBox = new TextAreaDetailsBox("Cuisines liked by the selected user:");
		ptv.setFindHint("Username...");
		controlBox.setButtonDisable(true);
		menuBar.addMenu("All", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			tv.filter(null);
			ptv.reset();
		});
		menuBar.addMenu("Followers", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			tv.filterFollowers(null);
			ptv.reset();
		});
		menuBar.addMenu("Following", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			tv.filterFollowing(null);
			ptv.reset();
		});
		menuBar.addMenu("Recommend", () -> {
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			detailsBox.setText("");
			UserRecommendDialog login = new UserRecommendDialog();
			Optional<RecommendUserInfo> result = login.showAndWait();
			result.ifPresentOrElse(
				(data) -> { tv.filterRecommend(null, data); },
				() -> { tv.filterRecommend(null, null); }
			);
			ptv.reset();
		});
		setMenuBar(menuBar);
		controlBox.setOnClick(() -> {
			UserBean item = ptv.getSelection();
			if (item == null) {
				controlBox.setButtonDisable(true);
				ptv.setDeleteDisable(true);
				return;
			}
			ResponseMessage resMsg;
			if (!item.isFollowing())
				resMsg = Protocol.getInstance().followUser(new StringFilter(item.getUsername()));
			else
				resMsg = Protocol.getInstance().unfollowUser(new StringFilter(item.getUsername()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to (un)follow the user.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			item.setFollowing(!item.isFollowing());
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
			ResponseMessage resMsg = Protocol.getInstance().getUser(new StringFilter(item.getUsername()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to get user's liked cuisines.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			controlBox.setText(item.isFollowing() ? "Unfollow" : "Follow");
			List<CuisineInfo> cuisines = resMsg.getEntities(CuisineInfo.class);
			StringBuilder sb = new StringBuilder();
			for (CuisineInfo cuisine: cuisines)
				sb.append(cuisine.getName() + System.lineSeparator());
			detailsBox.setText(sb.toString());
		});
		ptv.setDeletable(deletable);
		if (deletable)
			ptv.setOnDelete((item) -> {
				ResponseMessage resMsg = Protocol.getInstance().deleteUser(new StringFilter(item.getUsername()));
				if (!resMsg.isSuccess()) {
					new ErrorBox("Error", "An error has occured while trying to delete the user.", resMsg.getErrorMsg()).showAndWait();
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
