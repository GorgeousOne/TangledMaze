package me.tangledmaze.gorgeousone.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Constants {
	
	public static final String prefix =
		ChatColor.DARK_GREEN + "["  +
		ChatColor.GREEN      + "TM" + 
		ChatColor.DARK_GREEN + "] " +
		ChatColor.YELLOW;
	
	public static final Material
		MAZE_BORDER        = Material.REDSTONE_BLOCK,
		MAZE_EXIT      = Material.EMERALD_BLOCK,
		MAZE_MAIN_EXIT = Material.DIAMOND_BLOCK,
		SELECTION_CORNER   = Material.LAPIS_BLOCK,
		SELECTION_BORDER   = Material.GOLD_BLOCK;
}