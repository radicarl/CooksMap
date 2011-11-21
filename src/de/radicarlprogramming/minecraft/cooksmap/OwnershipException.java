package de.radicarlprogramming.minecraft.cooksmap;

public class OwnershipException extends Exception {

	private static final long serialVersionUID = 1L;

	public OwnershipException(Integer id, String playerName) {
		super("Landmark with id " + id + " belongs to " + playerName + " and not to you");
	}

}
