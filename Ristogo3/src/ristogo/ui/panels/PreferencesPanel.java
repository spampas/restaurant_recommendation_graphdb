package ristogo.ui.panels;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.ErrorBox;
import ristogo.ui.boxes.CitySelectorControlBox;
import ristogo.ui.boxes.CuisinesDetailsBox;
import ristogo.ui.panels.base.TablePanel;

public class PreferencesPanel extends TablePanel
{
	private String prevCity = "";

	public PreferencesPanel()
	{
		super("Your Preferences");
		//setMinSize(350, 600);
		//setMaxSize(400, 800);
		CitySelectorControlBox controlBox = new CitySelectorControlBox();
		CuisinesDetailsBox detailsBox = new CuisinesDetailsBox();
		ResponseMessage resMsg = Protocol.getInstance().getCity();
		if (!resMsg.isSuccess())
			new ErrorBox("Error", "An error has occured while trying to get your city.", resMsg.getErrorMsg()).showAndWait();
		else
			prevCity = resMsg.getEntity(CityInfo.class).getName();
		controlBox.setCity(prevCity);
		controlBox.setOnClick((city) -> {
			if (city == null || city.isEmpty())
				return;
			ResponseMessage msg = Protocol.getInstance().setCity(new StringFilter(city));
			if (msg.isSuccess())
				return;
			new ErrorBox("Error", "An error has occured while trying to set your city.", msg.getErrorMsg()).showAndWait();
			controlBox.setCity(prevCity);
		});
		setControlBox(controlBox);
		setDetailsBox(detailsBox);
		showContent();
	}
}
