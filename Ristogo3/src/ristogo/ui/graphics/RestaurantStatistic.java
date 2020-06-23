package ristogo.ui.graphics;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;

public class RestaurantStatistic extends VBox {
	
	
	private final Label title = new Label();
	
	
	public RestaurantStatistic () {
		
		super(10);
		
		title.setText("Statistics of Selected Restaurant");
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		
		getChildren().addAll(title);
		
		
		
		
	}

}
