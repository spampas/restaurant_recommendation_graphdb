package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.Cuisine;
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
		cuisineColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
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

}
