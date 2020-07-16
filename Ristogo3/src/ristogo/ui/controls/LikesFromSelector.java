package ristogo.ui.controls;

import javafx.scene.control.ChoiceBox;
import ristogo.common.net.entities.enums.LikesFrom;

public class LikesFromSelector extends ChoiceBox<LikesFrom>
{
	public LikesFromSelector()
	{
		super();
		getItems().addAll(LikesFrom.values());
	}
}
