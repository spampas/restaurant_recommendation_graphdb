package ristogo.ui.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ristogo.common.net.entities.RestaurantInfo;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.FormButton;

public class MainPane extends BasePane
{
	public MainPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected Node createHeader()
	{
		GridPane grid = createHeaderBase();

		Label welcomeLabel = new Label("Welcome");
		welcomeLabel.setFont(GUIConfig.getWelcomeFont());
		welcomeLabel.setTextFill(GUIConfig.getFgColor());

		Label usernameLabel = new Label(loggedUser.getUsername());
		usernameLabel.setFont(GUIConfig.getUsernameFont());
		usernameLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(welcomeLabel, 2, 0);
		grid.add(usernameLabel, 3, 0);

		return grid;
	}
	/*private VBox buildCustomerInterface()
	{
		VBox applicationInterface = new VBox(10);
		HBox menu = new HBox();

		VBox leftPart = new VBox(10);
		VBox leftMenu = new VBox(10);
		Label subtitleUser = new Label("Users Menu");
		subtitleUser.setFont(GUIConfig.getTitleFont());
		subtitleUser.setTextFill(GUIConfig.getFgColor());
		ButtonForm buttonFormLeft = new ButtonForm("restaurant");
		UserViewer userTable = new UserViewer(loggedUser);


		leftMenu.getChildren().addAll(buttonFormLeft, userTable);
		leftPart.getChildren().addAll(subtitleUser, leftMenu);
		leftPart.setAlignment(Pos.CENTER);

		VBox rightPart = new VBox(10);
		VBox rightMenu = new VBox(10);
		Label subtitleRestaurant = new Label("Restaurants Menu");
		subtitleRestaurant.setFont(GUIConfig.getTitleFont());
		subtitleRestaurant.setTextFill(GUIConfig.getFgColor());
		ButtonForm buttonFormRight = new ButtonForm("friend");
		RestaurantViewer restaurantTable = new RestaurantViewer(loggedUser);

		rightMenu.getChildren().addAll(buttonFormRight,restaurantTable);
		rightPart.getChildren().addAll(subtitleRestaurant, rightMenu);
		rightPart.setAlignment(Pos.CENTER);

		menu.getChildren().addAll(leftPart, rightPart);

		applicationInterface.getChildren().addAll(title, menu, optionMenu);
		applicationInterface.setAlignment(Pos.CENTER);

		applicationInterface.setStyle(GUIConfig.getCSSFormBoxStyle());

		leftMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		leftMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		rightMenu.setStyle(GUIConfig.getCSSInterfacePartStyle());
		rightMenu.setStyle(GUIConfig.getCSSFormBoxStyle());
		leftMenu.setPrefSize(500, 600);
		rightMenu.setPrefSize(500, 600);
		leftPart.setPrefSize(500, 600);
		rightPart.setPrefSize(500, 600);
		menu.setPrefSize(1000, 600);


		buttonFormLeft.getMyButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Unfollow");
			userTable.getUserTableTitle().setText("List of Users that you follow");
			userTable.getTable().loadFriends(loggedUser);

		});

		buttonFormLeft.getFindButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
			userTable.getUserTableTitle().setText("List of Users");
			userTable.getTable().loadUser();
		});

		buttonFormLeft.getRecommendedButton().setOnMouseClicked((event) -> {
			userTable.getFollowButton().setText("Follow");
			userTable.getUserTableTitle().setText("List of Users recommended to you");
			UserRecommendationDialog recommendationDialog = new UserRecommendationDialog();
			Optional<UserInfo> result = recommendationDialog.showAndWait();
			result.ifPresentOrElse(
				data -> { loggedUser = data; },
				() -> { }
			);

		});

		buttonFormRight.getMyButton().setOnMouseClicked((event) -> {
				restaurantTable.getLikeButton().setText("Remove Like");
				restaurantTable.getRestaurantTableTitle().setText("List of Restaurants that you like");
				restaurantTable.getTable().loadRestaurants(loggedUser);
		});

		buttonFormRight.getFindButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
			restaurantTable.getRestaurantTableTitle().setText("List of Restaurants");
			restaurantTable.getTable().refreshRestaurants();
		});

		buttonFormRight.getRecommendedButton().setOnMouseClicked((event) -> {
			restaurantTable.getLikeButton().setText("Put Like");
			restaurantTable.getRestaurantTableTitle().setText("List of Restaurants recommended to you");
			RestaurantRecommendationDialog recommendationDialog = new RestaurantRecommendationDialog();
			Optional<RestaurantInfo> result = recommendationDialog.showAndWait();
			result.ifPresentOrElse(
				data -> { restaurant = data; },
				() -> { }
			);
		});

		return applicationInterface;
	}*/

	@Override
	protected Node createLeft()
	{
		return new UsersPanel();
	}

	@Override
	protected Node createCenter()
	{
		return null;
	}

	@Override
	protected Node createRight()
	{
		return new RestaurantsPanel();
	}

	@Override
	protected Node createFooter()
	{
		ToolBar toolBar = new ToolBar();
		FormButton restaurantButton = new FormButton("My Restaurants");
		restaurantButton.setOnAction((event) -> {
			changeView.accept(View.RESTAURANTS);
		});
		toolBar.getItems().add(restaurantButton);
		FormButton prefButton = new FormButton("My Preferences");
		prefButton.setOnAction((event) -> {
			changeView.accept(View.PREFERENCES);
		});
		toolBar.getItems().add(prefButton);
		if (!loggedUser.isAdmin())
			return toolBar;
		FormButton adminButton = new FormButton("Admin Panel");
		adminButton.setOnAction((event) -> {
			changeView.accept(View.ADMIN);
		});
		toolBar.getItems().add(adminButton);
		return toolBar;
	}

}
