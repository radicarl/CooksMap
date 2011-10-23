package de.radicarlprogramming.minecraft.cooksmap.listener;

import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class LandmarkChangedEvent {

	private final Landmark landmark;
	private boolean isCategoryChanged = false;
	private boolean isNameChanged = false;
	private boolean isVisibilityChanged = false;

	public Landmark getLandmark() {
		return this.landmark;
	}

	public boolean isCategoryChanged() {
		return this.isCategoryChanged;
	}

	public boolean isNameChanged() {
		return this.isNameChanged;
	}

	public boolean isVisibilityChanged() {
		return this.isVisibilityChanged;
	}

	public LandmarkChangedEvent(Landmark landmark) {
		this.landmark = landmark;
	}

	public void markCategoryAsChanged() {
		this.isCategoryChanged = true;
	}

	public void markNameAsChanged() {
		this.isNameChanged = true;
	}

	public void markVisibilityAsChanged() {
		this.isVisibilityChanged = true;
	}

	public boolean isLandmarkChanged() {
		return this.isCategoryChanged() || this.isNameChanged() || this.isVisibilityChanged();
	}

}
