package de.radicarlprogramming.minecraft.cooksmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.radicarlprogramming.minecraft.cooksmap.comparator.CategoryComparator;
import de.radicarlprogramming.minecraft.cooksmap.persistence.MapLoader;
import de.radicarlprogramming.minecraft.cooksmap.persistence.MapSaver;
import de.radicarlprogramming.minecraft.cooksmap.ui.LandmarkList;

public class CooksMapPlugin extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final HashMap<World, Map> maps = new HashMap<World, Map>();
	public static int ROWS_PER_PAGE = 10;

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
			map = new Map();
			File file = new File(this.getDataFolder(), world.getName() + ".csv");
			try {
				new MapLoader(file).load(map);
				this.log.info("Loaded map for world" + world + " from map file.");
			} catch (FileNotFoundException e) {
				this.log.info("For world " + world + " exists no map file.");
			} catch (IOException e) {
				this.log.warning("Could not read map file for world " + world.getName() + ". Reason:" + e.getMessage());
			}
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

	public static int getDistance(int x1, int z1, int x2, int z2) {
		return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
	}

	public static int getDistance(Location position, Location target) {
		return CooksMapPlugin.getDistance(position.getBlockX(), position.getBlockZ(), target.getBlockX(),
				target.getBlockZ());
	}

	public static int getDistance(Location loc, Position pos) {
		return CooksMapPlugin.getDistance(loc.getBlockX(), loc.getBlockZ(), pos.getX(), pos.getZ());
	}

	public static int getLevelDifference(int position, int target) {
		return position - target;
	}

	public static int getLevelDifference(Position position, Location target) {
		return CooksMapPlugin.getLevelDifference(position.getY(), target.getBlockY());
	}

	public static int getLevelDifference(Location position, Position target) {
		return CooksMapPlugin.getLevelDifference(position.getBlockY(), target.getY());
	}

	public static int getLevelDifference(Location position, Location target) {
		return CooksMapPlugin.getLevelDifference(position.getBlockY(), target.getBlockY());
	}

	public static int getLevelDifference(Position position, Position target) {
		return CooksMapPlugin.getLevelDifference(position.getY(), target.getY());
	}

	@Override
	public void onDisable() {
		for (World world : this.maps.keySet()) {
			this.saveMap(world);
		}
		this.log.info("Plugin CooksMap has been disabled");
	}

	private void saveMap(World world) {
		Map map = this.maps.get(world);
		String worldName = world.getName();
		try {
			this.getDataFolder().mkdirs();
			File file = new File(this.getDataFolder(), worldName + ".csv");
			new MapSaver(file).safe(map);
			this.log.info("Map of world " + worldName + " saved to " + file.getAbsolutePath());
		} catch (IOException e) {
			this.log.warning("Could not save Map for world " + worldName + ". Reason: " + e.getMessage());
		}
	}

	@Override
	public void onEnable() {
		this.log.info("Plugin CooksMap has been enabled");
		this.getCommand("cmap").setExecutor(new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (args.length > 0 && sender instanceof Player) {
					Player player = (Player) sender;
					if ("add".equals(args[0]) && args.length > 2) {
						int i = 1;
						boolean isPrivate = true;
						if ("public".equals(args[i])) {
							if (args.length < 3) {
								// if optional flag public is given, there still
								// must be a type and a description
								return false;
							}
							isPrivate = false;
							i++;
						}
						String type = args[i++].toLowerCase();
						String description = args[i++];
						while (i < args.length) {
							description += " " + args[i++];
						}
						return this.addLandmark(player, type, description, isPrivate);
					} else if ("set".equals(args[0]) && args.length > 1) {
						return this.setLandmark(player, args[1]);
					} else if ("rm".equals(args[0]) && args.length > 1) {
						return this.removeLandmark(player, args[1]);
					} else if ("dist".equals(args[0])) {
						return this.calculateDistance(player);
					} else if ("list".equals(args[0])) {
						int page = 1;
						if (args.length > 1) {
							try {
								page = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								// show Page 1 if invalid number is given
							}
						}
						return this.list(player, page);
					}
				}
				return false;
			}

			private boolean list(Player player, int page) {
				Map map = CooksMapPlugin.this.getMap(player);
				ArrayList<Landmark> landmarks = map.getLandmarks(player);
				Collections.sort(map.getLandmarks(player), new CategoryComparator());

				int pages = (int) (Math.ceil(landmarks.size() / (CooksMapPlugin.ROWS_PER_PAGE - 1.0)));
				LandmarkList list = new LandmarkList(pages, player);
				int offset = (page - 1) * (CooksMapPlugin.ROWS_PER_PAGE - 1);
				int lastRow = offset + (CooksMapPlugin.ROWS_PER_PAGE - 1);
				for (; offset < landmarks.size() && offset < lastRow; offset++) {
					list.addLandmarkToList(landmarks.get(offset));
				}
				list.getPrintString(player, page);
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
				int levelDifference = CooksMapPlugin.getLevelDifference(position, target);
				player.sendMessage("Distance to target: " + distance + ". Level difference: " + levelDifference);
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
					if (CooksMapPlugin.this.getMap(player).removeLandmark(id, player) == null) {
						player.sendMessage("No landmark with id " + idString + "found.");
					} else {
						player.sendMessage("Landmark removed.");
						CooksMapPlugin.this.saveMap(player.getWorld());
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
			 * @param args
			 * @return
			 */
			private boolean addLandmark(Player player, String type, String name, boolean isPublic) {
				Location location = player.getLocation();
				type = type.replaceAll(";", ",");
				type = type.replaceAll("(\n|\r)+", " ");
				int id = CooksMapPlugin.this.getMap(player).addLandmark(location, type, name, player, isPublic);
				player.sendMessage("New landmark with id " + id + " added.");
				CooksMapPlugin.this.saveMap(player.getWorld());
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
					} else if (!landmark.isVisible(player.getName())) {
						player.sendMessage("You can not access the landmark with id " + idString + ".");
					} else {
						Position target = landmark.getPosition();
						player.setCompassTarget(Helper.createLocation(player, target));
						Location position = player.getLocation();
						int distance = CooksMapPlugin.getDistance(position, target);
						int levelDifference = CooksMapPlugin.getLevelDifference(position, target);
						player.sendMessage("Distance to " + landmark.getDescription() + ": " + distance + "/"
								+ levelDifference);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

		});
	}

}