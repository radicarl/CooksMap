package de.radicarlprogramming.minecraft.cooksmap.comparator;

import java.util.Comparator;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public abstract class LandmarkComparator implements Comparator<Landmark> {

	private int sortDirection = 1;
	private LandmarkComparator nextComparator = null;

	public LandmarkComparator(boolean sortAscending) {
		this.setIsSortDirectionAscending(sortAscending);
	}

	public void setNextComparator(LandmarkComparator nextComparator) {
		this.nextComparator = nextComparator;
	}

	@Override
	public final int compare(Landmark o1, Landmark o2) {
		int compareResult = this.getCompareValue(o1, o2);
		if (compareResult == 0 && this.nextComparator != null) {
			return this.nextComparator.compare(o1, o2);
		}
		return this.sortDirection * compareResult;
	}

	protected abstract int getCompareValue(Landmark o1, Landmark o2);

	public void setIsSortDirectionAscending(boolean sortAscending) {
		if (!sortAscending) {
			this.sortDirection = -1;
		}

	}

	public static LandmarkComparator createComparator(String arg) {
		boolean sortAscending = arg.startsWith("+");
		String type = arg.substring(1).toLowerCase();
		LandmarkComparator comparator = null;
		if ("category".equals(type)) {
			comparator = new CategoryComparator(sortAscending);
		} else if ("description".equals(type)) {
			comparator = new DescriptionComparator(sortAscending);
		} // TODO implement distance comparator. Calculate the distance in the
			// comparator, or save it in the landmark and use a dirty-bit which
			// is update by onmove events of the player
		else {
			comparator = new IdComparator(sortAscending);
		}
		return comparator;
	}
}
