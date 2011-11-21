package de.radicarlprogramming.minecraft.cooksmap;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.listener.LandmarkChangedEvent;
import de.radicarlprogramming.minecraft.cooksmap.listener.LandmarkListener;

public class Landmark {

	private final Integer id;
	private final Position position;
	private String category;
	private String name;
	private boolean isPrivate;
	private final String playerName;
	private final List<LandmarkListener> listeners = new ArrayList<LandmarkListener>();

	public Landmark(Location location, int id, String category, String name, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(location);
		this.category = category.toLowerCase();
		this.name = name;
		this.playerName = playerName;
		this.isPrivate = isPrivate;
	}

	public Landmark(int x, int y, int z, int id, String category, String name, String playerName, boolean isPrivate) {
		this.id = id;
		this.position = new Position(x, y, z);
		this.category = category.toLowerCase();
		this.name = name;
		this.isPrivate = isPrivate;
		this.playerName = playerName;
	}

	public Integer getId() {
		return new Integer(this.id);
	}

	public Integer getI() {
		return this.getId();
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

	public String getC() {
		return this.getCategory();
	}

	public String getName() {
		return this.name;
	}

	public String getN() {
		return this.getName();
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

	public Distance getDistance(Player player) {
		return Distance.calculateDistance(player, this);
	}

	public Distance getD(Player player) {
		return this.getDistance(player);
	}

	public void changeLandmark(String visibility, String category, String name) {
		LandmarkChangedEvent event = new LandmarkChangedEvent(this);
		if (category != null && !category.isEmpty() && !category.equalsIgnoreCase(this.category)) {
			this.category = category.toLowerCase();
			event.markCategoryAsChanged();
		}
		if (name != null && !name.isEmpty() && !name.equals(this.name)) {
			this.name = name;
			event.markNameAsChanged();
		}
		if (visibility != null && !visibility.isEmpty()) {
			visibility = visibility.trim();
			if ("+".equals(visibility) && this.isPrivate) {
				// landmark was private, but now it should be public
				this.isPrivate = false;
				event.markVisibilityAsChanged();
			} else if ("-".equals(visibility) && !this.isPrivate) {
				// landmark is public, but now it should be private
				this.isPrivate = true;
				event.markVisibilityAsChanged();
			}
		}
		Logger.getLogger("Minecraft").info("has changed:" + event.isLandmarkChanged());
		Logger.getLogger("Minecraft").info("has visibility changed:" + event.isVisibilityChanged());
		if (event.isLandmarkChanged()) {
			for (LandmarkListener listener : this.listeners) {
				Logger.getLogger("Minecraft").info("notify:" + listener.toString());
				listener.landmarkChanged(event);
			}
		}
	}

	public void addListener(LandmarkListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeListener(LandmarkListener listener) {
		this.listeners.remove(listener);
	}

	public void removeAllListener() {
		this.listeners.clear();
	}
}
