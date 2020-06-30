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
		likeLabel.setTextFill(GUIConfig.getFgColor());
		
		cityLabel.setText("Rank of the restaurants in your city: ");
		cityLabel.setTextFill(GUIConfig.getFgColor());
		
		cuisineLabel.setText("Rank of restaurants with your cuisine: ");

		cuisineLabel.setTextFill(GUIConfig.getFgColor());
		
		rankLabel.setText("Rank of restaurants in your city and with your cuisine: ");
		rankLabel.setTextFill(GUIConfig.getFgColor());
		
		//TODO: Caricare valore dal db
		likeField.setEditable(false);
		likeField.setMinSize(50, 30);
		likeField.setMaxSize(50, 30);
		
		//TODO: Caricare valore dal db
		cityField.setEditable(false);
		cityField.setMinSize(50, 30);
		cityField.setMaxSize(50, 30);
		
		//TODO: Caricare valore dal db
		cuisineField.setEditable(false);
		cuisineField.setMinSize(50, 30);
		cuisineField.setMaxSize(50, 30);
		
		//TODO: Caricare valore dal db
		rankField.setEditable(false);
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
