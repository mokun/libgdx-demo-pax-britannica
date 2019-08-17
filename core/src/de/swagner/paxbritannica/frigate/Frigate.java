package de.swagner.paxbritannica.frigate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.Ship;

public class Frigate extends Ship {

	/**
	 * counter keeps track of how many have been produced for each player.
	 */
	private static Map<Integer, Integer> shipCounters = new HashMap<>();
	private final float DESIGN_BONUS = .015f;
	private final float DEFAULT_TURN_SPEED = 20f;
	private final float DEFAULT_ACCEL = 14f;
	private final float DEFAULT_HIT_POINTS = 2000f;

	private float shotCooldownTime = 5f;
	private float shotCapacity = 8f;
	private float shotReloadRate = 1f;

	private float shots = 0;
	private float cooldown = 0;
	float delta;

	public FrigateAI ai = new FrigateAI(this);

	public Frigate(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);

		float turnSpeed = DEFAULT_TURN_SPEED;
		float accel = DEFAULT_ACCEL;
		float hitPoints = DEFAULT_HIT_POINTS;

		switch (id) {
		case 1:
			this.set(Resources.getInstance().frigateP1);
			break;
		case 2:
			this.set(Resources.getInstance().frigateP2);
			break;
		case 3:
			// Player 3 has bonus on frigates
			turnSpeed *= 1.2f;
			accel *= 1.2f;
			hitPoints *= 1.2f;
			this.set(Resources.getInstance().frigateP3);
			break;
		case 4:
			this.set(Resources.getInstance().frigateP4);
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
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		ai.update();
		
		cooldown = Math.max(0, cooldown - delta*50f);
		shots = Math.min(shots + (shotReloadRate * delta), shotCapacity);

		super.draw(batch);
	}

	public boolean isEmpty() {
		return shots < 1;
	}

	public boolean isReloaded() {
		return shots == shotCapacity;
	}

	public boolean isCooledDown() {
		return cooldown == 0;
	}

	public boolean isReadyToShoot() {
		return isCooledDown() && isReloaded();
	}

	public void shoot() {
		if (cooldown == 0 && shots >= 1) {
			shots -= 1;
			cooldown = shotCooldownTime;
			GameInstance.getInstance().bullets.add(new Missile(id, 0, collisionCenter, facing));
		}
	}

}
