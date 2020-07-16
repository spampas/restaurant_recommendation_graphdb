package ristogo.ui.panels;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.beans.CuisineBean;
import ristogo.ui.boxes.AddCuisineControlBox;
import ristogo.ui.panels.base.TablePanel;
import ristogo.ui.tables.CuisinesTableView;
import ristogo.ui.tables.base.PagedTableView;

public class CuisinesPanel extends TablePanel
{

	public CuisinesPanel()
	{
		super("Cuisines");

		AddCuisineControlBox controlBox = new AddCuisineControlBox(false);
		CuisinesTableView tv = new CuisinesTableView(true);
		PagedTableView<CuisineBean> ptv = new PagedTableView<CuisineBean>(tv);
		ptv.setFindHint("Search cuisines...");
		ptv.setDeleteDisable(true);
		controlBox.setOnClick((cuisine) -> {
			if (cuisine == null || cuisine.isEmpty())
				return;
			ResponseMessage resMsg = Protocol.getInstance().addCuisine(new CuisineInfo(cuisine));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to add the cuisine.", resMsg.getErrorMsg()).showAndWait();
				return;
			}
			ptv.setDeleteDisable(true);
			ptv.refresh();
		});
		setControlBox(controlBox);
		ptv.setOnSelect((item) -> {
		});
		ptv.setDeletable(true);
		ptv.setOnDelete((item) -> {
			ResponseMessage resMsg = Protocol.getInstance().deleteCuisine(new StringFilter(item.getCuisine()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to delete the cuisine.", resMsg.getErrorMsg()).showAndWait();
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
