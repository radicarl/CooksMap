package de.radicarlprogramming.minecraft.cooksmap.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;
import de.radicarlprogramming.minecraft.cooksmap.Map;

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
			this.print(";");
			this.print(landmark.getCategory());
			this.print(";");
			this.print(landmark.getDescription());
			this.print(";");
			this.print(landmark.getPlayerName());
			this.print(";");
			this.print(landmark.isPrivate());
			this.println(";");
		}
		this.close();
	}
}
