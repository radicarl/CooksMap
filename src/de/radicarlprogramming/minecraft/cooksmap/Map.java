package de.radicarlprogramming.minecraft.cooksmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Map {
	/**
	 * Contains all landmarks mapped to their ids.
	 */
	// TODO: use HashMap cause of performance, cause sorting is only needed for
	// listing, and this will be done by using the landmarks list for the
	// player.
	private final TreeMap<Integer, Landmark> landmarksById = new TreeMap<Integer, Landmark>();

	/**
	 * Contains the list of landmarks visible to the mapped player. A list for a
	 * player is only generated if needed. All for this player visible landmarks
	 * are automatically added to the list.
	 */
	private final HashMap<String, ArrayList<Landmark>> landmarksByPlayer = new HashMap<String, ArrayList<Landmark>>();

	private int lastId = 0;
	private final static Logger log = Logger.getLogger("Minecraft");

	public Landmark getLandmark(int id) {
		return this.landmarksById.get(new Integer(id));
	}

	/**
	 * Adds a new landmark with the given location, type, description, player
	 * name and visible state to the map and to the landmarks list of those
	 * player who can see the new landmark. Returns the id for this landmark.
	 * 
	 * @param location
	 *            x,y and z coordinates for the landmark
	 * @param type
	 *            , type of the landmark (for example: home, mine, dungeon...)
	 * @param description
	 * @param player
	 *            , name of the player who owns the new landmark
	 * @param isPublic
	 *            , true if everybody can see the new landmark
	 * @return id of the added landmark
	 */
	public int addLandmark(Location location, String type, String description, Player player, boolean isPublic) {
		Landmark landmark = new Landmark(location, ++this.lastId, type, description, player.getName(), isPublic);
		this.landmarksById.put(new Integer(landmark.getId()), landmark);
		this.add2PlayersLists(landmark);
		return this.lastId;
	}

	/**
	 * Adds the given landmark to all already existing player lists of those
	 * players who can see the landmark. No new player lists will be generated.
	 * 
	 * @param landmark
	 */
	private void add2PlayersLists(Landmark landmark) {
		for (Entry<String, ArrayList<Landmark>> entry : this.landmarksByPlayer.entrySet()) {
			if (landmark.isVisible(entry.getKey())) {
				entry.getValue().add(landmark);
			}
		}
	}

	/**
	 * Removes the landmark identified by the given id from all list it is
	 * contained. Returns the removed landmark.
	 * 
	 * @param id
	 * @param removedBy
	 * @return
	 */
	public Landmark removeLandmark(int id, Player removedBy) {
		// TODO: check if player owns the landmark, otherwise throw exception
		// remove Landmark from global list
		Landmark landmark = this.landmarksById.remove(new Integer(id));
		if (landmark != null) {
			ArrayList<Landmark> playersLandmarks = this.landmarksByPlayer.get(removedBy.getName());
			if (playersLandmarks != null) {
				playersLandmarks.remove(landmark);
				if (!landmark.isPrivate()) {
					// if Landmark was public, remove it from all other lists
					for (ArrayList<Landmark> landmarks : this.landmarksByPlayer.values())
						landmarks.remove(landmark);
				}
			} else {
				Map.log.warning("Could not remove landmark with id " + id + ". Reason: no map found for player "
						+ removedBy.getName() + ".");
			}
		}
		return landmark;
	}

	@Deprecated
	public TreeMap<Integer, Landmark> getLandmarks() {
		// TODO: remove this function
		return this.landmarksById;
	}

	/**
	 * Adds the given landmark to the map and to all already existing player
	 * lists. New player lists are not generated.
	 * 
	 * @param landmark
	 */
	public int addLandmark(Landmark landmark) {
		Integer id = new Integer(landmark.getId());
		if (!this.landmarksById.containsKey(id)) {
			this.landmarksById.put(id, landmark);
			this.lastId = Math.max(id, this.lastId);
			this.add2PlayersLists(landmark);
		} else {
			// TODO: throw exception
			Map.log.warning("Could not add Landmark with id " + id
					+ ". Reason: a landmark with this id already exists.");
		}
		return this.lastId;
	}

	public ArrayList<Landmark> getLandmarks(Player player) {
		return this.getLandmarks(player.getName());
	}

	/**
	 * Returns the list of Landmarks visible for the given player. If the list
	 * does not exists, it is created and filled with the specified landmarks.
	 * 
	 * @param name
	 * @return
	 */
	public ArrayList<Landmark> getLandmarks(String name) {
		ArrayList<Landmark> landmarks = this.landmarksByPlayer.get(name);
		if (landmarks == null) {
			// create new List for this player name
			landmarks = new ArrayList<Landmark>();
			this.landmarksByPlayer.put(name, landmarks);

			// add all public landmarks to the new list
			for (Landmark landmark : this.landmarksById.values()) {
				if (landmark.isVisible(name)) {
					landmarks.add(landmark);
				}
			}
		}
		return landmarks;
	}
}
