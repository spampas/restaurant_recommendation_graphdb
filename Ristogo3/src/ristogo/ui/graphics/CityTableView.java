package ristogo.ui.graphics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ristogo.common.entities.City;
import ristogo.common.entities.Cuisine;
import ristogo.ui.graphics.beans.*;
import ristogo.ui.graphics.config.GUIConfig;


public class CityTableView extends TableView<CityBean>{
	
	
	private final ObservableList<CityBean> cityList;

	@SuppressWarnings("unchecked")
	CityTableView()
	{
		cityList = FXCollections.observableArrayList();

		setEditable(false);
		setFixedCellSize(35);
		setMinWidth(600);
		setMaxWidth(600);
		setMaxHeight(GUIConfig.getMaxRowDisplayable() * getFixedCellSize());

		TableColumn<CuisineBean, String> stateColumn = new TableColumn<CuisineBean, String>("State");
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
		stateColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		stateColumn.setMinWidth(150);
		stateColumn.setMaxWidth(150);

		TableColumn<CuisineBean, String> countryColumn = new TableColumn<CuisineBean, String>("Country");
		countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
		countryColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		countryColumn.setMinWidth(150);
		countryColumn.setMaxWidth(150);
		
		TableColumn<CuisineBean, String> cityColumn = new TableColumn<CuisineBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		cityColumn.setMinWidth(150);
		cityColumn.setMaxWidth(150);
		
		getColumns().addAll(stateColumn, countryColumn, cityColumn);
		setItems(cityList);

	}

	City getSelectedEntity()
	{
		CityBean cityBean = getSelectionModel().getSelectedItem();
		return cityBean == null ? null : cityBean.toEntity();
	}

}
