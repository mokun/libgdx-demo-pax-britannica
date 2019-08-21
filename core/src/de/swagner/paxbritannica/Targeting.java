package de.swagner.paxbritannica;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.swagner.paxbritannica.bomber.Bomber;
import de.swagner.paxbritannica.factory.FactoryProduction;
import de.swagner.paxbritannica.fighter.Fighter;
import de.swagner.paxbritannica.frigate.Frigate;

public class Targeting {

	// is friendly fire allowed ?
	public static boolean friendlyFire = true;

	// Set if friendly fire is allowed or not
	public static void setFriendlyFire(boolean value) {
		friendlyFire = value;
	}

	public static boolean onScreen(Vector2 position) {
		return onScreen(position, 0);
	}

	public static boolean onScreen(Vector2 position, float buffer) {
		return position.x >= Constants.screenLeft - buffer && position.x <= Constants.screenRight + buffer && position.y >= Constants.screenBottom - buffer
				&& position.y <= Constants.screenTop + buffer;
	}

	/*
	 * returns the closest target of the given type 0 = Fighter 1 = Bomber 2 =
	 * Frigate 3 = Factory
	 */
	public static Ship getNearestOfType(Ship source, Ship.ShipType shipType, float range) {
		if (shipType == Ship.ShipType.FIGHTER)
			return getNearestOfType(source, GameInstance.getInstance().fighters, range);
		else if (shipType == Ship.ShipType.BOMBER)
			return getNearestOfType(source, GameInstance.getInstance().bombers, range);
		else if (shipType == Ship.ShipType.FRIGATE)
			return getNearestOfType(source, GameInstance.getInstance().frigates, range);
		else if (shipType == Ship.ShipType.FACTORY)
			return getFactoryWithHighestHealth(source, GameInstance.getInstance().factorys, range);
		else
			return null;
	}

	private static Ship getFactoryWithHighestHealth(Ship source, Array<Ship> ships, float range) {
		// find the closest one!
		Ship closestShip = null;
		float highestHealth = Float.MIN_VALUE;

		// Find the designated target_id
		int player_id = source.getID();
		int target_id = GameInstance.getInstance().targetingMap.get(player_id);
		boolean target_all_colors = false;
		if (target_id == GameInstance.TARGET_ANY)
			target_all_colors = true;

		int size = ships.size;
		for (int i = 0; i < size; i++) {
			Ship ship = ships.get(i);
			float currentHealth = ship.hitPoints+(((FactoryProduction)ship).harvestRate*500);
			float currentDistance = source.collisionCenter.dst(ship.collisionCenter);

			// Exclude friendly ship
			if (friendlyFire || ship.getTeamID() != source.getTeamID() || source.getTeamID() == 0 || ship.getTeamID() == 0) {
				// Target only ships with a particular target_id
				if (ship.alive &&
						(target_all_colors || ship.getID() == target_id) && source.id != ship.id && onScreen(ship.collisionCenter)
						&& (currentHealth > highestHealth)
						&& (currentDistance < range)) {
					closestShip = ship;
					highestHealth = currentHealth;
				}
			}
		}

		return closestShip;
	}

	private static Ship getNearestOfType(Ship source, Array<Ship> ships, float range) {
		// find the closest one!
		Ship closestShip = null;
		float closestDistanze = Float.MAX_VALUE;

		// Find the designated target_id
		int player_id = source.getID();
		int target_id = GameInstance.getInstance().targetingMap.get(player_id);
		boolean target_all_colors = false;
		if (target_id == GameInstance.TARGET_ANY)
			target_all_colors = true;

		int size = ships.size;
		for (int i = 0; i < size; i++) {
			Ship ship = ships.get(i);

			// Exclude friendly ship
			if (friendlyFire || ship.getTeamID() != source.getTeamID() || source.getTeamID() == 0 || ship.getTeamID() == 0) {
				float currentDistance = source.collisionCenter.dst(ship.collisionCenter);

				if (currentDistance <= range) {
					// Target only ships with a particular target_id
					if (ship.alive && (target_all_colors || ship.getID() == target_id) && source.id != ship.id && onScreen(ship.collisionCenter) && (currentDistance < closestDistanze)) {
						//skip if ship is not targeting source ship
						if (ship instanceof Fighter) {
							if (((Fighter) ship).ai.target != null && ((Fighter) ship).ai.target.id != source.id) {
								continue;
							}
						} else if (ship instanceof Bomber) {
							if (((Bomber) ship).ai.target != null && ((Bomber) ship).ai.target.id != source.id) {
								continue;
							}
						} else if (ship instanceof Frigate) {
							if (((Frigate) ship).ai.target != null && ((Frigate) ship).ai.target.id != source.id) {
								continue;
							}
						}

						closestShip = ship;
						closestDistanze = currentDistance;
					}
				}
			}
		}

		return closestShip;
	}

	/*
	 * return a random ship of the desired type that's in range
	 * 0 = Fighter 1 = Bomber 2 = Frigate 3 = Factory
	 */
	public static Ship getTypeInRange(Ship source, Ship.ShipType shipType, float range) {
		if (shipType == Ship.ShipType.FIGHTER)
			return getTypeInRange(source, GameInstance.getInstance().fighters, range);
		else if (shipType == Ship.ShipType.BOMBER)
			return getTypeInRange(source, GameInstance.getInstance().bombers, range);
		else if (shipType == Ship.ShipType.FRIGATE)
			return getTypeInRange(source, GameInstance.getInstance().frigates, range);
		else if (shipType == Ship.ShipType.FACTORY)
			return getTypeInRange(source, GameInstance.getInstance().factorys, range);
		else
			return null;
	}

	/**
	 * return a random ship of the desired type that's in range
	 */
	private static Ship getTypeInRange(Ship source, Array<Ship> ships, float range) {
		Array<Ship> shipsInRange = new Array<Ship>();
		float range_squared = range * range;

		// Find the designated target_id
		int player_id = source.getID();
		int target_id = GameInstance.getInstance().targetingMap.get(player_id);
		boolean target_all_colors = false;
		if (target_id == GameInstance.TARGET_ANY)
			target_all_colors = true;

		for (int i = 0; i < ships.size; i++) {
			Ship ship = ships.get(i);
			float currentDistance = source.collisionCenter.dst(ship.collisionCenter);

			if (currentDistance <= range_squared) {
				// Exclude friendly ship
				if (friendlyFire || ship.getTeamID() != source.getTeamID() || source.getTeamID() == 0 || ship.getTeamID() == 0) {
					// Target only ships with a particular target_id
					if (ship.alive && (target_all_colors || ship.getID() == target_id)
							&& source.id != ship.id && onScreen(ship.collisionCenter)) {
						shipsInRange.add(ship);
					}
				}
			}
		}

		if (shipsInRange.size > 0) {
			return shipsInRange.get(MathUtils.random(0, shipsInRange.size - 1));
		} else {
			return null;
		}
	}
}
