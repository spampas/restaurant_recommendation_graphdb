package ristogo.ui.menus.forms;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashSet;

import ristogo.common.entities.User;

public abstract class TextForm
{
	protected String prompt;
	private LinkedHashSet<FormField> fields;

	protected abstract LinkedHashSet<FormField> createFields();

	protected TextForm()
	{
		this("Fill the following form");
	}

	protected TextForm(String prompt)
	{
		this.prompt = prompt;
	}

	public HashMap<Integer, String> show()
	{
		if (!prompt.isBlank())
		fields = createFields();
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		int i = 0;
		for (FormField field: fields) {
			field.show();
			hm.put(i, field.getValue());
			i++;
		}
		return hm;
	}

	protected boolean validateUsername(String username)
	{
		if (!User.validateUsername(username)) {
			return false;
		}
		return true;
	}

	protected boolean validatePassword(String password)
	{
		if (!User.validatePassword(password)) {
			return false;
		}
		return true;
	}

	protected boolean validatePositiveInteger(String value)
	{
		try {
			int converted = Integer.parseInt(value);
			if (converted <= 0)
				return false;
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	protected boolean validateFutureDate(String date)
	{
		try {
			LocalDate converted = LocalDate.parse(date);
			if (converted.compareTo(LocalDate.now()) < 0) {
				return false;
			}
		} catch (DateTimeParseException ex) {
			return false;
		}
		return true;
	}
}
