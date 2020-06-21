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

		TableColumn<CityBean, String> stateColumn = new TableColumn<CityBean, String>("State");
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
		stateColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		stateColumn.setMinWidth(200);
		stateColumn.setMaxWidth(200);

		TableColumn<CityBean, String> countryColumn = new TableColumn<CityBean, String>("Country");
		countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
		countryColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		countryColumn.setMinWidth(200);
		countryColumn.setMaxWidth(200);
		
		TableColumn<CityBean, String> cityColumn = new TableColumn<CityBean, String>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		cityColumn.setStyle(GUIConfig.getCSSTableColumnStyle(false));
		cityColumn.setMinWidth(200);
		cityColumn.setMaxWidth(200);
		
		getColumns().addAll(stateColumn, countryColumn, cityColumn);
		setItems(cityList);

	}

	City getSelectedEntity()
	{
		CityBean cityBean = getSelectionModel().getSelectedItem();
		return cityBean == null ? null : cityBean.toEntity();
	}

}
