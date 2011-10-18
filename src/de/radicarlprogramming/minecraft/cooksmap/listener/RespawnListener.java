package de.radicarlprogramming.minecraft.cooksmap.listener;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class RespawnListener extends PlayerListener {

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Location location = player.getCompassTarget();
		player.setCompassTarget(location);
		// TODO: Bug: compass does not show to death location, but dist
		// calculation is correct. After /cmap set everything is correct to.
		Logger.getLogger("Minecraft").info("Tot:" + location.getBlockX() + ", " + location.getBlockY() + ", "
				+ location.getBlockZ());
		if (!player.getInventory().contains(Material.COMPASS)) {
			// TODO: make this configurable
			player.getInventory().addItem(new ItemStack(Material.COMPASS));
		}
	}

}
