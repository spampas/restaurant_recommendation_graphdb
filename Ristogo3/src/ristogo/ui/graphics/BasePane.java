package ristogo.ui.graphics;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.BasePanel;
import ristogo.ui.graphics.controls.base.FormButton;

public abstract class BasePane extends BorderPane
{
	protected UserInfo loggedUser;
	protected Consumer<View> changeView;

	public BasePane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super();
		this.changeView = changeView;
		this.loggedUser = loggedUser;
		setContent();
	}

	protected final void setContent()
	{
		Node node = createHeader();
		if (node != null) {
			setAlignment(node, Pos.CENTER);
			setTop(node);
		}
		node = createLeft();
		if (node != null) {
			setLeft(node);
		}
		node = createCenter();
		if (node != null) {
			setCenter(node);
		}
		node = createRight();
		if (node != null) {
			setRight(node);
		}
		node = createFooter();
		if (node != null) {
			HBox footerBox = new HBox(10);
			footerBox.getChildren().add(node);
			footerBox.setAlignment(Pos.CENTER);
			setBottom(footerBox);
		}
	}

	protected abstract BasePanel createLeft();
	protected abstract BasePanel createCenter();
	protected abstract BasePanel createRight();
	protected abstract View getView();

	protected GridPane createHeader()
	{
		Label title = new Label("RistoGo - Recommendations");
		title.setFont(GUIConfig.getTitleFont());
		title.setTextFill(GUIConfig.getFgColor());

		ImageView icon = new ImageView(getClass().getResource("/resources/logo.png").toString());
		icon.setFitHeight(30);
		icon.setFitWidth(30);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(1, 1, 5, 1));
		grid.setMaxWidth(500);
		grid.setAlignment(Pos.CENTER);

		grid.add(title, 0, 0);
		grid.add(icon, 1, 0);

		return grid;
	}

	protected ToolBar createFooter()
	{
		ToolBar toolBar = new ToolBar();
		if (getView() != View.MAIN) {
			FormButton mainButton = new FormButton("Home");
			mainButton.setOnAction((event) -> {
				changeView.accept(View.MAIN);
			});
			toolBar.getItems().add(mainButton);
		}
		if (getView() != View.RESTAURANTS) {
			FormButton restaurantButton = new FormButton("My Restaurants");
			restaurantButton.setOnAction((event) -> {
				changeView.accept(View.RESTAURANTS);
			});
			toolBar.getItems().add(restaurantButton);
		}
		if (getView() != View.PREFERENCES) {
			FormButton prefButton = new FormButton("My Preferences");
			prefButton.setOnAction((event) -> {
				changeView.accept(View.PREFERENCES);
			});
			toolBar.getItems().add(prefButton);
		}
		if (!loggedUser.isAdmin() || getView() == View.ADMIN)
			return toolBar;
		FormButton adminButton = new FormButton("Admin Panel");
		adminButton.setOnAction((event) -> {
			changeView.accept(View.ADMIN);
		});
		toolBar.getItems().add(adminButton);
		return toolBar;
	}
}