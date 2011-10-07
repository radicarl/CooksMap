package de.radicarlprogramming.minecraft.cooksmap;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CooksMapPlugin extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final HashMap<World, Map> maps = new HashMap<World, Map>();

	@Override
	public void onDisable() {

		this.log.info("Plugin CooksMap has been disabled");
	}

	@Override
	public void onEnable() {
		this.log.info("Plugin CooksMap has been enabled");

		this.getCommand("cmap").setExecutor(new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label,
					String[] args) {
				if (args.length > 0 && sender instanceof Player) {
					Player player = (Player) sender;
					if ("add".equals(args[0])) {
						return this.addLandmark(player);
					} else if ("set".equals(args[0]) && args.length > 1) {
						return this.setLandmark(player, args[1]);
					} else if ("rm".equals(args[0]) && args.length > 1) {
						return this.removeLandmark(player, args[1]);
					} else if ("dist".equals(args[0])) {
						return this.calculateDistance(player);
					}
				}
				return false;
			}

			private boolean calculateDistance(Player player) {
				Location playerLocation = player.getLocation();
				Location compassTarget = player.getCompassTarget();
				if (playerLocation == null) {
					player.sendMessage("You have no Location?!?");
					return true;
				}
				if (compassTarget == null) {
					player.sendMessage("You must first set a Landmark.");
					return true;
				}

				double distance = compassTarget.distance(playerLocation);
				player.sendMessage("You are " + distance + " away from your destination");
				return true;
			}

			private boolean removeLandmark(Player player, String idString) {
				try {
					int id = Integer.parseInt(idString);
					if (CooksMapPlugin.this.getMap(player).removeLandmark(id) == null) {
						player.sendMessage("No landmark with id " + idString + "found.");
					} else {
						player.sendMessage("Landmark removed.");
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

			private boolean addLandmark(Player player) {
				Location location = player.getLocation();
				int id = CooksMapPlugin.this.getMap(player).addLandmard(location);
				player.sendMessage("New landmark with id " + id + " added.");
				return true;
			}

			private boolean setLandmark(Player player, String idString) {
				try {
					int id = Integer.parseInt(idString);
					Landmark landmark = CooksMapPlugin.this.getMap(player).getLandmark(id);
					if (landmark == null) {
						player.sendMessage("No landmark with id " + idString + " found.");
					} else {
						player.setCompassTarget(landmark.getLocation());
						double distance = player.getLocation().distance(landmark.getLocation());
						player.sendMessage("New landmark as compass target set. Distance: "
								+ distance);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

		});

		// initialize Maps for all World
		for (World world : this.getServer().getWorlds()) {
			this.maps.put(world, new Map(world));
		}
	}

	private Map getMap(World world) {
		return this.maps.get(world);
	}

	private Map getMap(Player player) {
		return this.getMap(player.getWorld());
	}
}
