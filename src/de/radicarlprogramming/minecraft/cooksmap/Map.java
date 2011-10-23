package de.radicarlprogramming.minecraft.cooksmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.listener.LandmarkChangedEvent;
import de.radicarlprogramming.minecraft.cooksmap.listener.LandmarkListener;

public class Map implements LandmarkListener {
	/**
	 * Contains all landmarks mapped to their ids.
	 */
	private final HashMap<Integer, Landmark> landmarksById = new HashMap<Integer, Landmark>();

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
	 * Adds a new landmark with the given location, type, name, player name and
	 * visible state to the map and to the landmarks list of those player who
	 * can see the new landmark. Returns the id for this landmark.
	 * 
	 * @param location
	 *            x,y and z coordinates for the landmark
	 * @param type
	 *            , type of the landmark (for example: home, mine, dungeon...)
	 * @param name
	 * @param player
	 *            , name of the player who owns the new landmark
	 * @param isPublic
	 *            , true if everybody can see the new landmark
	 * @return id of the added landmark
	 */
	public int addNewLandmark(Location location, String type, String name, Player player, boolean isPublic) {
		Landmark landmark = new Landmark(location, ++this.lastId, type, name, player.getName(), isPublic);
		this.landmarksById.put(landmark.getId(), landmark);
		this.addToPlayersLists(landmark);

		// register map as LandmarkListener
		landmark.addListener(this);
		return this.lastId;
	}

	/**
	 * Adds the given landmark to all already existing player lists of those
	 * players who can see the landmark. No new player lists will be generated.
	 * 
	 * @param landmark
	 */
	private void addToPlayersLists(Landmark landmark) {
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
			// remove Landmark from owners list
			ArrayList<Landmark> playersLandmarks = this.landmarksByPlayer.get(removedBy.getName());
			// the list for the player have not been create yet -> null check
			// getLandmarks(player) is not used, because it is not necessary to
			// create the list for removing the landmark
			if (playersLandmarks != null) {
				playersLandmarks.remove(landmark);
			}
			if (!landmark.isPrivate()) {
				// if Landmark was public, remove it from all other lists
				for (ArrayList<Landmark> landmarks : this.landmarksByPlayer.values())
					landmarks.remove(landmark);
			}

			// remove all Listeners from landmark
			landmark.removeAllListener();
		}
		return landmark;
	}

	public HashMap<Integer, Landmark> getLandmarks() {
		return this.landmarksById;
	}

	/**
	 * Adds the given landmark to the map and to all already existing player
	 * lists. New player lists are not generated.
	 * 
	 * @param landmark
	 */
	public int addLandmark(Landmark landmark) {
		Integer id = landmark.getId();
		if (!this.landmarksById.containsKey(id)) {
			this.landmarksById.put(id, landmark);
			this.lastId = Math.max(id, this.lastId);
			this.addToPlayersLists(landmark);
			landmark.addListener(this);
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
	 * does not exists, it is created and filled with the for the given player
	 * visible landmarks.
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

	@Override
	public void landmarkChanged(LandmarkChangedEvent event) {
		Landmark landmark = event.getLandmark();
		Map.log.info("visibilityChandeg: " + event.isVisibilityChanged());
		if (event.isVisibilityChanged()) {
			// add or remove the landmark to the player lists
			Map.log.info("landmark changed:" + landmark.getId());
			for (Entry<String, ArrayList<Landmark>> entry : this.landmarksByPlayer.entrySet()) {
				String playerName = entry.getKey();
				ArrayList<Landmark> landmarkList = entry.getValue();
				Map.log.info("editing list for player :" + playerName);
				if (landmark.isVisible(playerName) && !landmarkList.contains(landmark)) {
					landmarkList.add(landmark);
					Map.log.info("adding landmark to list of player :" + playerName);
				} else if (!landmark.isVisible(playerName) && landmarkList.contains(landmark)) {
					landmarkList.remove(landmark);
					Map.log.info("removing landmark from list of player :" + playerName);
				}
			}
		}
	}
}
