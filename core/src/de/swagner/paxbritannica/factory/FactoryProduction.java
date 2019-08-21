package de.swagner.paxbritannica.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.swagner.paxbritannica.Constants;
import de.swagner.paxbritannica.GameInstance;
import de.swagner.paxbritannica.Resources;
import de.swagner.paxbritannica.Ship;

public class FactoryProduction extends Ship {

	private final float DEFAULT_TURN_SPEED = 2f;
	private final float DEFAULT_ACCEL = 5f;
	private final float SHIELD_POWER_USAGE = 30f;

	public float harvestRate = 40f;
	public float harvestRateUpgrade = 15f;
	public float upgradesUsed = 0f;
	public float resourceAmount = 20;
	
	public int ownShips = 0;

	public int id;

	public boolean button_held = false;
	
	private boolean drawDamage = false;

	float delta;

	private String sCache = "";

	private Sprite heavy_damage1 = new Sprite();
	private Sprite heavy_damage2 = new Sprite();
	private Sprite heavy_damage3 = new Sprite();
	private Sprite light_damage1 = new Sprite();
	private Sprite light_damage2 = new Sprite();
	private Sprite light_damage3 = new Sprite();

	private Sprite current_damage = new Sprite();

	public Production production = new Production(this);
	
	private Vector2 facing90 = new Vector2();

	private boolean isShieldedUp = false;

	//private Array<Integer> playerList;

	public FactoryProduction(int id, int team, Vector2 position, Vector2 facing) {
		super(id, team, position, facing);

		this.id = id;

		float turnSpeed = DEFAULT_TURN_SPEED;
		float accel = DEFAULT_ACCEL;
		float hitPoints = 0;

		if(GameInstance.getInstance().factoryHealthConfig == 0) {
			hitPoints = 25000;
		} else if(GameInstance.getInstance().factoryHealthConfig == 1) {
			hitPoints = 45000;
		} else {
			hitPoints = 65000;
		}
		maxHitPoints = hitPoints;

		velocity.set(facing.x, facing.y);

		switch (id) {
			case 0:
			this.set(Resources.getInstance().factoryP1);
			break;
			case 1:
			this.set(Resources.getInstance().factoryP2);
			break;
			case 2:
			this.set(Resources.getInstance().factoryP3);
			break;
			case 3:
				// Player 4 has bonus on factory ship
				hitPoints *= 1.2f;
			this.set(Resources.getInstance().factoryP4);
			break;
		}

		super.turnSpeed = turnSpeed;
		super.accel = accel;
		super.hitPoints = hitPoints;


		String tag = "Player ";
		for (int i : GameInstance.getInstance().getCpuList()) {
			if (i == id) {
				tag = "CPU ";
				break;
			}
		}

		// Prints player/cpu id and team id
		Gdx.app.log("FP", obtainShipColor(id) + " (" + tag + " " + (id + 1) + ", Team " + team + ")");

		light_damage1.set(Resources.getInstance().factoryLightDamage1);
		light_damage2.set(Resources.getInstance().factoryLightDamage2);
		light_damage3.set(Resources.getInstance().factoryLightDamage3);
		heavy_damage1.set(Resources.getInstance().factoryHeavyDamage1);
		heavy_damage2.set(Resources.getInstance().factoryHeavyDamage2);
		heavy_damage3.set(Resources.getInstance().factoryHeavyDamage3);
		current_damage = light_damage1;

		this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
		
	}

	// Gets ship color in order to print log
	private String obtainShipColor(int id) {
		if (id == 0)
			return "Blue";
		else if (id == 1)
			return "Red";
		else if (id == 2)
			return "Green";
		else if (id == 3)
			return "Yellow";
		return "";
	}

	@Override
	public void draw(Batch batch) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
		
		resourceAmount = Math.min(2000, resourceAmount + (harvestRate * delta));
		super.draw(batch);
		production.draw(batch);

		// Damage
		// ugh. . . sprite needs to be more flexible
		drawDamage = false;
		float health = health();

//		double h = health;
//
//		int size = playerList.size;
//		String s = "";
//		for (int i = 0 ; i < size; i++) {
//			if (playerList.get(i) == id) {
//				String name = obtainShipColor(id);
//				s = name + " Ship (Team " + team + ") : " + Math.round(h*1000.0)/10.0 + " %";
//				if (!sCache.equals(s)) {
//					Gdx.app.log("", s);
//					sCache = s;
//				}
//			}
//		}

		int animation = (int) (Math.floor(aliveTime * 20) % 3 + 1);

		if (health < Constants.lowHealthThreshold) {
			switch (animation) {
			case 1:
				current_damage = heavy_damage1;
				break;
			case 2:
				current_damage = heavy_damage2;
				break;
			default:
				current_damage = heavy_damage3;
				break;
			}
			drawDamage = true;
		} else if (health < Constants.highHealthThreshold) {
			switch (animation) {
			case 1:
				current_damage = light_damage1;
				break;
			case 2:
				current_damage = light_damage2;
				break;
			default:
				current_damage = light_damage3;
				break;
			}
			drawDamage = true;
		}
		
		if(drawDamage) {
			facing90.set(facing);
			facing90.rotate(90).nor();

			current_damage.setOrigin(0, 0);
			current_damage.setPosition(collisionCenter.x - (90 * facing.x) - (60 * facing90.x), collisionCenter.y - (90 * facing.y)
					- (60 * facing90.y));
			current_damage.setRotation(facing.angle());
			current_damage.setColor(1, 1, 1, MathUtils.random());
			current_damage.draw(batch);
		}
	}

	public void setShieldUp(boolean value) {
		float newResourceAmt = resourceAmount - SHIELD_POWER_USAGE;
		if (value && newResourceAmt >= 0) {
			resourceAmount = newResourceAmt;
			isShieldedUp = true;
//			Gdx.app.log("FP", "Shield's up : " + ((FactoryProduction) this).resourceAmount);
		} else
			isShieldedUp = false;
	}

	public boolean isShieldedUp() {
		return isShieldedUp;
	}
}
