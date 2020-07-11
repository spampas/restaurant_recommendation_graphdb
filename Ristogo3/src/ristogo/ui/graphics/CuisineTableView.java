package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Cuisine;
import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.graphics.beans.*;
import ristogo.ui.graphics.config.GUIConfig;


public class CuisineTableView extends TableView<CuisineBean>{
	
	
	private final ObservableList<CuisineBean> cuisineList;

	@SuppressWarnings("unchecked")
	CuisineTableView()
	{
		cuisineList = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		TableColumn<CuisineBean, String> cuisineColumn = new TableColumn<CuisineBean, String>("Cuisine");
		cuisineColumn.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
		cuisineColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		cuisineColumn.setMinWidth(600);
		cuisineColumn.setMaxWidth(600);

		getColumns().addAll(cuisineColumn);
		setItems(cuisineList);

	}

	Cuisine getSelectedEntity()
	{
		CuisineBean cuisineBean = getSelectionModel().getSelectedItem();
		return cuisineBean == null ? null : cuisineBean.toEntity();
	}

	void loadCuisines()
	{
		cuisineList.clear();
		ResponseMessage resMsg = Protocol.getInstance().getCuisines();
		if (resMsg.isSuccess())
			for (Entity entity : resMsg.getEntities())
				cuisineList.add(CuisineBean.fromEntity((Cuisine)entity));
		else
			new ErrorBox("Error", "An error has occured while fetching the list of cuisines.", resMsg.getErrorMsg()).showAndWait();
	}
	
	void findCuisine()
	{
		findCuisine(null);
	}

	void findCuisine(String findCuisine)
	{
		cuisineList.clear();
		ResponseMessage resMsg;
		if(findCuisine == null || findCuisine.isBlank()) {
			resMsg = Protocol.getInstance().getCuisines();
		} else {
			Cuisine cuisine = new Cuisine(findCuisine);
			resMsg = Protocol.getInstance().getCuisines(cuisine);
		}
		if (resMsg.isSuccess())
			for (Entity entity : resMsg.getEntities())
				cuisineList.add(CuisineBean.fromEntity((Cuisine)entity));
		else
			new ErrorBox("Error", "An error has occured while fetching the list of cuisines.", resMsg.getErrorMsg()).showAndWait();
	}
	
}
