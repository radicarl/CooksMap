package de.radicarlprogramming.minecraft.cooksmap.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.Distance;
import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class LandmarkTable {
	private int pages = 1;
	private int maxLengthId = 0;
	private int maxLengthX = 0;
	private int maxLengthY = 0;
	private int maxLengthZ = 0;
	private int maxLengthDistance = 0;
	private int maxLengthLevelDifference;

	private final List<LandmarkTabletRow> rows = new ArrayList<LandmarkTabletRow>();
	public final Player player;

	public LandmarkTable(int pages, Player player) {
		this.pages = pages;
		this.player = player;
	}

	public void addLandmarkToList(Landmark landmark) {
		this.rows.add(new LandmarkTabletRow(landmark));
	}

	public void getPrintString(Player player, int page) {
		String coords = " [X,Y,Z]->distance";
		// +6 cause of [,,]->
		int lengthCoords = Math.max(coords.length(), this.maxLengthX + this.maxLengthY + this.maxLengthZ + 6);
		player.sendMessage(LandmarkTable.padRight(" id", this.maxLengthId) + LandmarkTable.padRight(coords, lengthCoords)
				+ " Category   Description         (Page " + page + "/" + this.pages + ")");
		for (LandmarkTabletRow row : this.rows) {
			player.sendMessage(row.getPrintString());
		}
	}

	private static String padRight(String string, int length) {
		return LandmarkTable.pad(string, length, false);
	}

	private static String padLeft(String string, int length) {
		return LandmarkTable.pad(string, length, true);
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

	private class LandmarkTabletRow {
		private final String id;
		private final String x;
		private final String y;
		private final String z;
		private final String distance;
		private final Landmark landmark;
		private final String levelDifference;

		private LandmarkTabletRow(Landmark landmark) {
			this.landmark = landmark;
			this.id = String.valueOf(landmark.getId().intValue());
			this.x = String.valueOf(landmark.getX());
			this.y = String.valueOf(landmark.getY());
			this.z = String.valueOf(landmark.getZ());
			Distance distance = Distance.calculateDistance(LandmarkTable.this.player, landmark);
			this.distance = String.valueOf(distance.getDistance());
			this.levelDifference = String.valueOf(distance.getLevelDifference());

			LandmarkTable.this.maxLengthId = Math.max(LandmarkTable.this.maxLengthId, this.id.length());
			LandmarkTable.this.maxLengthX = Math.max(LandmarkTable.this.maxLengthX, this.x.length());
			LandmarkTable.this.maxLengthY = Math.max(LandmarkTable.this.maxLengthY, this.y.length());
			LandmarkTable.this.maxLengthZ = Math.max(LandmarkTable.this.maxLengthZ, this.z.length());
			LandmarkTable.this.maxLengthZ = Math.max(LandmarkTable.this.maxLengthZ, this.z.length());
			LandmarkTable.this.maxLengthDistance = Math.max(LandmarkTable.this.maxLengthDistance, this.distance.length());
			LandmarkTable.this.maxLengthLevelDifference = Math.max(LandmarkTable.this.maxLengthLevelDifference,
					this.levelDifference.length());
		}

		public String getPrintString() {
			StringBuffer buffer = new StringBuffer((this.landmark.isPrivate()) ? ChatColor.DARK_RED.toString()
					: ChatColor.DARK_GREEN.toString());
			buffer.append(LandmarkTable.padLeft(this.id, LandmarkTable.this.maxLengthId));
			buffer.append(" [");
			buffer.append(LandmarkTable.padLeft(this.x, LandmarkTable.this.maxLengthX));
			buffer.append(",");
			buffer.append(LandmarkTable.padLeft(this.y, LandmarkTable.this.maxLengthY));
			buffer.append(",");
			buffer.append(LandmarkTable.padLeft(this.z, LandmarkTable.this.maxLengthZ));
			buffer.append("]->");
			buffer.append(LandmarkTable.padLeft(this.distance, LandmarkTable.this.maxLengthDistance));
			buffer.append("/");
			buffer.append(LandmarkTable.padLeft(this.levelDifference, LandmarkTable.this.maxLengthLevelDifference));
			buffer.append(" ");
			buffer.append(this.landmark.getCategory());
			buffer.append(" '");
			// TODO: calc max rowlength and shorten description if longer
			buffer.append(this.landmark.getDescription());
			buffer.append("'");
			return buffer.toString();
		}
	}
}
