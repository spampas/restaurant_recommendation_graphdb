package ristogo.ui.graphics;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ristogo.ui.graphics.config.GUIConfig;

public class ButtonForm extends VBox{

	private String type;
	private Button myButton;
	private Button findButton;
	private Button recommendedButton;
	
	public ButtonForm(String type)
	{
		
		super(10);
		
		this.type = type;
		
		myButton = new Button();
		myButton.setFont(GUIConfig.getButtonFont());
		myButton.setTextFill(GUIConfig.getInvertedFgColor());
		myButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		myButton.setPrefWidth(300);
		
		findButton = new Button();
		findButton.setFont(GUIConfig.getButtonFont());
		findButton.setTextFill(GUIConfig.getInvertedFgColor());
		findButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		findButton.setPrefWidth(300);
		
		recommendedButton = new Button();
		recommendedButton.setFont(GUIConfig.getButtonFont());
		recommendedButton.setTextFill(GUIConfig.getInvertedFgColor());
		recommendedButton.setStyle(GUIConfig.getInvertedCSSButtonBgColor());
		recommendedButton.setPrefWidth(300);
		
		switch(type)
		{
			case "friend": 
				initializeButtonFriend();
				break;
			case "restaurant":
				initializeButtonRestaurant();
				break;
		}
		
		this.getChildren().addAll(findButton, myButton, recommendedButton);
		
	}

	private void initializeButtonRestaurant() {
		
		myButton.setText("View my friends");
		findButton.setText("Find friends");
		recommendedButton.setText("View recommended friends");
	}

	private void initializeButtonFriend() {
		
		myButton.setText("View my favourite restaurants");
		findButton.setText("Find restaurants");
		recommendedButton.setText("View recommended restaurants");
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Button getMyButton() {
		return myButton;
	}

	public Button getFindButton() {
		return findButton;
	}

	public Button getRecommendedButton() {
		return recommendedButton;
	}

	
}
