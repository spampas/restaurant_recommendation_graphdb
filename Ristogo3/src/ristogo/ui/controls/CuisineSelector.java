package ristogo.ui.controls;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.net.ResponseMessage;
import ristogo.common.net.entities.CuisineInfo;
import ristogo.common.net.entities.PageFilter;
import ristogo.common.net.entities.StringFilter;
import ristogo.net.Protocol;
import ristogo.ui.controls.base.AutocompleteTextField;

public class CuisineSelector extends AutocompleteTextField
{
	public CuisineSelector()
	{
		super(CuisineSelector::loadCuisines);
	}

	private static List<String> loadCuisines(String filter)
	{
		List<String> result = new ArrayList<String>();
		ResponseMessage resMsg = Protocol.getInstance().listCuisines(new StringFilter(filter), new PageFilter(0, 10));
		if(resMsg.isSuccess())
			resMsg.getEntities(CuisineInfo.class).forEach((CuisineInfo c) -> { result.add(c.getName()); });
		return result;
	}
}
