package de.radicarlprogramming.minecraft.cooksmap;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;

public class Map {
	private final HashMap<Integer, Landmark> landmarksById = new HashMap<Integer, Landmark>();
	private final World world;
	private int lastId = 0;

	public Map(World world) {
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public Landmark getLandmark(int id) {
		return this.landmarksById.get(new Integer(id));
	}

	public int addLandmard(Location location) {
		this.landmarksById.put(new Integer(++this.lastId), new Landmark(location));
		return this.lastId;
	}

	public Landmark removeLandmark(int id) {
		return this.landmarksById.remove(new Integer(id));

	}
}
