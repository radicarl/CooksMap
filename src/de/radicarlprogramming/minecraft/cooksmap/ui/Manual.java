package de.radicarlprogramming.minecraft.cooksmap.ui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Manual {

	public static boolean printHelp(Player player, String command) {
		final String commandLowerCase = command.toLowerCase();
		if ("list".equals(commandLowerCase)) {
			Manual.printHelp4List(player);
		} else if ("n".equals(commandLowerCase)) {
			Manual.printHelp4Next(player);
		} else if ("p".equals(commandLowerCase)) {
			Manual.printHelp4Previous(player);
		} else if ("goto".equals(commandLowerCase)) {
			Manual.printHelp4Goto(player);
		} else if ("add".equals(commandLowerCase)) {
			Manual.printHelp4Add(player);
		} else if ("set".equals(commandLowerCase)) {
			Manual.printHelp4Set(player);
		} else if ("dist".equals(commandLowerCase)) {
			Manual.printHelp4Dist(player);
		} else if ("del".equals(commandLowerCase)) {
			Manual.printHelp4Del(player);
		} else if ("edit".equals(commandLowerCase)) {
			Manual.printHelp4Edit(player);
		} else if ("help".equals(commandLowerCase)) {
			Manual.printHelp4Help(player);
		} else {
			player.sendMessage("Help: unknown command:" + command);
		}
		return true;
	}

	private static void printHelp4List(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "list " + ChatColor.LIGHT_PURPLE
				+ "(([<>][idcn]) | ([idcn][<>=!~].+))* <page>?");
		player.sendMessage("Search and order Landmarks.");
		player.sendMessage("Order: " + ChatColor.LIGHT_PURPLE + "[<>][idcn]" + ChatColor.WHITE
				+ " = [Order][column] (i=id,d=distance,..)");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "list >d <i" + ChatColor.WHITE
				+ " sort first by distance, then by id (reversed)");
		player.sendMessage("Filter: " + ChatColor.LIGHT_PURPLE + "[idcn][<>=!~].+) [column][comparator] expression");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "list c!foo n~bar" + ChatColor.WHITE
				+ " search for Landmarks with category unequal foo and name containing bar");
		// player.sendMessage("Only landmarks that match all filters will be displayed");
		player.sendMessage("Page: list page " + ChatColor.LIGHT_PURPLE + "<page>" + ChatColor.WHITE
				+ " of search result.");
		player.sendMessage("Use commands p and n to navigate through last search");
		player.sendMessage("Combine order, filter and page (page must be last)");
	}

	private static void printHelp4Next(Player player) {
		player.sendMessage("Help: Displays the next page of the last search");
	}

	private static void printHelp4Previous(Player player) {
		player.sendMessage("Help: Displays the previous page of the last search");
	}

	private static void printHelp4Goto(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "goto " + ChatColor.LIGHT_PURPLE + "<page>");
		player.sendMessage("Show page <page> of your last search");
		player.sendMessage("e.g.: goto " + ChatColor.GREEN + " 3");
	}

	private static void printHelp4Add(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "add " + ChatColor.LIGHT_PURPLE + "(+|-)? <category> <name>");
		player.sendMessage("add new landmark at your current position");
		player.sendMessage("first word is used as category, all other for the name");
		player.sendMessage("Use + if all players may see this landmark, otherwise -");
		player.sendMessage("default is private (-)");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "add + publicPlace everyone can come here");
		player.sendMessage("-> creates a public landmark in category publicPlace");
		player.sendMessage("with the name \"everyone can come here\"");
	}

	private static void printHelp4Set(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "set " + ChatColor.LIGHT_PURPLE + "<id>");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "set 42");
		player.sendMessage("Sets your compass to the landmark with id 42");
	}

	private static void printHelp4Dist(Player player) {
		player.sendMessage("Displays the distance in blocks to your current compass target");
		player.sendMessage("First number shows distance in plane, Second in height");
		player.sendMessage("If second number is less than zero->dig");
		player.sendMessage("If second number is greater than zero->jump, jump");
	}

	private static void printHelp4Del(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "del " + ChatColor.LIGHT_PURPLE + "<id>");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "del 42");
		player.sendMessage("Deletes the landmark with id 42");
		player.sendMessage("You can not delete white Landmarks, because you dont own them");

	}

	private static void printHelp4Edit(Player player) {
		player.sendMessage("Help: " + ChatColor.GREEN + "edit " + ChatColor.LIGHT_PURPLE
				+ "<id> (v=(+|-)|c=<category>|n=<name>)+");
		player.sendMessage("Change visibility, category and/or name of the landmark");
		player.sendMessage("e.g.: " + ChatColor.GREEN + "edit 42 v=+ c=home");
		player.sendMessage("Change visibility to public and category to home, name will not be changed.");
		player.sendMessage("You can not change white Landmarks, because you dont own them");
	}

	private static void printHelp4Help(Player player) {
		player.sendMessage("I think you know how to use the help ^^");
	}

}
