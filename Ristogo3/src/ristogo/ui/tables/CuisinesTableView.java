package ristogo.ui.tables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.beans.CuisineBean;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.tables.base.RistogoTableView;

public class CuisinesTableView extends RistogoTableView<CuisineBean>
{
	private boolean liked;

	public CuisinesTableView(boolean liked)
	{
		super();
		this.liked = liked;
	}

	@Override
	protected Collection<TableColumn<CuisineBean, ?>> generateColumns()
	{
		TableColumn<CuisineBean, String> cuisineColumn = new TableColumn<CuisineBean, String>("Cuisine");
		cuisineColumn.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
		cuisineColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		cuisineColumn.setMinWidth(350);
		cuisineColumn.setMaxWidth(400);
		return Arrays.asList(cuisineColumn);
	}

	protected boolean extractItemsFromMessage(ResponseMessage resMsg)
	{
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the list of cuisines.", resMsg.getErrorMsg()).showAndWait();
			return false;
		}
		items.clear();
		List<CuisineInfo> cuisines = resMsg.getEntities(CuisineInfo.class);
		hasNext = cuisines.size() >= GUIConfig.getMaxRowDisplayable();
		if (cuisines.isEmpty())
			return true;
		for (CuisineInfo cuisine: cuisines)
			items.add(new CuisineBean(cuisine.getName()));
		return true;
	}

	@Override
	public boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (liked)
			if (filter == null)
				resMsg = Protocol.getInstance().listLikedCuisines(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listLikedCuisines(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		else
			if (filter == null)
				resMsg = Protocol.getInstance().listCuisines(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
			else
				resMsg = Protocol.getInstance().listCuisines(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		return extractItemsFromMessage(resMsg);
	}

}
