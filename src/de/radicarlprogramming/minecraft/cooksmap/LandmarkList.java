package de.radicarlprogramming.minecraft.cooksmap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class LandmarkList {
	public static int DISPLAY_HEIGHT = 10;

	private int maxLengthId = 0;
	private int maxLengthX = 0;
	private int maxLengthY = 0;
	private int maxLengthZ = 0;

	private final List<LandmarkListRow> rows = new ArrayList<LandmarkListRow>();

	public boolean addLandmarkToList(int id, Landmark landmark) {
		if (this.rows.size() >= LandmarkList.DISPLAY_HEIGHT - 1) {
			return false;
		}
		this.rows.add(new LandmarkListRow(id, landmark));
		return true;
	}

	public void getPrintString(Player player) {
		// +4 cause of [,,]
		String coords = " coords";
		int lengthCoords = Math.max(coords.length(), this.maxLengthX + this.maxLengthY + this.maxLengthZ + 4);
		player.sendMessage(LandmarkList.padRight(" id", this.maxLengthId) + LandmarkList.padRight(coords, lengthCoords));
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

		private LandmarkListRow(int id, Landmark landmark) {
			this.id = String.valueOf(id);
			this.x = String.valueOf(landmark.getX());
			this.y = String.valueOf(landmark.getY());
			this.z = String.valueOf(landmark.getZ());
			LandmarkList.this.maxLengthId = Math.max(LandmarkList.this.maxLengthId, this.id.length());
			LandmarkList.this.maxLengthX = Math.max(LandmarkList.this.maxLengthX, this.x.length());
			LandmarkList.this.maxLengthY = Math.max(LandmarkList.this.maxLengthY, this.y.length());
			LandmarkList.this.maxLengthZ = Math.max(LandmarkList.this.maxLengthZ, this.z.length());
		}

		public String getPrintString() {
			return LandmarkList.padLeft(this.id, LandmarkList.this.maxLengthId) + " ["
					+ LandmarkList.padLeft(this.x, LandmarkList.this.maxLengthX) + ","
					+ LandmarkList.padLeft(this.y, LandmarkList.this.maxLengthY) + ","
					+ LandmarkList.padLeft(this.z, LandmarkList.this.maxLengthZ) + "]";
		}
	}
}
