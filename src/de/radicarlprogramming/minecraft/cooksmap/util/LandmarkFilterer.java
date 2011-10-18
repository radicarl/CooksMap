package de.radicarlprogramming.minecraft.cooksmap.util;

import java.util.ArrayList;
import java.util.List;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class LandmarkFilterer {
	private final List<Filter> filters = new ArrayList<Filter>();

	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}

	public List<Landmark> filter(List<Landmark> list) {
		List<Landmark> filtered = new ArrayList<Landmark>();
		if (this.filters.isEmpty()) {
			filtered.addAll(list);
		} else {
			for (Landmark landmark : list) {
				boolean fullfilled = true;
				for (Filter filter : this.filters) {
					fullfilled = fullfilled && filter.fullfills(landmark);
				}
				if (fullfilled) {
					filtered.add(landmark);
				}
			}
		}
		return filtered;
	}
}
