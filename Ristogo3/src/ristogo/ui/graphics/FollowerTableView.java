package ristogo.ui.graphics;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.StringFilter;
import ristogo.common.net.entities.UserInfo;
import ristogo.net.Protocol;

public class FollowerTableView extends UserTableView
{
	protected boolean followers;

	public FollowerTableView(boolean followers)
	{
		super();
		this.followers = followers;
	}

	@Override
	protected boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (filter == null)
			if (followers)
				resMsg = Protocol.getInstance().listFollowers(new PageFilter(page, 10));
			else
				resMsg = Protocol.getInstance().listFollowing(new PageFilter(page, 10));
		else
			if (followers)
				resMsg = Protocol.getInstance().listFollowers(new StringFilter(filter), new PageFilter(page, 10));
			else
				resMsg = Protocol.getInstance().listFollowing(new StringFilter(filter), new PageFilter(page, 10));
		return extractItemsFromMessage(resMsg);
	}
}
