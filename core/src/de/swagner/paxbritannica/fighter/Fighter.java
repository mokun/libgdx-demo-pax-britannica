package de.swagner.paxbritannica.fighter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.Ship;

public class Fighter extends Ship {

	private float shotCooldownTime = 6f;
	private float shotCapacity = 5f;
	private float shotReloadRate = 1f;

	private float shots = shotCapacity;
	private float cooldown = 0;
    /**
     * counter keeps track of how many have been produced for each player.
     */
    private static Map<Integer, Integer> shipCounters = new HashMap<>();
    private final float DESIGN_BONUS = .005f;

    private final float DEFAULT_TURN_SPEED = 120f;
    private final float DEFAULT_ACCEL = 120f;
    private final float DEFAULT_HIT_POINTS = 40f;
    private float delta;

	public FighterAI ai = new FighterAI(this);

	public Fighter(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);

        float turnSpeed = DEFAULT_TURN_SPEED;
        float accel = DEFAULT_ACCEL;
        float hitPoints = DEFAULT_HIT_POINTS;

		switch (id) {
		case 1:
			// Player 1 has bonus on Fighters
			turnSpeed *= 1.2f;
			accel *= 1.2f;
			hitPoints *= 1.2f;
			this.set(Resources.getInstance().fighterP1);
			break;
		case 2:
			this.set(Resources.getInstance().fighterP2);
			break;
		case 3:
			this.set(Resources.getInstance().fighterP3);
			break;
			case 4:
			this.set(Resources.getInstance().fighterP4);
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
		shots = Math.min(shots + (shotReloadRate * Gdx.graphics.getDeltaTime()), shotCapacity);

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
		return isCooledDown() && !isEmpty();
	}

	public void shoot() {
		if (cooldown == 0 && shots >= 1) {
			shots -= 1;
			cooldown = shotCooldownTime;

			GameInstance.getInstance().bullets.add(new Laser(id, 0, collisionCenter, facing));
		}
	}

}
