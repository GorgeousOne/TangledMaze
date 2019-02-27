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
			TOOL_EXIT,
			MESSAGE_MAZE_START,
			MESSAGE_MAZE_DISCARD,
			MESSAGE_MAZE_BUILDING,
			MESSAGE_TOOL_SWITCH,
			MESSAGE_TOOL_FOR_MAZE_ONLY,
			ERROR_NO_BUILD_PERMISSION,
			ERROR_CLIPBOARD_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_FINISHED,
			ERROR_MAZE_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_TOUCHING_MAZE,
			ERROR_NO_MAZE_EXIT_SET,
			ERROR_NO_BUILD_BLOCKS_SPECIFIED,
			ERROR_NO_MATCHING_BLOCK_TYPE,
			ERROR_NUMBER_NOT_VALID;

	public static void loadLanguage(FileConfiguration langConfig) {
		
		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");

		COMMAND_WAND = new TextMessage(ChatColor.GREEN + helpPages.getString("wand-command") , false);
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
		
		ConfigurationSection messages = langConfig.getConfigurationSection("messages");
		
		MESSAGE_MAZE_START = new TextMessage(Constants.prefix + messages.getString("maze-start"), false);
		MESSAGE_MAZE_DISCARD = new TextMessage(Constants.prefix + messages.getString("maze-discard"), false);
		MESSAGE_MAZE_BUILDING = new TextMessage(Constants.prefix + messages.getString("maze-building"), false);
		MESSAGE_TOOL_SWITCH = new TextMessage(Constants.prefix + messages.getString("tool-switch"), false);
		MESSAGE_TOOL_FOR_MAZE_ONLY = new TextMessage(Constants.prefix + messages.getString("tool-for-floor-plan-only"), false);
		
		
		ConfigurationSection errors = langConfig.getConfigurationSection("errors");
		
		ERROR_NO_BUILD_PERMISSION = new TextMessage(ChatColor.RED + errors.getString("insufficient-permission"), false);
		ERROR_CLIPBOARD_NOT_STARTED = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-started"), false);
		ERROR_CLIPBOARD_NOT_FINISHED = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-finished"), false);
		ERROR_MAZE_NOT_STARTED = new TextMessage(ChatColor.RED + errors.getString("maze-not-started"), false);
		ERROR_CLIPBOARD_NOT_TOUCHING_MAZE = new TextMessage(ChatColor.RED + errors.getString("no-maze-border-clicke"), false);
		ERROR_NO_MAZE_EXIT_SET = new TextMessage(ChatColor.RED + errors.getString("no-maze-exit-set"), false);
		ERROR_NO_BUILD_BLOCKS_SPECIFIED = new TextMessage(ChatColor.RED + errors.getString("no-build-blocks-specified"), false);
		ERROR_NO_MATCHING_BLOCK_TYPE = new TextMessage(ChatColor.RED + errors.getString("argument-not-matching-block"), false);
		ERROR_NUMBER_NOT_VALID = new TextMessage(ChatColor.RED + errors.getString("number-not-valid"), false);
	}
}
