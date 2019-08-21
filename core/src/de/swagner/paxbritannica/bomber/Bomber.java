package de.swagner.paxbritannica.bomber;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.Ship;

public class Bomber extends Ship {

	/**
	 * counter keeps track of how many have been produced for each player.
	 */
	private static Map<Integer, Integer> shipCounters = new HashMap<>();
	private final float DESIGN_BONUS = .01f;
	private final float DEFAULT_TURN_SPEED = 45f;
	private final float DEFAULT_ACCEL = 45f;
	private final float DEFAULT_HIT_POINTS = 440f;

	public BomberAI ai = new BomberAI(this);

	public Bomber(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);

		float turnSpeed = DEFAULT_TURN_SPEED;
		float accel = DEFAULT_ACCEL;
		float hitPoints = DEFAULT_HIT_POINTS;

		switch (id) {
            case 0:
			this.set(Resources.getInstance().bomberP1);
			break;
            case 1:
			// Player 2 has bonus on bombers
			turnSpeed *= 1.2f;
			accel *= 1.2f;
			hitPoints *= 1.2f;
			this.set(Resources.getInstance().bomberP2);
			break;
            case 2:
			this.set(Resources.getInstance().bomberP3);
			break;
            case 3:
			this.set(Resources.getInstance().bomberP4);
			break;
		}
		this.setOrigin(this.getWidth()/2, this.getHeight() / 2);

		if (shipCounters.containsKey(id)) {
			int num = shipCounters.get(id);
			shipCounters.put(id, num + 1);
			turnSpeed = turnSpeed * (1 + DESIGN_BONUS * num);
			accel = accel * (1 + DESIGN_BONUS * num);
			hitPoints = hitPoints * (1 + DESIGN_BONUS * num);
		} else
			shipCounters.put(id, 1);

		super.turnSpeed = turnSpeed;
		super.accel = accel;
		super.hitPoints = hitPoints;
	}

	@Override
	public void draw(Batch batch) {
		ai.update();
		
		super.draw(batch);
	}

	public void shoot(int approach) {
		 Vector2 bombFacing = new Vector2().set(facing).rotate(90*approach);
		 GameInstance.getInstance().bullets.add(new Bomb(id, 0, collisionCenter, bombFacing));
		
	}

}
