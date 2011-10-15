package de.radicarlprogramming.minecraft.cooksmap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Helper {

	public static Location createLocation(Player player, Position position) {
		return new Location(player.getWorld(), position.getX(), position.getY(), position.getZ());
	}

}
