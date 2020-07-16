package ristogo.ui;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ristogo.common.net.entities.UserInfo;
import ristogo.ui.config.GUIConfig;
import ristogo.ui.enums.View;
import ristogo.ui.panels.PreferencesPanel;
import ristogo.ui.panels.base.BasePanel;

public class PreferencesPane extends BasePane
{

	public PreferencesPane(Consumer<View> changeView, UserInfo loggedUser)
	{
		super(changeView, loggedUser);
	}

	@Override
	protected GridPane createHeader()
	{
		GridPane grid = super.createHeader();

		Label prefLabel = new Label("Preferences");
		prefLabel.setFont(GUIConfig.getWelcomeFont());
		prefLabel.setTextFill(GUIConfig.getFgColor());

		grid.add(prefLabel, 2, 0);

		return grid;
	}

	@Override
	protected BasePanel createLeft()
	{
		return null;
	}

	@Override
	protected PreferencesPanel createCenter()
	{
		return new PreferencesPanel();
	}

	@Override
	protected BasePanel createRight()
	{
		return null;
	}

	@Override
	protected View getView()
	{
		return View.PREFERENCES;
	}

}
