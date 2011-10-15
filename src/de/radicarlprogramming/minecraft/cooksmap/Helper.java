package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Helper {

	public static Location createLocation(Player player, Position position) {
		return new Location(player.getWorld(), position.getX(), position.getY(), position.getZ());
	}

	public static int getDistance(int x1, int z1, int x2, int z2) {
		return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
	}

	public static int getDistance(Location position, Location target) {
		return getDistance(position.getBlockX(), position.getBlockZ(), target.getBlockX(),
				target.getBlockZ());
	}

	public static int getDistance(Location loc, Position pos) {
		return getDistance(loc.getBlockX(), loc.getBlockZ(), pos.getX(), pos.getZ());
	}

	public static int getLevelDifference(int position, int target) {
		return position - target;
	}

	public static int getLevelDifference(Position position, Location target) {
		return getLevelDifference(position.getY(), target.getBlockY());
	}

	public static int getLevelDifference(Location position, Position target) {
		return getLevelDifference(position.getBlockY(), target.getY());
	}

	public static int getLevelDifference(Location position, Location target) {
		return getLevelDifference(position.getBlockY(), target.getBlockY());
	}

	public static int getLevelDifference(Position position, Position target) {
		return getLevelDifference(position.getY(), target.getY());
	}

}
