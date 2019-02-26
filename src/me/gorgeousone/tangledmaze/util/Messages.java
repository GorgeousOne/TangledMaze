package me.gorgeousone.tangledmaze.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

	public static TextMessage
			COMMAND_WAND,
			COMMAND_START,
			COMMAND_DISCARD,
			COMMAND_SELECT,
			COMMAND_ADD_CUT,
			COMMAND_UNDO,
			COMMAND_DIMENSIONS,
			COMMAND_BUILD,
			COMMAND_TELEPORT,
			TOOL_RECT,
			TOOL_CIRCLE,
			TOOL_BRUSH,
			TOOL_EXIT;
	
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

		COMMAND_WAND = new TextMessage(ChatColor.GREEN + helpPages.getString("wand-command") , true);
		COMMAND_START = new TextMessage(ChatColor.GREEN + helpPages.getString("start-command"), true);
		COMMAND_DISCARD = new TextMessage(ChatColor.GREEN + helpPages.getString("discard-command"), true);
		COMMAND_SELECT = new TextMessage(ChatColor.GREEN + helpPages.getString("select-command"), true);
		COMMAND_ADD_CUT = new TextMessage(ChatColor.GREEN + helpPages.getString("add-cut-command"), true);
		COMMAND_UNDO = new TextMessage(ChatColor.GREEN + helpPages.getString("undo-command"), true);
		COMMAND_DIMENSIONS = new TextMessage(ChatColor.GREEN + helpPages.getString("pathwidth-wallwidth-wallheight-command"), true);
		COMMAND_BUILD = new TextMessage(ChatColor.GREEN + helpPages.getString("build-command"), true);
		COMMAND_TELEPORT = new TextMessage(ChatColor.GREEN + helpPages.getString("teleport-command"), true);

		ConfigurationSection tools = helpPages.getConfigurationSection("tools");

		TOOL_RECT = new TextMessage(ChatColor.GREEN + tools.getString("rectangle"), false);
		TOOL_CIRCLE = new TextMessage(ChatColor.GREEN + tools.getString("circle"), false);
		TOOL_BRUSH = new TextMessage(ChatColor.GREEN + tools.getString("brush"), false);
		TOOL_EXIT = new TextMessage(ChatColor.GREEN + tools.getString("exit"), false);

		ConfigurationSection errors = langConfig.getConfigurationSection("error-messages");
	}
}
