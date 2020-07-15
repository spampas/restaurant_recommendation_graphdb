package ristogo.ui.graphics;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.graphics.config.GUIConfig;
import ristogo.ui.graphics.controls.BasePanel;
import ristogo.ui.graphics.controls.base.FormButton;

public class AdminPane extends BasePane
{

	public AdminPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected GridPane createHeader()
	{
		GridPane grid = super.createHeader();

		Label adminLabel = new Label("Admin Panel");
		adminLabel.setFont(GUIConfig.getWelcomeFont());
		adminLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(adminLabel, 2, 0);

		return grid;
	}

	@Override
	protected BasePanel createLeft()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BasePanel createCenter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BasePanel createRight()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected View getView()
	{
		return View.ADMIN;
	}
}
