package ristogo.ui.boxes;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.beans.CuisineBean;
import ristogo.ui.tables.CuisinesTableView;
import ristogo.ui.tables.base.PagedTableView;

public class CuisinesDetailsBox extends TableDetailsBox
{
	public CuisinesDetailsBox()
	{
		super("Add your favourite cuisines:");

		AddCuisineControlBox controlBox = new AddCuisineControlBox(true);
		CuisinesTableView tv = new CuisinesTableView(true);
		PagedTableView<CuisineBean> ptv = new PagedTableView<CuisineBean>(tv);
		ptv.setFindHint("Search liked cuisines...");
		ptv.setDeleteDisable(true);
		controlBox.setOnClick((cuisineOld, cuisine) -> {
			if (cuisine == null || cuisine.getName().isEmpty())
				return;
			ResponseMessage resMsg = Protocol.getInstance().likeCuisine(new StringFilter(cuisine.getName()));
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
		ptv.setDeleteButtonText("Remove");
		ptv.setOnDelete((item) -> {
			ResponseMessage resMsg = Protocol.getInstance().unlikeCuisine(new StringFilter(item.getCuisine()));
			if (!resMsg.isSuccess()) {
				new ErrorBox("Error", "An error has occured while trying to remove the cuisine.", resMsg.getErrorMsg()).showAndWait();
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
