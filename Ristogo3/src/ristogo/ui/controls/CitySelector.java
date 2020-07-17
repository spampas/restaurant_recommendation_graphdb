package ristogo.ui.controls;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CityInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.controls.base.AutocompleteTextField;

public class CitySelector extends AutocompleteTextField
{
	public CitySelector()
	{
		super(CitySelector::loadCities);
	}

	private static List<String> loadCities(String filter)
	{
		List<String> result = new ArrayList<String>();
		ResponseMessage resMsg = Protocol.getInstance().listCities(new StringFilter(filter), new PageFilter(0, 10));
		if(resMsg.isSuccess())
			resMsg.getEntities(CityInfo.class).forEach((CityInfo c) -> { result.add(c.getName()); });
		return result;
	}
}
