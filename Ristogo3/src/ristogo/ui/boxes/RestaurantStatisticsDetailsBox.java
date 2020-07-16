package ristogo.ui.boxes;

import javafx.scene.layout.GridPane;
import ristogo.common.net.entities.StatisticInfo;
import ristogo.ui.boxes.base.DetailsBox;
import ristogo.ui.controls.base.FormLabel;
import ristogo.ui.controls.base.NotEditableTextField;

public class RestaurantStatisticsDetailsBox extends DetailsBox
{
	private final NotEditableTextField likeField = new NotEditableTextField();
	private final NotEditableTextField cityField = new NotEditableTextField();
	private final NotEditableTextField cuisineField = new NotEditableTextField();
	private final NotEditableTextField rankField = new NotEditableTextField();

	public RestaurantStatisticsDetailsBox()
	{
		super("Stats of selected restaurant:");

		FormLabel likeLabel = new FormLabel("Likes for your restaurant:");
		FormLabel cityLabel = new FormLabel("Rank of the restaurants in your city:");
		FormLabel cuisineLabel = new FormLabel("Rank of restaurants with your cuisine:");
		FormLabel rankLabel = new FormLabel("Rank of restaurants in your city and with your cuisine:");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setMaxWidth(500);

		grid.add(likeLabel, 0, 0);
		grid.add(cityLabel, 0, 1);
		grid.add(cuisineLabel, 0, 2);
		grid.add(rankLabel, 0, 3);

		grid.add(likeField, 1, 0);
		grid.add(cityField, 1, 1);
		grid.add(cuisineField, 1, 2);
		grid.add(rankField, 1, 3);

		addContent(grid);
	}

	public void setStats(StatisticInfo stats)
	{
		if (stats == null) {
			clear();
			return;
		}
		likeField.setText(Integer.toString(stats.getLikes()));
		cityField.setText(Integer.toString(stats.getCityRank()));
		cuisineField.setText(Integer.toString(stats.getCuisineRank()));
		rankField.setText(Integer.toString(stats.getCuisineCityRank()));
	}

	public void clear()
	{
		likeField.clear();
		cityField.clear();
		cuisineField.clear();
		rankField.clear();
	}
}
