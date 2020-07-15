package ristogo.ui.graphics.controls;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.graphics.ErrorBox;
import ristogo.ui.graphics.beans.CityBean;

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
