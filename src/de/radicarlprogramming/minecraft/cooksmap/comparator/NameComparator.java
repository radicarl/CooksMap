package de.radicarlprogramming.minecraft.cooksmap.comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class NameComparator extends LandmarkComparator {

	public NameComparator(boolean sortAscending) {
		super(sortAscending);
	}

	@Override
	protected int getCompareValue(Landmark o1, Landmark o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
