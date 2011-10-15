package de.radicarlprogramming.minecraft.cooksmap.comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class IdComparator extends LandmarkComparator {

	public IdComparator(boolean sortAscending) {
		super(sortAscending);
	}

	@Override
	protected int getCompareValue(Landmark o1, Landmark o2) {
		return o1.getId() - o2.getId();
	}

}
