package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;

public class Landmark {

	private final Position position;

	public Landmark(Position position) {
		this.position = position;
	}

	public Landmark(Location location) {
		this.position = new Position(location);
	}

	public Landmark(int x, int y, int z) {
		this.position = new Position(x, y, z);
	}

	public Position getPosition() {
		return this.position;
	}

	public int getX() {
		return this.position.getX();
	}

	public int getY() {
		return this.position.getY();
	}

	public int getZ() {
		return this.position.getZ();
	}
}
