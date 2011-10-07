package de.radicarlprogramming.minecraft.cooksmap;

import java.util.HashMap;

import org.bukkit.Location;

public class Map {
	private final HashMap<Integer, Landmark> landmarksById = new HashMap<Integer, Landmark>();
	private int lastId = 0;

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

	public HashMap<Integer, Landmark> getLandmarks() {
		return this.landmarksById;
	}
}
