package ristogo.ui.graphics.controls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.beans.CityBean;
import ristogo.ui.graphics.beans.CuisineBean;
import ristogo.ui.graphics.beans.RestaurantBean;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.base.RistogoTableView;

public class CitiesTableView extends RistogoTableView<CityBean>
{
	@Override
	protected Collection<TableColumn<CityBean, ?>> generateColumns()
	{
		TableColumn<CityBean, String> nameColumn = new TableColumn<CityBean, String>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		nameColumn.setMinWidth(200);
		nameColumn.setMaxWidth(200);

		TableColumn<CityBean, Double> latitudeColumn = new TableColumn<CityBean, Double>("Latitude");
		latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		latitudeColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		latitudeColumn.setMinWidth(200);
		latitudeColumn.setMaxWidth(200);

		TableColumn<CityBean, Double> longitudeColumn = new TableColumn<CityBean, Double>("Longitude");
		longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
		longitudeColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		longitudeColumn.setMinWidth(200);
		longitudeColumn.setMaxWidth(200);
		return Arrays.asList(nameColumn, latitudeColumn, longitudeColumn);
	}

	protected boolean extractItemsFromMessage(ResponseMessage resMsg)
	{
		if (!resMsg.isSuccess()) {
			new ErrorBox("Error", "An error has occured while fetching the list of cities.", resMsg.getErrorMsg()).showAndWait();
			return false;
		}
		items.clear();
		List<CityInfo> cities = resMsg.getEntities(CityInfo.class);
		if (cities.isEmpty())
			return false;
		for (CityInfo city: cities)
			items.add(new CityBean(city.getName(), city.getLatitude(), city.getLongitude()));
		return items.size() >= GUIConfig.getMaxRowDisplayable();
	}

	@Override
	public boolean populateTable(int page)
	{
		ResponseMessage resMsg;
		if (filter == null)
			resMsg = Protocol.getInstance().listCities(new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		else
			resMsg = Protocol.getInstance().listCities(new StringFilter(filter), new PageFilter(page, GUIConfig.getMaxRowDisplayable()));
		return extractItemsFromMessage(resMsg);
	}

}
