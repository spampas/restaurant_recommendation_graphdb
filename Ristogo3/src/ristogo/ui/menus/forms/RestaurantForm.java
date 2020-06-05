package ristogo.ui.menus.forms;

import java.util.HashMap;
import java.util.LinkedHashSet;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class RestaurantForm extends TextForm
{
	protected Restaurant restaurant;

	public RestaurantForm(Restaurant restaurant)
	{
		super("Editing " + restaurant.getName());
		this.restaurant = restaurant;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Name", restaurant.getName(), (s) -> { return s != null && s.length() < 46; }));
		if (restaurant.getGenre() != null)
			fields.add(new ChoiceFormField<Genre>("Genre", restaurant.getGenre(), Genre.class));
		else
			fields.add(new ChoiceFormField<Genre>("Genre", Genre.class));
		if (restaurant.getPrice() != null)
			fields.add(new ChoiceFormField<Price>("Price", restaurant.getPrice(), Price.class));
		else
			fields.add(new ChoiceFormField<Price>("Price", Price.class));
		fields.add(new FormField("City", restaurant.getCity(), (s) -> { return s == null || s.length() < 33; }));
		fields.add(new FormField("Address", restaurant.getAddress(), (s) -> { return s == null || s.length() < 33; }));
		fields.add(new FormField("Description", restaurant.getDescription()));
		fields.add(new FormField("Seats", Integer.toString(restaurant.getSeats()), this::validatePositiveInteger));
		fields.add(new ChoiceFormField<OpeningHours>("Opening hours", restaurant.getOpeningHours(), OpeningHours.class));
		return fields;
	}

	@Override
	public HashMap<Integer, String> show()
	{
		HashMap<Integer, String> response = super.show();
		restaurant.setName(response.get(0));
		restaurant.setGenre(Genre.valueOf(response.get(1)));
		restaurant.setPrice(Price.valueOf(response.get(2)));
		restaurant.setCity(response.get(3));
		restaurant.setAddress(response.get(4));
		restaurant.setDescription(response.get(5));
		restaurant.setSeats(Integer.parseInt(response.get(6)));
		restaurant.setOpeningHours(OpeningHours.valueOf(response.get(7)));
		return response;
	}
}
