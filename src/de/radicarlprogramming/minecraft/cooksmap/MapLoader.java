package de.radicarlprogramming.minecraft.cooksmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

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
			if (data.length > 3) {
				try {
					int id = Integer.parseInt(data[0]);
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);
					int z = Integer.parseInt(data[3]);
					map.addLandmark(id, new Landmark(x, y, z));
				} catch (NumberFormatException e) {
					this.log.warning("Row " + i + " in " + this.filename + " is invalid. Skipped.");
				}
			} else {
				this.log.warning("Row " + i + " in " + this.filename + " is incomplete. Skipped.");
			}
		}
	}
}
