package de.radicarlprogramming.minecraft.cooksmap.comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class DescriptionComparator extends LandmarkComparator {

	public DescriptionComparator(boolean sortAscending) {
		super(sortAscending);
	}

	@Override
	protected int getCompareValue(Landmark o1, Landmark o2) {
		return o1.getDescription().compareTo(o2.getDescription());
	}

}
