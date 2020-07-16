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
		ptv.setFindHint("Search cuisines...");
		ptv.setDeleteDisable(true);
		controlBox.setOnClick((city) -> {
			if (city == null || city.isEmpty())
				return;
			ResponseMessage resMsg = Protocol.getInstance().addCity(new CityInfo(city));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to add the city.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			ptv.setDeleteDisable(true);
			ptv.refresh();
		});
		setControlBox(controlBox);
		ptv.setOnSelect((item) -> {
			controlBox.setButtonDisable(item == null);
			if (item == null)
				return;
			controlBox.setCity(new CityInfo(item.getName(), item.getLatitude(), item.getLongitude()));
		});

		ptv.setDeletable(true);
		ptv.setOnDelete((item) -> {
			ResponseMessage resMsg = Protocol.getInstance().deleteCity(new StringFilter(item.getName()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to delete the city.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			controlBox.setButtonDisable(true);
			ptv.setDeleteDisable(true);
			ptv.refresh();
		});
		setTableView(ptv);
		showContent();
	}

}
