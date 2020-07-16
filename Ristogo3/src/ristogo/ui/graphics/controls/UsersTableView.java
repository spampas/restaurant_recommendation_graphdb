package ristogo.ui.graphics.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RecommendUserInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.beans.UserBean;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.RistogoTableView;

public class UsersTableView extends RistogoTableView<UserBean>
{
	enum FilterType {
		ALL,
		FOLLOWERS,
		FOLLOWING,
		RECOMMEND
	}

	private FilterType filterType = FilterType.ALL;
	private RecommendUserInfo recommendFilter;

	@Override
	protected Collection<TableColumn<UserBean, ?>> generateColumns()
	{
		TableColumn<UserBean, String> nameColumn = new TableColumn<UserBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		nameColumn.setMinWidth(300);
		nameColumn.setMaxWidth(400);

		TableColumn<UserBean, String> cityColumn = new TableColumn<UserBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle());
		cityColumn.setMinWidth(300);
		nameColumn.setMaxWidth(400);

		return Arrays.asList(nameColumn, cityColumn);
	}

	protected boolean extractItemsFromMessage(ResponseMessage resMsg)
	{
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the list of users.", resMsg.getErrorMsg()).showAndWait();
			return false;
		}
		items.clear();
		List<UserInfo> users = resMsg.getEntities(UserInfo.class);
		hasNext = users.size() >= GUIConfig.getMaxRowDisplayable();
		if (users.isEmpty())
			return true;
		for (UserInfo user: users)
			items.add(new UserBean(user.getUsername(), user.getCity().getName(), user.isFollowing()));
		return true;
	}

	@Override
	public boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		switch (filterType) {
		case FOLLOWERS:
			if (filter == null)
				resMsg = Protocol.getInstance().listFollowers(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listFollowers(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			break;
		case FOLLOWING:
			if (filter == null)
				resMsg = Protocol.getInstance().listFollowing(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listFollowing(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			break;
		case RECOMMEND:
			if (filter == null)
				resMsg = Protocol.getInstance().recommendUser(recommendFilter, new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().recommendUser(new StringFilter(filter), recommendFilter, new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			break;
		case ALL:
		default:
			if (filter == null)
				resMsg = Protocol.getInstance().listUsers(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listUsers(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		}
		return extractItemsFromMessage(resMsg);
	}

	public void filter(String filter)
	{
		filterType = FilterType.ALL;
		super.filter(filter);
	}

	public void filterFollowers(String filter)
	{
		filterType = FilterType.FOLLOWERS;
		super.filter(filter);
	}

	public void filterFollowing(String filter)
	{
		filterType = FilterType.FOLLOWING;
		super.filter(filter);
	}

	public void filterRecommend(String filter, RecommendUserInfo recommend)
	{
		filterType = FilterType.RECOMMEND;
		recommendFilter = recommend;
		super.filter(filter);
	}
}
