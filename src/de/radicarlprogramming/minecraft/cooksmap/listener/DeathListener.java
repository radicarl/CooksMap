package de.radicarlprogramming.minecraft.cooksmap.listener;

import java.text.DateFormat;
import java.util.Date;

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
			int id = this.plugin.getMap(player).addNewLandmark(location,
					"death",
					date + " " + player.getName(),
					player,
					false);
			player.sendMessage("Your death landmark has id " + id + ". Set your compass target: /cmap set " + id);
			this.plugin.saveMap(player.getWorld());
		}
	}
}
