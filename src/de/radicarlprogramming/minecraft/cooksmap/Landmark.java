package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;

public class Landmark {

	private final Location location;

	public Landmark(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}
}
