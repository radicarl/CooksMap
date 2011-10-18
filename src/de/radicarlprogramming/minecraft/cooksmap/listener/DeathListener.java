package de.radicarlprogramming.minecraft.cooksmap.listener;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import de.radicarlprogramming.minecraft.cooksmap.CooksMapPlugin;

public class DeathListener extends EntityListener {
	CooksMapPlugin plugin;

	public DeathListener(CooksMapPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Location location = event.getEntity().getLocation();
			String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
			// TODO: save map
			int id = this.plugin.getMap(player).addNewLandmark(location,
					"death",
					date + " " + player.getName(),
					player,
					false);
			player.setCompassTarget(location);
			Logger.getLogger("Minecraft").info("Tot:" + location.getBlockX() + ", " + location.getBlockY() + ", "
					+ location.getBlockZ());
			player.sendMessage("Your Compass is set to your death location (id " + id + ").");
			// TODO: cleanup death landmarks after some time/amount
			// TODO: make this configurable->automatic death locations, cleanup
			// (time or amount)
		}
	}
}
