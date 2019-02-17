package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

	public static TextMessage
			WAND_COMMAND,
			START_COMMAND,
			DISCARD_COMMAND,
			SELECT_COMMAND,
			ADD_CUT_COMMAND,
			UNDO_COMMAND,
			DIMENSIONS_COMMAND,
			BUILD_COMMAND,
			TP_COMMAND,
			RECTANGLE_TOOL,
			CIRCLE_TOOL,
			BRUSH_TOOL,
			EXIT_TOOL;
	
	public static String
			NO_BUILD_PERMISSION,
			CLIPBOARD_NOT_STARTED,
			CLIPBOARD_NOT_FINISHED,
			MAZE_NOT_STARTED,
			NO_MAZE_FILLING_LEFT,
			NO_MAZE_EXIT_SET,
			NO_BUILD_BLOCKS_GIVEN,
			NO_MATCHING_BLOCK_TYPE,
			BUILD_BLOCK_NOT_STABLE;

	public static void loadLanguage(FileConfiguration langConfig) {

		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");

		WAND_COMMAND       = new TextMessage(helpPages.getString("wand-command"), ChatColor.GREEN);
		START_COMMAND      = new TextMessage(helpPages.getString("start-command"), ChatColor.GREEN);
		DISCARD_COMMAND    = new TextMessage(helpPages.getString("discard-command"), ChatColor.GREEN);
		SELECT_COMMAND     = new TextMessage(helpPages.getString("select-command"), ChatColor.GREEN);
		ADD_CUT_COMMAND    = new TextMessage(helpPages.getString("add-cut-command"), ChatColor.GREEN);
		UNDO_COMMAND       = new TextMessage(helpPages.getString("undo-command"), ChatColor.GREEN);
		DIMENSIONS_COMMAND = new TextMessage(helpPages.getString("pathwidth-wallwidth-wallheight-command"), ChatColor.GREEN);
		BUILD_COMMAND      = new TextMessage(helpPages.getString("build-command"), ChatColor.GREEN);
		TP_COMMAND         = new TextMessage(helpPages.getString("teleport-command"), ChatColor.GREEN);

		ConfigurationSection tools = helpPages.getConfigurationSection("tools");

		RECTANGLE_TOOL = new TextMessage(tools.getString("rectangle"), ChatColor.GREEN);
		CIRCLE_TOOL    = new TextMessage(tools.getString("circle"), ChatColor.GREEN);
		BRUSH_TOOL     = new TextMessage(tools.getString("brush"), ChatColor.GREEN);
		EXIT_TOOL      = new TextMessage(tools.getString("exit"), ChatColor.GREEN);

		ConfigurationSection errors = langConfig.getConfigurationSection("error-messages");
	}
}
