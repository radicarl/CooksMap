package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;

public class Landmark {

	private final int id;
	private final Position position;
	// TODO: rename to category
	private final String type;
	private final String description;
	private final boolean isPrivate;
	private final String playerName;

	public Landmark(Location location, int id, String type, String description, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(location);
		this.type = type;
		this.description = description;
		this.playerName = playerName;
		this.isPrivate = isPrivate;
	}

	public Landmark(int x, int y, int z, int id, String type, String description, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(x, y, z);
		this.type = type;
		this.description = description;
		this.isPrivate = isPrivate;
		this.playerName = playerName;
	}

	public int getId() {
		return this.id;
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

	public String getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public boolean isVisible(String name) {
		return !this.isPrivate && this.playerName.equals(name);
	}

	public boolean isPrivate() {
		return this.isPrivate;
	}
}
