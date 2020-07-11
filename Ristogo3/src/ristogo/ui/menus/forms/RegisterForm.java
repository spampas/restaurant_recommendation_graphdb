package ristogo.ui.menus.forms;

import java.util.LinkedHashSet;

import ristogo.common.entities.enums.UserType;

public class RegisterForm extends TextForm
{
	public RegisterForm()
	{
		super("Create an account");
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("USERNAME", this::validateUsername));
		fields.add(new FormField("PASSWORD", true, this::validatePassword));
		fields.add(new ChoiceFormField<UserType>("TYPE", UserType.CUSTOMER, UserType.class));
		return fields;
	}

	@Override
	protected boolean validatePassword(String password)
	{
		if (!super.validatePassword(password))
			return false;
		FormField confirm = new FormField("CONFIRM PASSWORD", true);
		confirm.show();
		if (!confirm.getValue().equals(password)) {
			return false;
		}
		return true;
	}
}
