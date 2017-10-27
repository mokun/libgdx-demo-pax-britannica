package de.swagner.paxbritannica;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import de.swagner.paxbritannica.bomber.Bomber;
import de.swagner.paxbritannica.factory.FactoryProduction;
import de.swagner.paxbritannica.fighter.Fighter;
import de.swagner.paxbritannica.frigate.Frigate;

public class Ship extends Sprite {

	protected float amount = 1.0f;

	protected float turnSpeed = 1.0f;
	protected float accel = 0.0f;
	protected float hitPoints = 0;

	protected float maxHitPoints = 0;

	private float delta = 0.0f;

	public float aliveTime = 0.0f;

	public Vector2 position = new Vector2();
	public Vector2 velocity = new Vector2();
	public Vector2 facing = new Vector2();
	
	public Vector2 collisionCenter = new Vector2();
	public Array<Vector2> collisionPoints = new Array<Vector2>();

	public boolean alive = true;

	public float deathCounter = 50f;
	private float nextExplosion = 10f;
	private float opacity = 5.0f;

	private String sCache = "";

	public int id = 0;
	public int team = 0;

	private Array<Integer> playerList;


	public Ship(int id, int team, Vector2 position, Vector2 facing) {
		super();

		playerList = GameInstance.getInstance().getPlayerList();

		//Gdx.app.setLogLevel(Application.LOG_DEBUG);

		this.id = id;
		this.team = team;
		this.position.set(position);
		this.facing.set(facing);
		
		collisionPoints.clear();
		collisionPoints.add(new Vector2());
		collisionPoints.add(new Vector2());
		collisionPoints.add(new Vector2());
		collisionPoints.add(new Vector2());

		this.setOrigin(this.getWidth() / 2.f, this.getHeight() / 2.f);
	}

	@Override
	public void draw(Batch batch) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		aliveTime += delta;
		collisionPoints.get(0).set( this.getVertices()[0], this.getVertices()[1]);
		collisionPoints.get(1).set( this.getVertices()[5], this.getVertices()[6]);
		collisionPoints.get(2).set( this.getVertices()[10], this.getVertices()[11]);
		collisionPoints.get(3).set( this.getVertices()[15], this.getVertices()[16]);
		
		collisionCenter.set(collisionPoints.get(0)).add(collisionPoints.get(2)).scl(0.5f);

		velocity.scl( (float) Math.pow(0.97f, delta * 30.f));
		position.add(velocity.x * delta, velocity.y * delta);
		
		this.setRotation(facing.angle());
		this.setPosition(position.x, position.y);

		if (!(this instanceof Bullet) && hitPoints <= 0)
			destruct();
		if (MathUtils.random() < velocity.len() / 900.f) {
			GameInstance.getInstance().bubbleParticles.addParticle(randomPointOnShip());
		}
		super.draw(batch);
	}

	public void turn(float direction) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		facing.rotate(direction * turnSpeed * delta).nor();
	}

	public void thrust() {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		velocity.add(facing.x * accel * delta, facing.y * accel * delta);
	}
	
	public void thrust(float amount) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		velocity.add(facing.x * accel * delta, facing.y * accel * amount * delta);
	}

	public Vector2 randomPointOnShip() {
		return new Vector2(collisionCenter.x + MathUtils.random(-this.getWidth() / 2, this.getWidth() / 2), collisionCenter.y
				+ MathUtils.random(-this.getHeight() / 2, this.getHeight() / 2));
	}

	/*
	 * Scratch space for computing a target's direction. This is safe (as a static) because
	 * its only used in goTowardsOrAway which is only called on the render thread (via update).
	 */
	private static final Vector2 target_direction = new Vector2(); 

	public void goTowardsOrAway(Vector2 targetPos, boolean forceThrust, boolean isAway) {
		target_direction.set(targetPos).sub(collisionCenter);
		if (isAway) {
			target_direction.scl(-1);
		}

		if (facing.crs(target_direction) > 0) {
			turn(1);
		} else {
			turn(-1);
		}

		if (forceThrust || facing.dot(target_direction) > 0) {
			thrust();
		}
	}

	public float healthPercentage() {
		return Math.max(hitPoints / maxHitPoints, 0);
	}

	public void damage(float amount)
	{
		hitPoints = Math.max(hitPoints - amount, 0);
/*
		if (this instanceof FactoryProduction) {
			int size = playerList.size;
			String s = "";
			for (int i = 0; i < size; i++) {
				//if (playerList.get(i) == id) {
					String name = obtainShipColor(id);
					double h = healthPercentage();
					//s = name + " (Team " + team + ") : " + Math.round(h * 100.0) + " %";
					s = name + "  :  " + Math.round(h * 100.0) + " %";
					if (!sCache.equals(s)) {
						Gdx.app.log("[SP] ", s);
						sCache = s;
					}
				//}
			}
		}
*/
	}
/*
	// Gets ship color in order to print log
	private String obtainShipColor(int id) {
		if (id == 1)
			return "Blue";
		else if (id == 2)
			return "Red";
		else if (id == 3)
			return "Green";
		else if (id == 4)
			return "Yellow";
		return "";
	}
*/
	public void destruct() {
		if (this instanceof FactoryProduction) {
			factoryDestruct();
		} else {
			GameInstance.getInstance().explode(this);
			alive = false;
			for (Ship factory : GameInstance.getInstance().factorys) {
				if(factory instanceof FactoryProduction && factory.id == this.id) {
					((FactoryProduction) factory).ownShips--;
					//GameInstance.getInstance().removeFactory(id);
				}
			}
		}

		// if there are only two factory ships left, the final epic battle begins and
		// friendly fires are allowed !
		if (GameInstance.getInstance().isFinalEpicBattle())
			Targeting.setFriendlyFire(true);
	}

	public void factoryDestruct() {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		if (deathCounter > 0) {
			((FactoryProduction) this).production.halt_production = true;
			this.setColor(1, 1, 1, Math.min(1, opacity));
			opacity -= 1 * delta;
			if (Math.floor(deathCounter) % nextExplosion == 0) {
				GameInstance.getInstance().explode(this, randomPointOnShip());
				nextExplosion = MathUtils.random(2, 6);
			}
			deathCounter -= 10 * delta;
		} else {
			for (int i = 1; i <= 10; ++i) {
				GameInstance.getInstance().explode(this, randomPointOnShip());
			}
			alive = false;
//
		}
	}

	// automatically thrusts and turns according to the target
	public void goTowards(Vector2 targetPos, boolean forceThrust) {
		goTowardsOrAway(targetPos, forceThrust, false);
	}

	public void goAway(Vector2 targetPos, boolean forceThrust) {
		goTowardsOrAway(targetPos, forceThrust, true);
	}

	public int getTeamID() {
		return team;
	}

	public int getID() {
		return id;
	}

	public boolean playerOwned() {
		int size = playerList.size;
		for (int i = 0; i < size; i++) {
			if (playerList.get(i) == id) {
				return true;
			}
		}
		return false;
	}

	public float getHitPoints() {
		return hitPoints;
	}

	public void addHitPoints() {
		float oldHealth = healthPercentage();
		float oldHit = hitPoints;
		hitPoints = hitPoints + (int) (maxHitPoints * .1);
/*
		Gdx.app.log("[SH] Hit Points (", oldHit + "   ->   " + hitPoints + ")      Health : (" + Math.round(oldHealth*1000.0)/10.0
				+ " %   ->   " + Math.round(healthPercentage()*1000.0)/10.0 + " %)");
*/

	}

	public int getShipType() {
		if (this instanceof Fighter)
			return 1;
		else if (this instanceof Bomber)
			return 2;
		else if (this instanceof Frigate)
			return 3;
		else if (this instanceof FactoryProduction)
			return 4;
		else
			return 0;
	}
}
