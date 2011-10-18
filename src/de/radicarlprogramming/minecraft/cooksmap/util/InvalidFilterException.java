package de.radicarlprogramming.minecraft.cooksmap.util;

public class InvalidFilterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5261513587842664747L;

	public InvalidFilterException(String filterArg, Exception e) {
		super("Invalid Filter: " + filterArg + " Reason: " + e.getMessage());
	}

	public InvalidFilterException(String filterArg) {
		super("Invalid Filter: " + filterArg);
	}
}
