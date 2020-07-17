package ristogo.ui.panels;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.beans.CityBean;
import ristogo.ui.boxes.AddCityControlBox;
import ristogo.ui.panels.base.TablePanel;
import ristogo.ui.tables.CitiesTableView;
import ristogo.ui.tables.base.PagedTableView;

public class CitiesPanel extends TablePanel
{

	public CitiesPanel()
	{
		super("Cities");

		AddCityControlBox controlBox = new AddCityControlBox();
		CitiesTableView tv = new CitiesTableView();
		PagedTableView<CityBean> ptv = new PagedTableView<CityBean>(tv);
		ptv.setFindHint("Search cities...");
		ptv.setDeleteDisable(true);
		controlBox.setOnClick((cityOld, cityNew) -> {
			if (cityNew == null)
				return;
			ResponseMessage resMsg;
			if (cityOld == null)
				resMsg = Protocol.getInstance().addCity(cityNew);
			else
				resMsg = Protocol.getInstance().editCity(new StringFilter(cityOld.getName()), cityNew);
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to add the city.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			ptv.setDeleteDisable(true);
			ptv.refresh();
		});
		setControlBox(controlBox);
		ptv.setOnSelect((item) -> {
			if (item == null)
				controlBox.clearCity();
			else
				controlBox.setCity(new CityInfo(item.getName(), item.getLatitude(), item.getLongitude()));
		});

		ptv.setDeletable(true);
		ptv.setOnDelete((item) -> {
			ResponseMessage resMsg = Protocol.getInstance().deleteCity(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to delete the city.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			ptv.setDeleteDisable(true);
			controlBox.clearCity();
			ptv.refresh();
		});
		setTableView(ptv);
		showContent();
	}

}
