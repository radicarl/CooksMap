package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;

public class Position {
	private final int x;
	private final int y;
	private final int z;

	public Position(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}

	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

}