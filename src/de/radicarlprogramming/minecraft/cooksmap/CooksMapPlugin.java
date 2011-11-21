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
import de.radicarlprogramming.minecraft.cooksmap.ui.Manual;
import de.radicarlprogramming.minecraft.cooksmap.util.Filter;
import de.radicarlprogramming.minecraft.cooksmap.util.InvalidFilterException;
import de.radicarlprogramming.minecraft.cooksmap.util.LandmarkFilterer;

// TODO: test multiworld capability
// TODO: junit tests, how to mock World, Player, tests eventlistner?
// TODO: Version support: http://wiki.bukkit.org/Version_tracking_Tutorial
// TODO: Config: http://wiki.bukkit.org/Introduction_to_the_New_Configuration
public class CooksMapPlugin extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	private final HashMap<World, Map> maps = new HashMap<World, Map>();
	private final DeathListener deathListener = new DeathListener(this);
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
			private static final String REGEX = "( c(ategory)?=(\\w*)| n(ame)?=([^=]*)| v(isibility)?=([\\+\\-])|())+";
			private static final int INDEX_CATEGORY = 3;
			private static final int INDEX_NAME = 5;
			private static final int INDEX_VISIBILITY = 7;
			private final Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (args.length > 0 && sender instanceof Player) {
					Player player = (Player) sender;
					String cmapCommand = args[0].toLowerCase();
					if ("add".equals(cmapCommand) && args.length > 2) {
						return this.addLandmark(player, args);
					} else if ("set".equals(cmapCommand) && args.length > 1) {
						return this.setLandmark(player, args[1]);
					} else if ("del".equals(cmapCommand) && args.length > 1) {
						return this.removeLandmark(player, args[1]);
					} else if ("dist".equals(cmapCommand)) {
						return this.calculateDistance(player);
					} else if ("list".equals(cmapCommand)) {
						return this.list(player, args);
					} else if ("edit".equals(cmapCommand) && args.length > 2) {
						return this.editLandmark(player, args);
					} else if ("n".equals(cmapCommand)) {
						return this.listNextPage(player);
					} else if ("p".equals(cmapCommand)) {
						return this.listPreviousPage(player);
					} else if ("goto".equals(cmapCommand) && args.length > 1) {
						return this.gotoPage(player, args[1]);
					} else if ("help".equals(cmapCommand) && args.length > 1) {
						return Manual.printHelp(player, args[1]);
					}

					// TODO implement command show/explain/... (shows last list
					// command)
					// TODO implement admin command for deleting landmarks owned
					// by other players
				}
				// TODO: always return true, show own usage page cause of
				// readability
				return false;
			}

			private boolean listNextPage(Player player) {
				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				session.incrementPage();
				return this.displayList(player, session);
			}

			private boolean listPreviousPage(Player player) {
				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				session.decrementPage();
				return this.displayList(player, session);
			}

			private boolean gotoPage(Player player, String pageString) {
				try {
					int page = Integer.parseInt(pageString);
					CooksMapSession session = CooksMapPlugin.this.getSession(player);
					session.setPage(page);
					return this.displayList(player, session);
				} catch (NumberFormatException e) {
					player.sendMessage("Page must be an integer");
				}
				return false;
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
						landmark.changeLandmark(matcher.group(INDEX_VISIBILITY),
								matcher.group(INDEX_CATEGORY),
								matcher.group(INDEX_NAME));
						CooksMapPlugin.this.saveMap(player.getWorld());
					}
					LandmarkTable landmarkTable = new LandmarkTable(1, player);
					landmarkTable.addLandmarkToList(landmark);
					landmarkTable.print(player, 1);
					player.sendMessage("Landmark was changed");
				} catch (NumberFormatException e) {
					player.sendMessage("Id must be an integer");
				}
				return true;
			}

			private boolean list(Player player, String[] args) {
				int page = 1;
				int argsLength = args.length;
				try {
					// Last argument is a page number, so it can not be a filter
					// or sort argument
					page = Integer.parseInt(args[argsLength - 1]);
					argsLength--;
				} catch (NumberFormatException e) {
					// Last argument is no page number
				}

				LandmarkComparator firstComparator = null;
				LandmarkComparator lastComparator = null;
				LandmarkFilterer filterer = new LandmarkFilterer();
				for (int i = 1; i < argsLength; i++) {
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
							filterer.addFilter(new Filter(arg, player));
						} catch (InvalidFilterException e1) {
							CooksMapPlugin.this.log.warning(e1.getMessage());
						} catch (NoSuchMethodException e2) {
							CooksMapPlugin.this.log.warning("Invalid filter colum in " + arg);
						}
					}
				}

				CooksMapSession session = CooksMapPlugin.this.getSession(player);
				session.setPage(page);
				session.setFilterer(filterer);
				session.setComparator(firstComparator);

				return this.displayList(player, session);
			}

			private boolean displayList(Player player, CooksMapSession session) {
				List<Landmark> landmarks = session.getFilterer().filter(CooksMapPlugin.this.getMap(player)
						.getLandmarks(player));
				Collections.sort(landmarks, session.getComparator());
				int page = session.getPage();
				int pages = (int) (Math.ceil(landmarks.size() / (CooksMapPlugin.ROWS_PER_PAGE - 1.0)));
				if (page > pages) {
					session.setPage(pages);
					page = pages;
				}
				LandmarkTable table = new LandmarkTable(pages, player);
				int offset = (page - 1) * (CooksMapPlugin.ROWS_PER_PAGE - 1);
				int lastRow = offset + (CooksMapPlugin.ROWS_PER_PAGE - 1);
				for (; offset < landmarks.size() && offset < lastRow; offset++) {
					table.addLandmarkToList(landmarks.get(offset));
				}
				table.print(player, page);
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
					try {
						if (CooksMapPlugin.this.getMap(player).removeLandmark(id, player) == null) {
							player.sendMessage("No landmark with id " + idString + "found.");
						} else {
							player.sendMessage("Landmark removed.");
							CooksMapPlugin.this.saveMap(player.getWorld());
						}
					} catch (OwnershipException e1) {
						player.sendMessage(e1.getMessage());
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
				if ("+".equals(args[1]) || "-".equals(args[1])) {
					if (args.length < 3) {
						// if optional flag public is given, there still
						// must be a type and a name
						return false;
					}
					isPrivate = "-".equals(args[1]);
					i = 2;
				}
				String type = args[i++].toLowerCase();
				String name = args[i++];
				while (i < args.length) {
					name += " " + args[i++];
				}
				Location location = player.getLocation();
				type = MapSaver.escapeString(type);
				name = MapSaver.escapeString(name);
				Map map = CooksMapPlugin.this.getMap(player);
				int id = map.addNewLandmark(location, type, name, player, isPrivate);
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
				// TODO: set by name, if more then one matches, show list
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
						player.sendMessage("New Target: " + landmark.getName() + ". " + distance);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("id must be an integer");
				}
				return true;
			}

		});
	}
}