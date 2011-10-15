package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;

public class Landmark {

	private final int id;
	private final Position position;
	private final String category;
	private final String description;
	private final boolean isPrivate;
	private final String playerName;

	public Landmark(Location location, int id, String type, String description, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(location);
		this.category = type;
		this.description = description;
		this.playerName = playerName;
		this.isPrivate = isPrivate;
	}

	public Landmark(int x, int y, int z, int id, String type, String description, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(x, y, z);
		this.category = type;
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

	public String getCategory() {
		return this.category;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public boolean isVisible(String name) {
		return this.playerName.equals(name) || !this.isPrivate();
	}

	public boolean isPrivate() {
		return this.isPrivate;
	}
}
