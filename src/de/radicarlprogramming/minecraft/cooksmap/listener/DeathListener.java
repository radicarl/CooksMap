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
					true);
			player.sendMessage("You died. To set your compass target to your death location enter:");
			player.sendMessage("/cmap set " + id);
			// TODO remove death oldest locations of this player while there are
			// more then x.
			// TODO make x configurable
			this.plugin.saveMap(player.getWorld());
		}
	}
}
