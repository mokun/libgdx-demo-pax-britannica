package de.swagner.paxbritannica.factory;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class PlayerProduction extends FactoryProduction {
		
	public PlayerProduction(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);
	}
	
	@Override
	public void draw(Batch batch) {
		thrust();
		turn(1);
		super.draw(batch);
	}

	
}
