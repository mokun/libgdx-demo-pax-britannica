package de.swagner.paxbritannica;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//import android.util.SparseArray;
import java.util.HashMap;
import java.util.Map;

import de.swagner.paxbritannica.bomber.Bomb;
import de.swagner.paxbritannica.bomber.Bomber;
import de.swagner.paxbritannica.factory.FactoryProduction;
import de.swagner.paxbritannica.fighter.Fighter;
import de.swagner.paxbritannica.fighter.Laser;
import de.swagner.paxbritannica.frigate.Frigate;
import de.swagner.paxbritannica.frigate.Missile;
import de.swagner.paxbritannica.particlesystem.BigBubbleParticleEmitter;
import de.swagner.paxbritannica.particlesystem.ExplosionParticleEmitter;
import de.swagner.paxbritannica.particlesystem.BubbleParticleEmitter;
import de.swagner.paxbritannica.particlesystem.SparkParticleEmitter;

public class GameInstance {
	
	public boolean debugMode = false;

	public Array<Ship> fighters = new Array<Ship>();
	public Array<Ship> factorys = new Array<Ship>();
	public Array<Ship> bombers = new Array<Ship>();
	public Array<Ship> frigates = new Array<Ship>();


	public Map<Integer, Ship> factoryMap = new HashMap<Integer, Ship>();

	public Array<Bullet> bullets = new Array<Bullet>();

	//public SparseArray<Array<Integer>> killMap = new SparseArray<Array<Integer>>();
	public Map<Integer, Map<Integer, Integer>> killMap = new HashMap<Integer, Map<Integer, Integer>>();
	//public Map<Integer, Map<Integer, Integer>> cpuKills = new HashMap<Integer, Map<Integer, Integer>>();

	public Array<Integer> playerList;
	public Array<Integer> cpuList;

	public BubbleParticleEmitter bubbleParticles = new BubbleParticleEmitter();
	public BigBubbleParticleEmitter bigBubbleParticles = new BigBubbleParticleEmitter();

	public SparkParticleEmitter sparkParticles = new SparkParticleEmitter();
	public ExplosionParticleEmitter explosionParticles = new ExplosionParticleEmitter();
	public int difficultyConfig = 0;
	public int factoryHealthConfig = 0;
	public int antiAliasConfig = 0;	

	public int[][] counts = new int[][]{{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};

	public static GameInstance instance;

	public static GameInstance getInstance() {
		if (instance == null) {
			instance = new GameInstance();
		}
		return instance;
	}
	
	public void resetGame() {

		fighters.clear();
		factorys.clear();

		bombers.clear();
		frigates.clear();
		bullets.clear();

		factoryMap.clear();

		bubbleParticles.dispose();
		bigBubbleParticles.dispose();
		sparkParticles.dispose();
		explosionParticles.dispose();

		bubbleParticles = new BubbleParticleEmitter();
		bigBubbleParticles = new BigBubbleParticleEmitter();

		sparkParticles = new SparkParticleEmitter();
		explosionParticles = new ExplosionParticleEmitter();
		
		Preferences prefs = Gdx.app.getPreferences("paxbritannica");
		GameInstance.getInstance().difficultyConfig  = prefs.getInteger("difficulty",0);
		GameInstance.getInstance().factoryHealthConfig  = prefs.getInteger("factoryHealth",0);
		GameInstance.getInstance().antiAliasConfig  = prefs.getInteger("antiAliasConfig",1);
	}

	public void bulletHit(Ship ship, Bullet bullet) {
		bullet.facing.nor();
		float offset=0;
		if(ship instanceof FactoryProduction) offset = 50;
		if(ship instanceof Frigate) offset = 20;
		if(ship instanceof Bomber) offset = 10;
		if(ship instanceof Fighter) offset = 10;
		Vector2 pos = new Vector2().set(bullet.collisionCenter.x + (offset * bullet.facing.x), bullet.collisionCenter.y + (offset * bullet.facing.y));

		// ugh. . .
		Vector2 bullet_vel = new Vector2().set(bullet.velocity);

		Vector2 bullet_dir;
		if (bullet_vel.dot(bullet_vel) == 0) {
			bullet_dir = new Vector2();
		} else {
			bullet_dir = bullet_vel.nor();
		}
		Vector2 vel = new Vector2(bullet_dir.x * 1.5f, bullet_dir.y * 1.5f);

		if (bullet instanceof Laser) {
			laser_hit(pos, vel);
		} else if (bullet instanceof Bomb) {
			explosionParticles.addMediumExplosion(bullet.position);
		} else if (bullet instanceof Missile) {
			explosionParticles.addTinyExplosion(bullet.position);
		}

		// Record the kill
		if (ship.hitPoints <= 0) {
			//if (bullet.playerOwned()) {
			// Find the player id
			int playerID = bullet.getID();
			// Find the ship id
			int shipType = ship.getShipType();
			if ((shipType == 4 && ship.deathCounter == 50f)
					|| shipType != 4) {

				Map<Integer, Integer> kills = null;
				if (killMap.containsKey(playerID)) {
					kills = killMap.get(playerID);
					int count = kills.get(shipType);
					// Record the kill
					kills.put(shipType, count+1);
					killMap.put(playerID, kills);

				}
				else {
					kills = new HashMap<Integer, Integer>();
					for (int i=1; i < 5; i++) {
						kills.put(i, 0);
					}
						// Record the kill
					kills.put(shipType, 1);
					killMap.put(playerID, kills);
				}

				//int[] counts = new int[4];
				int sum = 0;

				for (int i=1; i < 4; i++) {
					counts[playerID-1][i-1] = kills.get(i);
					if (i-1 == 0)
						sum += counts[playerID-1][i-1];
					else if (i-1 == 1)
						sum += counts[playerID-1][i-1] * 5;
					else if (i-1 == 2)
						sum += counts[playerID-1][i-1] * 15;
					//else if (i-1 == 3)
					//	sum += counts[i-1] * 50;
				}

				counts[playerID-1][3] = sum;

				Gdx.app.log("[GI] ", obtainShipColor(playerID)
						+ "    ["
						+ counts[playerID-1][0]
						+ " " + counts[playerID-1][1]
						+ " " + counts[playerID-1][2]
						//+ " " + counts[3]
						+ "]"
						+ "    Scores : " + sum);

			}
		}
	}

	public int[][] getCounts() {
		return counts;
	}

	// Gets ship color in order to print log
	public String obtainShipColor(int id) {
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

	public void laser_hit(Vector2 pos, Vector2 vel) {
		sparkParticles.addLaserExplosion(pos, vel);
	}

	public void explode(Ship ship) {
		explode(ship, ship.collisionCenter);
	}

	public void explode(Ship ship, Vector2 pos) {

		if (ship instanceof FactoryProduction) {
			explosionParticles.addBigExplosion(pos);
		} else if (ship instanceof Bomber) {
			explosionParticles.addMediumExplosion(pos);
		} else if (ship instanceof Frigate) {
			explosionParticles.addMediumExplosion(pos);
		} else {
			explosionParticles.addSmallExplosion(pos);
		}
	}

	public void setPlayerList(Array<Integer> list) {
		playerList = list;
	}

	//public void setCpuList(Array<Integer> list) {
	//	cpuList = list;
	//}

	public Array<Integer> getPlayerList() {
		return playerList;
	}

	public Array<Integer> getCpuList() {
		return cpuList;
	}

	// Check if only 2 factory ships are left for the final battle
	public boolean isFinalEpicBattle() {
		//if (playerList.size + cpuList.size == 2)
		if (factorys.size == 2)
			return true;
		else
			return false;

	}

	public void removeFactory(int id) {
		factoryMap.remove(id);
	}
}