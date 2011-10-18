package de.radicarlprogramming.minecraft.cooksmap;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Distance implements Comparable<Distance> {

	private final int distance;
	private final int levelDifference;
	/**
	 * contains the last location of a player used for a distance calculation.
	 */
	private static HashMap<Player, Location> lastPlayerPosition = new HashMap<Player, Location>();

	/**
	 */
	private static HashMap<Player, HashMap<Landmark, Distance>> lastCalculatedDistances = new HashMap<Player, HashMap<Landmark, Distance>>();

	public Distance(int distance, int levelDifference) {
		this.distance = distance;
		this.levelDifference = levelDifference;
	}

	public Distance(String[] distance) throws NumberFormatException {
		try {
			this.distance = (distance.length > 0) ? Integer.parseInt(distance[0]) : 0;
			this.levelDifference = (distance.length > 1) ? Integer.parseInt(distance[1]) : 0;
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	public int getDistance() {
		return this.distance;
	}

	public int getLevelDifference() {
		return this.levelDifference;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Distance: ");
		buffer.append(this.distance);
		buffer.append("/");
		buffer.append(this.levelDifference);
		return buffer.toString();
	}

	/**
	 * Calculates Distance between player location and landmark. Caches the
	 * result until the player location has changed.
	 * 
	 * @param player
	 * @param landmark
	 * @return
	 */
	public static Distance calculateDistance(Player player, Landmark landmark) {
		Location pos = player.getLocation();
		Location lastPos = Distance.lastPlayerPosition.get(player);
		if (lastPos == null || pos.getBlockX() != lastPos.getBlockX() || pos.getBlockY() != lastPos.getBlockY()
				|| pos.getBlockZ() != lastPos.getBlockZ()) {
			// position has changed, reset all calculated distances for this
			// player
			Distance.resetDistances(player);
		}
		Distance distance = Distance.getDistance(player, landmark);
		if (distance == null) {
			distance = Distance.calculateDistance(pos.getBlockX(),
					pos.getBlockY(),
					pos.getBlockZ(),
					landmark.getX(),
					landmark.getY(),
					landmark.getZ());
			Distance.lastPlayerPosition.put(player, pos);
			Distance.setDistance(player, landmark, distance);
		}
		return distance;
	}

	private static void resetDistances(Player player) {
		Distance.lastCalculatedDistances.put(player, new HashMap<Landmark, Distance>());
	}

	private static void setDistance(Player player, Landmark landmark, Distance distance) {
		HashMap<Landmark, Distance> distances = Distance.lastCalculatedDistances.get(player);
		if (distances == null) {
			distances = new HashMap<Landmark, Distance>();
			Distance.lastCalculatedDistances.put(player, distances);
		}
		distances.put(landmark, distance);

	}

	private static Distance getDistance(Player player, Landmark landmark) {
		HashMap<Landmark, Distance> distances = Distance.lastCalculatedDistances.get(player);
		if (distances == null) {
			distances = new HashMap<Landmark, Distance>();
			Distance.lastCalculatedDistances.put(player, distances);
		}
		return distances.get(landmark);
	}

	/**
	 * Calculates the Distance between position and target. The result is not
	 * cached.
	 * 
	 * @param pos
	 * @param target
	 * @return
	 */
	public static Distance calculateDistance(Location pos, Location target) {
		return Distance.calculateDistance(pos.getBlockX(),
				pos.getBlockY(),
				pos.getBlockZ(),
				target.getBlockX(),
				target.getBlockY(),
				target.getBlockZ());
	}

	private static Distance calculateDistance(int posX, int posY, int posZ, int targetX, int targetY, int targetZ) {
		return new Distance((int) Math.sqrt(Math.pow(posX - targetX, 2) + Math.pow(posZ - targetZ, 2)), posY - targetY);
	}

	@Override
	public int compareTo(Distance o) {
		int difference = this.distance - o.distance;
		if (difference == 0) {
			difference = Math.abs(this.levelDifference) - Math.abs(o.levelDifference);
		}
		return difference;
	}
}
