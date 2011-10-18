package de.radicarlprogramming.minecraft.cooksmap.comparator;

import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.Distance;
import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class DistanceComparator extends LandmarkComparator {

	private final Player player;

	public DistanceComparator(Player player, boolean sortAscending) {
		super(sortAscending);
		this.player = player;
	}

	@Override
	protected int getCompareValue(Landmark o1, Landmark o2) {
		Distance distance1 = Distance.calculateDistance(this.player, o1);
		Distance distance2 = Distance.calculateDistance(this.player, o2);
		return distance1.compareTo(distance2);
	}

}
