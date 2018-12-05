package me.gorgeousone.tangledmaze.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Constants {
	
	public static final String
			insufficientPerms = ChatColor.RED + "You do not have the Permission for this command.",
			buildPerm  = "tm.build",
			wandPerm   = "tm.build.getwand",
			mazeTpPerm = "tm.mazetp";
	
	public static final String prefix =
			ChatColor.DARK_GREEN + "["  +
			ChatColor.GREEN      + "TM" + 
			ChatColor.DARK_GREEN + "] " +
			ChatColor.YELLOW;
	
	public static final Material
			MAZE_BORDER      = Material.REDSTONE_BLOCK,
			MAZE_EXIT        = Material.EMERALD_BLOCK,
			MAZE_MAIN_EXIT   = Material.DIAMOND_BLOCK,
			SELECTION_CORNER = Material.LAPIS_BLOCK,
			SELECTION_BORDER = Material.GOLD_BLOCK;
	
	public static final int
		MAX_PATH_WIDTH = 50,
		MAX_WALL_WIDTH = 50,
		MAX_WALL_HEIGHT = 100;
}