package de.radicarlprogramming.minecraft.cooksmap.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;
import de.radicarlprogramming.minecraft.cooksmap.Map;

public class MapLoader extends BufferedReader {
	private final String filename;
	private final Logger log = Logger.getLogger("Minecraft");

	public MapLoader(File file) throws FileNotFoundException {
		super(new FileReader(file));
		this.filename = file.getAbsolutePath();
	}

	public void load(Map map) throws IOException {
		for (int i = 0; this.ready(); i++) {
			String line = this.readLine();
			String[] data = line.split(";");
			if (data.length > 7) {
				try {
					int id = Integer.parseInt(data[0]);
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);
					int z = Integer.parseInt(data[3]);
					String type = data[4];
					String name = data[5];
					String playerName = data[6];
					boolean isPublic = Boolean.parseBoolean(data[7]);
					map.addLandmark(new Landmark(x, y, z, id, type, name, playerName, isPublic));
				} catch (Exception e) {
					this.log.warning("Row " + (i + 1) + " in " + this.filename + " is invalid. Row Skipped. Reason: "
							+ e.getMessage());
				}
			} else {
				this.log.warning("Row " + (i + 1) + " in " + this.filename + " is incomplete. Row Skipped.");
			}
		}
	}
}
