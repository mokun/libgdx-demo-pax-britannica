package de.swagner.paxbritannica.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Ship;
import de.swagner.paxbritannica.bomber.Bomber;
import de.swagner.paxbritannica.fighter.Fighter;
import de.swagner.paxbritannica.frigate.Frigate;

public class HardEnemyProduction extends FactoryProduction {

	int action_index = 0;
	float timeToHold = 0;
	float accumulated_frames = 0;
	float frames_to_wait = 0;
	int script_index = 0;
	float delta;

	int action = -1;

	int enemyFighters = 0;
	int enemyBombers = 0;
	int enemyFrigates = 0;
	int ownFighters = 0;
	int ownBombers = 0;
	int ownFrigates = 0;

	int playerID;
	
	public HardEnemyProduction(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);
		playerID = id;
	}

	@Override
	public void draw(Batch batch) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		super.draw(batch);

		accumulated_frames += 30 * delta;

		if (production.currentBuildingUnit != action && action >-1) {
			button_held = true;
		} else {
			button_held = false;
			next_action();
		}

		thrust();
		turn(1);
	}

	public void next_action() {
		action = -1;		
		enemyFighters = 0;
		enemyBombers = 0;
		enemyFrigates = 0;
		ownFighters = 0;
		ownBombers = 0;
		ownFrigates = 0;				
		accumulated_frames = 0;
		timeToHold = 0;


		for (Ship fighter : GameInstance.getInstance().fighters) {
			if(fighter.id != this.id) {
				if(((Fighter) fighter).ai.target != null && ((Fighter) fighter).ai.target.id == this.id) {
					enemyFighters++;
				}
			}
			else ownFighters++;
		}
		
		for (Ship bomber : GameInstance.getInstance().bombers) {
			if(bomber.id != this.id) {
				if(((Bomber) bomber).ai.target != null && ((Bomber) bomber).ai.target.id == this.id) {
					enemyBombers++;
				}
			}
			else ownBombers++;
		}
		
		for (Ship frigate : GameInstance.getInstance().frigates) {
			if(frigate.id != this.id) {
				if(((Frigate) frigate).ai.target != null && ((Frigate) frigate).ai.target.id == this.id) {
					enemyFrigates++;
				}
			}
			else ownFrigates++;
		}
		
		// what to do
		if (health() < .4) action = 3;
		else if (super.resourceAmount < 80) action = -1;
		else if((ownFighters < 4 || enemyBombers > 2) && enemyFighters < 11 && enemyFrigates < 4 && ownFighters < 20) action = 0;
		else if((enemyFrigates > 1 && ownBombers < 1) && enemyFighters < 11) action = 1;
		else if(enemyFighters < 5 && ownBombers < 5) action = 1;
		else if(enemyFighters >= 5 && ownFrigates < 3) action = 2;
		else if(ownFrigates < 1) action = 2;
		else if(ownFighters < 10) action = 0;
		else if(ownBombers > 4 && ownFrigates >= 1 && ownFighters >= 10) action = 3;

		else {
			int rand = MathUtils.random(0, 2);

			if (rand == 2)
				action = MathUtils.random(-1, 3);
			else {
				// THe baseline is playing safe
				// It will do what it does best without considering alternative strategies
				if (playerID == 1)
					action = 0;
				else if (playerID == 2)
					action = 1;
				else if (playerID == 3)
					action = 2;
				else if (playerID == 4)
					action = 3;
			}
		}
	}

}
