package de.radicarlprogramming.minecraft.cooksmap.comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class CategoryComparator extends LandmarkComparator {

	public CategoryComparator(boolean sortAscending) {
		super(sortAscending);
	}

	@Override
	protected int getCompareValue(Landmark o1, Landmark o2) {
		return o1.getCategory().compareTo(o2.getCategory());
	}
}
