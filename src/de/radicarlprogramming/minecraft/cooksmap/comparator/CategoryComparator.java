package de.radicarlprogramming.minecraft.cooksmap.comparator;

import java.util.Comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class CategoryComparator implements Comparator<Landmark> {

	@Override
	public int compare(Landmark o1, Landmark o2) {
		return o1.getCategory().compareTo(o2.getCategory());
	}

}
