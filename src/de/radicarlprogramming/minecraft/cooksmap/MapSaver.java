package de.radicarlprogramming.minecraft.cooksmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MapSaver extends PrintWriter {

	public MapSaver(File file) throws IOException {
		super(new BufferedWriter(new FileWriter(file)));
	}

	public void safe(Map map) {
		for (Integer id : map.getLandmarks().keySet()) {
			Landmark landmark = map.getLandmarks().get(id);
			this.print(String.valueOf(id));
			this.print(";");
			this.print(String.valueOf(landmark.getX()));
			this.print(";");
			this.print(String.valueOf(landmark.getY()));
			this.print(";");
			this.print(String.valueOf(landmark.getZ()));
			this.println(";");
		}
		this.close();
	}
}
