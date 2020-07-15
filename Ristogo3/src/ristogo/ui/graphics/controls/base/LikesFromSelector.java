package ristogo.ui.graphics.controls.base;

import javafx.scene.control.ChoiceBox;
import ristogo.common.net.entities.enums.LikesFrom;
import ristogo.common.net.entities.enums.Price;

public class LikesFromSelector extends ChoiceBox<LikesFrom>
{
	public LikesFromSelector()
	{
		super();
		getItems().addAll(LikesFrom.values());
	}
}
