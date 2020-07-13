package ristogo.ui.graphics.controls;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;

public class CitySelector extends AutocompleteTextField
{

	public CitySelector()
	{
		super(CitySelector::loadCities);
	}

	private static List<String> loadCities(String filter)
	{
		List<String> result = new ArrayList<String>();
		ResponseMessage resMsg = Protocol.getInstance().getCities(new StringFilter("(?i).*" + filter + ".*"));
		if(resMsg.isSuccess())
			resMsg.getEntities(CityInfo.class).forEach((CityInfo c) -> { result.add(c.getName()); });
		return result;
	}

}
