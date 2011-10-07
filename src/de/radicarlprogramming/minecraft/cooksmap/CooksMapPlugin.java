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

	/**
	 * Returns the map for the given world. If no map for the given world
	 * exists, a new map is created.
	 * 
	 * @param world
	 * @return Map map
	 */
	private Map getMap(World world) {
		Map map = this.maps.get(world);
		if (map == null) {
			// TODO: read landmarks from config file
			map = new Map();
			this.maps.put(world, map);
		}
		return map;
	}

	/**
	 * Returns the map for the world of the given player. If no map for this
	 * player exists, a new map is created.
	 * 
	 * @param player
	 * @return Map map
	 */
	private Map getMap(Player player) {
		// TODO: what if world is null? can it be?
		return this.getMap(player.getWorld());
	}

	/**
	 * Returns the height difference between target and position as int.
	 * 
	 * @param target
	 * @param position
	 * @return
	 */
	public static int getEvelationDifference(Location target, Location position) {
		return target.getBlockY() - position.getBlockY();
	}

	/**
	 * Returns the distance in the XZ-Layer between loc1 and loc2 as int.
	 * 
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public static int getDistance(Location loc1, Location loc2) {
		return (int) Math.sqrt(Math.pow(loc1.getBlockX() - loc2.getBlockX(), 2)
				+ Math.pow(loc1.getBlockZ() - loc2.getBlockZ(), 2));
	}

	@Override
	public void onDisable() {
		// TODO: save maps
		this.log.info("Plugin CooksMap has been disabled");
	}

	@Override
	public void onEnable() {
		this.log.info("Plugin CooksMap has been enabled");
		this.getCommand("cmap").setExecutor(new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
					} else if ("list".equals(args[0])) {
						return this.list(player);
					}
				}
				return false;
			}

			private boolean list(Player player) {
				Map map = CooksMapPlugin.this.getMap(player);
				HashMap<Integer, Landmark> landmarks = map.getLandmarks();

				LandmarkList list = new LandmarkList();
				for (Integer id : landmarks.keySet()) {
					if (!list.addLandmarkToList(id, landmarks.get(id))) {
						break;
					}
				}
				list.getPrintString(player);
				return true;
			}

			/**
			 * Prints the distance between the current player position and the
			 * current compass target.
			 * 
			 * @param player
			 * @return
			 */
			private boolean calculateDistance(Player player) {
				Location position = player.getLocation();
				Location target = player.getCompassTarget();
				if (position == null) {
					player.sendMessage("You have no Location?!?");
					return true;
				}
				if (target == null) {
					player.sendMessage("You must first set a Landmark.");
					return true;
				}

				int distance = CooksMapPlugin.getDistance(position, target);
				int evelationDifference = CooksMapPlugin.getEvelationDifference(target, position);
				player.sendMessage("Distance to target: " + distance + ". Evelation difference: " + evelationDifference);
				return true;
			}

			/**
			 * Removes the landmark identified by idString from the map of the
			 * world of the given player.
			 * 
			 * @param player
			 * @param idString
			 * @return
			 */
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

			/**
			 * Adds the current position of player as landmark to the map of the
			 * world of the player.
			 * 
			 * @param player
			 * @return
			 */
			private boolean addLandmark(Player player) {
				Location location = player.getLocation();
				int id = CooksMapPlugin.this.getMap(player).addLandmard(location);
				player.sendMessage("New landmark with id " + id + " added.");
				return true;
			}

			/**
			 * Sets the landmark identified by idString as compass target for
			 * player.
			 * 
			 * @param player
			 * @param idString
			 * @return
			 */
			private boolean setLandmark(Player player, String idString) {
				try {
					int id = Integer.parseInt(idString);
					Landmark landmark = CooksMapPlugin.this.getMap(player).getLandmark(id);
					if (landmark == null) {
						player.sendMessage("No landmark with id " + idString + " found.");
					} else {
						Location target = landmark.getLocation();
						player.setCompassTarget(target);
						Location position = player.getLocation();
						int distance = CooksMapPlugin.getDistance(position, target);
						int evelationDifference = CooksMapPlugin.getEvelationDifference(target, position);
						player.sendMessage("Distance to new target: " + distance + ". Elevation difference: "
								+ evelationDifference);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

		});
	}
}
