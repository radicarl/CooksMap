package de.radicarlprogramming.minecraft.cooksmap;

import java.util.ArrayList;
import java.util.List;

public class CooksMapSession {
	private List<Landmark> landmarkList;
	private int page;

	// TODO: save filter and sorter to update landmarkList if changes are made
	// TODO: has to be MapChangeListener: added Landmarks must be added to
	// landmarkList if they match filter, removed Landmarks must be removed
	// TODO: has to be LandmarkChangeListener: if visibility has been changed,
	// add/remove/do nothing, if changed Landmark matches filter add landmark to
	// list (if not already exists) or remove if it does not match anymore

	public List<Landmark> getLandmarkList() {
		if (this.landmarkList == null) {
			this.landmarkList = new ArrayList<Landmark>();
		}
		return this.landmarkList;
	}

	public void setLandmarkList(List<Landmark> list) {
		this.landmarkList = list;
	}

	public int getPage() {
		if (this.page < 1) {
			this.page = 1;
		}
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
