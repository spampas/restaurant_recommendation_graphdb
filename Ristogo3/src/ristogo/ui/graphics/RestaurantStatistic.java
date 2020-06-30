package ristogo.ui.graphics;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;

public class RestaurantStatistic extends VBox {
	
	
	private final Label title = new Label();
	private final Label likeLabel = new Label();
	private final Label cityLabel = new Label();
	private final Label cuisineLabel = new Label();
	private final Label rankLabel = new Label();
	private final TextField likeField = new TextField();
	private final TextField cityField = new TextField();
	private final TextField cuisineField = new TextField();
	private final TextField rankField = new TextField();
	
	public RestaurantStatistic () {
		
		super(10);
		
		title.setText("Statistics of Selected Restaurant");
		title.setFont(GUIConfig.getFormTitleFont());
		title.setTextFill(GUIConfig.getFgColor());
		title.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		likeLabel.setText("Likes for your restaurant: ");
		likeLabel.setFont(GUIConfig.getFormTitleFont());
		likeLabel.setTextFill(GUIConfig.getFgColor());
		likeLabel.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		cityLabel.setText("Rank of the restaurants in your city: ");
		cityLabel.setFont(GUIConfig.getFormTitleFont());
		cityLabel.setTextFill(GUIConfig.getFgColor());
		cityLabel.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		cuisineLabel.setText("Rank of restaurants with your cuisine: ");
		cuisineLabel.setFont(GUIConfig.getFormTitleFont());
		cuisineLabel.setTextFill(GUIConfig.getFgColor());
		cuisineLabel.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		rankLabel.setText("Rank of restaurants in your city and with your cuisine: ");
		rankLabel.setFont(GUIConfig.getFormTitleFont());
		rankLabel.setTextFill(GUIConfig.getFgColor());
		rankLabel.setStyle(GUIConfig.getCSSFormTitleStyle());
		
		likeField.setMinSize(50, 30);
		likeField.setMaxSize(50, 30);
		
		cityField.setMinSize(50, 30);
		cityField.setMaxSize(50, 30);
		
		cuisineField.setMinSize(50, 30);
		cuisineField.setMaxSize(50, 30);
		
		rankField.setMinSize(50, 30);
		rankField.setMaxSize(50, 30);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setMaxWidth(500);
		
		grid.add(likeLabel, 0, 0);
		grid.add(cityLabel, 0, 1);
		grid.add(cuisineLabel, 0, 2);
		grid.add(rankLabel, 0, 3);

		grid.add(likeField, 1, 0);
		grid.add(cityField, 1, 1);
		grid.add(cuisineField, 1, 2);
		grid.add(rankField, 1, 3);
		
		getChildren().addAll(title, grid);
		
	}

}
