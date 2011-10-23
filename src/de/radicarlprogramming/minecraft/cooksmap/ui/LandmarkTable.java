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
		StringBuffer buffer = new StringBuffer(ChatColor.YELLOW.toString());
		buffer.append("i");
		buffer.append(ChatColor.WHITE);
		buffer.append("d:");

		buffer.append(ChatColor.YELLOW);
		buffer.append("  d");
		buffer.append(ChatColor.WHITE);
		buffer.append("istance");

		buffer.append(ChatColor.YELLOW);
		buffer.append("  c");
		buffer.append(ChatColor.WHITE);
		buffer.append("ategory   ");

		buffer.append(ChatColor.YELLOW);
		buffer.append("n");
		buffer.append(ChatColor.WHITE);
		buffer.append("ame             (Page " + page + "/" + this.pages + ")");

		player.sendMessage(buffer.toString());
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
		private final String distance;
		private final Landmark landmark;
		private final String levelDifference;

		private LandmarkTabletRow(Landmark landmark) {
			this.landmark = landmark;
			this.id = String.valueOf(landmark.getId().intValue());
			Distance distance = Distance.calculateDistance(LandmarkTable.this.player, landmark);
			this.distance = String.valueOf(distance.getDistance());
			this.levelDifference = String.valueOf(distance.getLevelDifference());

			LandmarkTable.this.maxLengthId = Math.max(LandmarkTable.this.maxLengthId, this.id.length());
			LandmarkTable.this.maxLengthDistance = Math.max(LandmarkTable.this.maxLengthDistance,
					this.distance.length());
			LandmarkTable.this.maxLengthLevelDifference = Math.max(LandmarkTable.this.maxLengthLevelDifference,
					this.levelDifference.length());
		}

		public String getPrintString() {
			StringBuffer buffer = new StringBuffer(this.getColor());
			buffer.append(LandmarkTable.padLeft(this.id, LandmarkTable.this.maxLengthId));
			buffer.append(": ");
			buffer.append(LandmarkTable.padLeft(this.distance, LandmarkTable.this.maxLengthDistance));
			buffer.append("/");
			buffer.append(LandmarkTable.padLeft(this.levelDifference, LandmarkTable.this.maxLengthLevelDifference));
			buffer.append("  ");
			buffer.append(this.landmark.getCategory());
			buffer.append("  '");
			// TODO: calc max rowlength and shorten name if longer (etwa
			// 50 6er Zeichen)
			buffer.append(this.landmark.getName());
			buffer.append("'");
			return buffer.toString();
		}

		private String getColor() {
			if (!this.landmark.getPlayerName().equals(LandmarkTable.this.player.getName())) {
				return ChatColor.WHITE.toString();
			}
			return (this.landmark.isPrivate()) ? ChatColor.DARK_GREEN.toString() : ChatColor.GREEN.toString();
		}
	}
}
