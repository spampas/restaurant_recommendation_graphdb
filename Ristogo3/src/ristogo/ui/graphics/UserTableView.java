package ristogo.ui.graphics;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.entities.Entity;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.common.net.entities.enums.Price;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.*;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.RistogoTableView;


public class UserTableView extends RistogoTableView<UserBean>
{
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
		if (users.isEmpty())
			return false;
		for (UserInfo user: users)
			items.add(new UserBean(user.getUsername(), user.getCity().getName(), user.isFollowing()));
		return items.size() >= GUIConfig.getMaxRowDisplayable();
	}

	@Override
	protected boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (filter == null)
			resMsg = Protocol.getInstance().listUsers(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		else
			resMsg = Protocol.getInstance().listUsers(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		return extractItemsFromMessage(resMsg);
	}
}
