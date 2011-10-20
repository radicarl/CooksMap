package de.radicarlprogramming.minecraft.cooksmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.radicarlprogramming.minecraft.cooksmap.comparator.LandmarkComparator;
import de.radicarlprogramming.minecraft.cooksmap.listener.DeathListener;
import de.radicarlprogramming.minecraft.cooksmap.listener.RespawnListener;
import de.radicarlprogramming.minecraft.cooksmap.persistence.MapLoader;
import de.radicarlprogramming.minecraft.cooksmap.persistence.MapSaver;
import de.radicarlprogramming.minecraft.cooksmap.ui.LandmarkTable;
import de.radicarlprogramming.minecraft.cooksmap.util.Filter;
import de.radicarlprogramming.minecraft.cooksmap.util.InvalidFilterException;
import de.radicarlprogramming.minecraft.cooksmap.util.LandmarkFilterer;

// TODO: test multiworld capability
// TODO: junit tests, how to mock World, Player, tests eventlistner?
public class CooksMapPlugin extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final HashMap<World, Map> maps = new HashMap<World, Map>();
	private final DeathListener deathListener = new DeathListener(this);
	private final HashMap<Player, Location> deathLocations = new HashMap<Player, Location>();
	private final Listener respawnListener = new RespawnListener();
	private final HashMap<Player, CooksMapSession> sessions = new HashMap<Player, CooksMapSession>();
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
	public Map getMap(Player player) {
		// TODO: what if world is null? can it be?
		return this.getMap(player.getWorld());
	}

	private CooksMapSession getSession(Player player) {
		CooksMapSession session = this.sessions.get(player);
		if (session == null) {
			session = new CooksMapSession();
			this.sessions.put(player, session);
		}
		return session;
	}

	@Override
	public void onDisable() {
		for (World world : this.maps.keySet()) {
			this.saveMap(world);
		}
		this.log.info("Plugin CooksMap has been disabled");
	}

	public void saveMap(World world) {
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
		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvent(Event.Type.ENTITY_DEATH, this.deathListener, Event.Priority.Monitor, this);
		pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, this.respawnListener, Event.Priority.Normal, this);
		this.getCommand("cmap").setExecutor(new CommandExecutor() {
			private static final String REGEX = "( category=(\\w*)| description=([^=]*)| visibility=([\\+\\-])|())+";
			private static final int INDEX_CATEGORY = 2;
			private static final int INDEX_DESCRIPTION = 3;
			private static final int INDEX_VISIBILITY = 4;
			private final Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (args.length > 0 && sender instanceof Player) {
					Player player = (Player) sender;
					if ("add".equalsIgnoreCase(args[0]) && args.length > 2) {
						return this.addLandmark(player, args);
					} else if ("set".equalsIgnoreCase(args[0]) && args.length > 1) {
						return this.setLandmark(player, args[1]);
					} else if ("rm".equalsIgnoreCase(args[0]) && args.length > 1) {
						return this.removeLandmark(player, args[1]);
					} else if ("dist".equalsIgnoreCase(args[0])) {
						return this.calculateDistance(player);
					} else if ("list".equalsIgnoreCase(args[0])) {
						return this.list(player, args);
					} else if ("edit".equalsIgnoreCase(args[0]) && args.length > 2) {
						return this.editLandmark(player, args);
					} else if ("next".equalsIgnoreCase(args[0])) {
						return this.listNextPage(player);
					} else if ("prev".equalsIgnoreCase(args[0])) {
						return this.listPreviousPage(player);

					}
				}
				return false;
			}

			private boolean listNextPage(Player player) {
				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				return this.displayList(player, session.getPage() + 1, session.getLandmarkList());
			}

			private boolean listPreviousPage(Player player) {
				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				int page = Math.max(1, session.getPage() - 1);
				return this.displayList(player, page, session.getLandmarkList());
			}

			private boolean editLandmark(Player player, String[] args) {
				try {
					int id = Integer.parseInt(args[1]);
					Landmark landmark = CooksMapPlugin.this.getMap(player).getLandmark(id);
					if (landmark == null) {
						player.sendMessage("No landmark with id " + id + " found.");
						return true;
					}
					if (!player.getName().equals(landmark.getPlayerName())) {
						player.sendMessage("You can only edit your own landmarks.");
						return true;
					}
					String arguments = "";
					for (int i = 2; i < args.length; i++) {
						arguments += " " + args[i];
					}
					Matcher matcher = this.pattern.matcher(arguments);
					if (matcher.matches()) {
						// TODO: if visibility has changed, update all lists
						// (map and session)
						// TODO: let map do the change for easier listener calls
						landmark.setVisibility(matcher.group(INDEX_VISIBILITY));
						landmark.setDescription(matcher.group(INDEX_DESCRIPTION));
						landmark.setCategory(matcher.group(INDEX_CATEGORY));
						CooksMapPlugin.this.saveMap(player.getWorld());
					}
					// TODO: feedback
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}

			private boolean list(Player player, String[] args) {
				int page = 1;
				LandmarkComparator firstComparator = null;
				LandmarkComparator lastComparator = null;
				LandmarkFilterer filterer = new LandmarkFilterer();
				for (int i = 1; i < args.length; i++) {
					String arg = args[i];
					if (arg.matches("(\\+|-).+")) {
						LandmarkComparator comparator = LandmarkComparator.createComparator(arg, player);
						if (firstComparator == null) {
							firstComparator = comparator;
							lastComparator = comparator;
						} else {
							lastComparator.setNextComparator(comparator);
							lastComparator = comparator;
						}

					} else {
						try {
							page = Integer.parseInt(arg);
						} catch (NumberFormatException e) {
							// stay on last set page or on page 1 and try if arg
							// is a filter
							try {
								filterer.addFilter(new Filter(arg, player));
							} catch (InvalidFilterException e1) {
								CooksMapPlugin.this.log.warning(e1.getMessage());
							} catch (NoSuchMethodException e2) {
								CooksMapPlugin.this.log.warning("Invalid filter colum in " + arg);
							}
						}
					}
				}

				Map map = CooksMapPlugin.this.getMap(player);
				List<Landmark> landmarks = filterer.filter(map.getLandmarks(player));
				if (firstComparator != null) {
					Collections.sort(map.getLandmarks(player), firstComparator);
				}

				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				session.setLandmarkList(landmarks);
				session.setPage(page);

				return this.displayList(player, page, landmarks);
			}

			private boolean displayList(Player player, int page, List<Landmark> landmarks) {
				int pages = (int) (Math.ceil(landmarks.size() / (CooksMapPlugin.ROWS_PER_PAGE - 1.0)));
				LandmarkTable table = new LandmarkTable(pages, player);
				int offset = (page - 1) * (CooksMapPlugin.ROWS_PER_PAGE - 1);
				int lastRow = offset + (CooksMapPlugin.ROWS_PER_PAGE - 1);
				for (; offset < landmarks.size() && offset < lastRow; offset++) {
					table.addLandmarkToList(landmarks.get(offset));
				}
				table.getPrintString(player, page);
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

				Distance distance = Distance.calculateDistance(position, target);
				player.sendMessage(distance.toString());
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
			private boolean addLandmark(Player player, String[] args) {
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
				Location location = player.getLocation();
				type = MapSaver.escapeString(type);
				description = MapSaver.escapeString(description);
				Map map = CooksMapPlugin.this.getMap(player);
				int id = map.addNewLandmark(location, type, description, player, isPrivate);
				// TODO coords/landmark ausgeben
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
				// TODO: set by description, if more then one matches, show list
				try {
					int id = Integer.parseInt(idString);
					Landmark landmark = CooksMapPlugin.this.getMap(player).getLandmark(id);
					if (landmark == null) {
						player.sendMessage("No landmark with id " + idString + " found.");
					} else if (!landmark.isVisible(player.getName())) {
						player.sendMessage("You can not access the landmark with id " + idString + ".");
					} else {
						Position target = landmark.getPosition();
						player.setCompassTarget(new Location(player.getWorld(), target.getX(), target.getY(), target
								.getZ()));
						Distance distance = Distance.calculateDistance(player, landmark);
						player.sendMessage("New Target: " + landmark.getDescription() + ". " + distance);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

		});
	}

	public void setPlayerDeathLocation(Player player, Location location) {
		this.deathLocations.put(player, location);

	}

	public Location getDeathlocation(Player player) {
		return this.deathLocations.remove(player);
	}
}