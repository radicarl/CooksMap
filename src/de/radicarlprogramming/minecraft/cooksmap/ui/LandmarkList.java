package de.radicarlprogramming.minecraft.cooksmap.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.CooksMapPlugin;
import de.radicarlprogramming.minecraft.cooksmap.Landmark;
import de.radicarlprogramming.minecraft.cooksmap.Position;

public class LandmarkList {
	private int pages = 1;
	private int maxLengthId = 0;
	private int maxLengthX = 0;
	private int maxLengthY = 0;
	private int maxLengthZ = 0;
	private int maxLengthDistance = 0;

	private final List<LandmarkListRow> rows = new ArrayList<LandmarkListRow>();
	public final Player player;

	public LandmarkList(int pages, Player player) {
		this.pages = pages;
		this.player = player;
	}

	public void addLandmarkToList(Landmark landmark) {
		this.rows.add(new LandmarkListRow(landmark));
	}

	public void getPrintString(Player player, int page) {
		// +4 cause of [,,]
		String coords = " coords->distance";
		// TODO add padding for distance
		int lengthCoords = Math.max(coords.length(), this.maxLengthX + this.maxLengthY + this.maxLengthZ + 4);
		player.sendMessage(LandmarkList.padRight(" id", this.maxLengthId) + LandmarkList.padRight(coords, lengthCoords)
				+ " Type   Name   Page " + page + "/" + this.pages);
		for (LandmarkListRow row : this.rows) {
			player.sendMessage(row.getPrintString());
		}
	}

	private static String padRight(String string, int length) {
		return LandmarkList.pad(string, length, false);
	}

	private static String padLeft(String string, int length) {
		return LandmarkList.pad(string, length, true);
	}

	private static String pad(String string, int length, boolean left) {
		int difference = (length - string.length()) * 3;
		if (difference <= 0) {
			return string;
		}
		char[] padding = new char[difference];
		for (int i = 0; i < padding.length; i++) {
			padding[i] = ' ';
		}
		if (left) {
			return String.valueOf(padding) + string;
		}

		return string + String.valueOf(padding);
	}

	private class LandmarkListRow {
		private final String id;
		private final String x;
		private final String y;
		private final String z;
		private final String distance;
		private final Landmark landmark;

		private LandmarkListRow(Landmark landmark) {
			this.landmark = landmark;
			this.id = String.valueOf(landmark.getId());
			this.x = String.valueOf(landmark.getX());
			this.y = String.valueOf(landmark.getY());
			this.z = String.valueOf(landmark.getZ());
			Location location = LandmarkList.this.player.getLocation();
			Position position = this.landmark.getPosition();
			this.distance = String.valueOf(CooksMapPlugin.getDistance(location, position));

			LandmarkList.this.maxLengthId = Math.max(LandmarkList.this.maxLengthId, this.id.length());
			LandmarkList.this.maxLengthX = Math.max(LandmarkList.this.maxLengthX, this.x.length());
			LandmarkList.this.maxLengthY = Math.max(LandmarkList.this.maxLengthY, this.y.length());
			LandmarkList.this.maxLengthZ = Math.max(LandmarkList.this.maxLengthZ, this.z.length());
			LandmarkList.this.maxLengthZ = Math.max(LandmarkList.this.maxLengthZ, this.z.length());
			LandmarkList.this.maxLengthDistance = Math.max(LandmarkList.this.maxLengthDistance, this.distance.length());
		}

		public String getPrintString() {
			StringBuffer buffer = new StringBuffer((this.landmark.isPrivate()) ? ChatColor.DARK_RED.toString()
					: ChatColor.DARK_GREEN.toString());
			buffer.append(LandmarkList.padLeft(this.id, LandmarkList.this.maxLengthId));
			buffer.append(" [");
			buffer.append(LandmarkList.padLeft(this.x, LandmarkList.this.maxLengthX));
			buffer.append(",");
			buffer.append(LandmarkList.padLeft(this.y, LandmarkList.this.maxLengthY));
			buffer.append(",");
			buffer.append(LandmarkList.padLeft(this.z, LandmarkList.this.maxLengthZ));
			buffer.append("]->");
			// TODO add padding for distance
			buffer.append(this.distance);
			buffer.append(" ");
			buffer.append(this.landmark.getType());
			buffer.append(" '");
			buffer.append(this.landmark.getDescription());
			buffer.append("'");
			return buffer.toString();
		}
	}
}
