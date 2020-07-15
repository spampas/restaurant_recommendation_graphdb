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
import ristogo.ui.graphics.controls.PreferencesPanel;
import ristogo.ui.graphics.controls.base.FormButton;

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
