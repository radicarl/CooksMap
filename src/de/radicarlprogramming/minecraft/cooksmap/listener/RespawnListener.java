package de.radicarlprogramming.minecraft.cooksmap.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class RespawnListener extends PlayerListener {

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		// TODO: make this configurable
		if (!player.getInventory().contains(Material.COMPASS)) {
			player.getInventory().addItem(new ItemStack(Material.COMPASS));
		}
	}

}
