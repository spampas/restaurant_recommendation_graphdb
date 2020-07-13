package ristogo.ui.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.UserBean;

public class UsersPanel extends MenuPanel
{
	private static SearchableTableView<UserBean> AllTable = new SearchableTableView<UserBean>(new UserTableView());
	private static SearchableTableView<UserBean> FollowersTable = new SearchableTableView<UserBean>(new FollowerTableView(true));
	private static SearchableTableView<UserBean> FollowingTable = new SearchableTableView<UserBean>(new FollowerTableView(false));
	private static SearchableTableView<UserBean> ReccomendTable = new SearchableTableView<UserBean>(new UserTableView());
	private static LinkedHashMap<String, SearchableTableView<?>> map;
	static {
		map = new LinkedHashMap<String, SearchableTableView<?>>();
		map.put("All", AllTable);
		map.put("Followers", FollowersTable);
		map.put("Following", FollowingTable);
		map.put("Reccomend", ReccomendTable);
	}

	public UsersPanel()
	{
		super("Users Menu", map);
		AllTable.setActionButtonText("Follow");
		FollowersTable.setActionButtonText("Follow");
		FollowingTable.setActionButtonText("Unfollow");
		ReccomendTable.setActionButtonText("Follow");
		AllTable.setPromptText("Search users by name");
		FollowersTable.setPromptText("Search users by name");
		FollowingTable.setPromptText("Search users by name");
		ReccomendTable.setPromptText("Search users by name");
		AllTable.setDetailsLabel("Cuisines liked by the selected user:");
		FollowersTable.setDetailsLabel("Cuisines liked by the selected user:");
		FollowingTable.setDetailsLabel("Cuisines liked by the selected user:");
		ReccomendTable.setDetailsLabel("Cuisines liked by the selected user:");
		AllTable.setOnAction((event) -> {
			handleAction(AllTable);
		});
		FollowersTable.setOnAction((event) -> {
			handleAction(FollowersTable);
		});
		FollowingTable.setOnAction((event) -> {
			handleAction(FollowingTable);
		});
		ReccomendTable.setOnAction((event) -> {
			handleAction(ReccomendTable);
		});
		AllTable.setOnSelect((event) -> {
			handleSelection(AllTable);
		});
		FollowersTable.setOnSelect((event) -> {
			handleSelection(FollowersTable);
		});
		FollowingTable.setOnSelect((event) -> {
			handleSelection(FollowingTable);
		});
		ReccomendTable.setOnSelect((event) -> {
			handleSelection(ReccomendTable);
		});
	}

	private void handleSelection(SearchableTableView<UserBean> table)
	{
		UserBean user = table.getSelection();
		if (user == null) {
			table.setActionDisable(true);
			table.setDeleteDisable(true);
			table.setDetails("");
			return;
		}
		table.setDeleteDisable(false);
		table.setActionDisable(false);
		table.setActionButtonText(user.isFollowing() ? "Unfollow" : "Follow");
	}

	private void handleAction(SearchableTableView<UserBean> table)
	{
		UserBean user = table.getSelection();
		if (user == null)
			return;
		ResponseMessage resMsg;
		if (!user.isFollowing())
			resMsg = Protocol.getInstance().followUser(new StringFilter(user.getUsername()));
		else
			resMsg = Protocol.getInstance().unfollowUser(new StringFilter(user.getUsername()));
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while trying to (un)follow the user.", resMsg.getErrorMsg()).showAndWait();
			return;
		}
		user.setFollowing(!user.isFollowing());
		table.refresh();
	}
}
