package de.radicarlprogramming.minecraft.cooksmap;

import de.radicarlprogramming.minecraft.cooksmap.comparator.IdComparator;
import de.radicarlprogramming.minecraft.cooksmap.comparator.LandmarkComparator;
import de.radicarlprogramming.minecraft.cooksmap.util.LandmarkFilterer;

public class CooksMapSession {
	private int page;
	private LandmarkFilterer filterer;
	private LandmarkComparator comparator;

	public int getPage() {
		if (this.page < 1) {
			this.page = 1;
		}
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void incrementPage() {
		this.page++;
	}

	public void decrementPage() {
		this.page = Math.max(1, this.page - 1);
	}

	public LandmarkFilterer getFilterer() {
		if (this.filterer == null) {
			this.filterer = new LandmarkFilterer();
		}
		return this.filterer;
	}

	public void setFilterer(LandmarkFilterer filterer) {
		this.filterer = filterer;
	}

	public LandmarkComparator getComparator() {
		if (this.comparator == null) {
			this.comparator = new IdComparator(true);
		}
		return this.comparator;
	}

	public void setComparator(LandmarkComparator comparator) {
		this.comparator = comparator;
	}
}
