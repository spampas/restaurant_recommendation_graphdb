package ristogo.ui;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.enums.View;
import ristogo.ui.panels.CitiesPanel;
import ristogo.ui.panels.CuisinesPanel;
import ristogo.ui.panels.base.BasePanel;

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
	protected CitiesPanel createLeft()
	{
		return new CitiesPanel();
	}

	@Override
	protected BasePanel createCenter()
	{
		return null;
	}

	@Override
	protected CuisinesPanel createRight()
	{
		return new CuisinesPanel();
	}

	@Override
	protected View getView()
	{
		return View.ADMIN;
	}
}
