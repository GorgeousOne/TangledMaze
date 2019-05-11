package me.gorgeousone.tangledmaze.data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.gorgeousone.tangledmaze.util.TextMessage;

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
			COMMAND_UNBUILD,
			TOOL_RECT,
			TOOL_CIRCLE,
			TOOL_BRUSH,
			TOOL_EXIT,
			MESSAGE_PLUGIN_RELOADED,
			MESSAGE_TOOL_SWITCHED,
			MESSAGE_TOOL_FOR_MAZE_ONLY,
			MESSAGE_PATHWIDTH_CHANGED,
			MESSAGE_WALLWIDTH_CHANGED,
			MESSAGE_WALLHEIGHT_CHANGED,
			MESSAGE_MAZE_BUILDING_STARTED,
			MESSAGE_MAZE_UNBUILDING_STARTED,
			MESSAGE_NO_MAZE_TO_UNBUILD,
			ERROR_NO_BUILD_PERMISSION,
			ERROR_CLIPBOARD_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_FINISHED,
			ERROR_MAZE_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_TOUCHING_MAZE,
			ERROR_NO_MAZE_BORDER_CLICKED,
			ERROR_NO_MAZE_EXIT_SET,
			ERROR_NO_BUILD_BLOCKS_SPECIFIED,
			ERROR_NO_MATCHING_BLOCK_TYPE,
			ERROR_INVALID_NUMBER;

	public static void loadMessages(FileConfiguration langConfig) {
		
		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");
		
		//TODO I will come back in 3 years and laugh about how silly I implemented this here
		COMMAND_WAND       = new TextMessage(ChatColor.DARK_GREEN + "/maze wand\n" + ChatColor.GREEN + helpPages.getString("wand-command") , false);
		COMMAND_START      = new TextMessage(ChatColor.DARK_GREEN + "/maze start\n" + ChatColor.GREEN + helpPages.getString("start-command"), true);
		COMMAND_DISCARD    = new TextMessage(ChatColor.DARK_GREEN + "/maze discard\n"+ ChatColor.GREEN + helpPages.getString("discard-command"), true);
		COMMAND_SELECT     = new TextMessage(ChatColor.DARK_GREEN + "/maze select <tool>\n" + ChatColor.GREEN + helpPages.getString("select-command"), true);
		COMMAND_ADD_CUT    = new TextMessage(ChatColor.DARK_GREEN + "/maze add / cut\n" + ChatColor.GREEN + helpPages.getString("add-cut-command"), true);
		COMMAND_UNDO       = new TextMessage(ChatColor.DARK_GREEN + "/maze undo\n" + ChatColor.GREEN + helpPages.getString("undo-command"), true);
		COMMAND_DIMENSIONS = new TextMessage(ChatColor.DARK_GREEN + "/maze pathwidth / wallwidth / wallheight <integer>\n" + ChatColor.GREEN + helpPages.getString("pathwidth-wallwidth-wallheight-command"), true);
		COMMAND_BUILD      = new TextMessage(ChatColor.DARK_GREEN + "/maze build <block> ...\n" + ChatColor.GREEN + helpPages.getString("build-command"), true);
		COMMAND_TELEPORT   = new TextMessage(ChatColor.DARK_GREEN + "/maze teleport\n" + ChatColor.GREEN + helpPages.getString("teleport-command"), true);
		COMMAND_UNBUILD    = new TextMessage(ChatColor.DARK_GREEN + "/maze unbuild\n" + ChatColor.GREEN + helpPages.getString("unbuild-command"), true);

		ConfigurationSection tools = helpPages.getConfigurationSection("tools");

		TOOL_RECT   = new TextMessage(ChatColor.DARK_GREEN + "rectangle\n" + ChatColor.GREEN + tools.getString("rectangle"), false);
		TOOL_CIRCLE = new TextMessage(ChatColor.DARK_GREEN + "circle\n" + ChatColor.GREEN + tools.getString("circle"), false);
		TOOL_BRUSH  = new TextMessage(ChatColor.DARK_GREEN + "brush\n" + ChatColor.GREEN + tools.getString("brush"), false);
		TOOL_EXIT   = new TextMessage(ChatColor.DARK_GREEN + "exit\n" + ChatColor.GREEN + tools.getString("exit"), false);
		
		ConfigurationSection messages = langConfig.getConfigurationSection("messages");
		
		MESSAGE_PLUGIN_RELOADED = new TextMessage(Constants.prefix + messages.getString("plugin-reloaded"), false);
		MESSAGE_TOOL_SWITCHED = new TextMessage(Constants.prefix + messages.getString("tool-switched"), false);
		MESSAGE_TOOL_FOR_MAZE_ONLY = new TextMessage(Constants.prefix + messages.getString("tool-for-floor-plan-only"), false);
		MESSAGE_PATHWIDTH_CHANGED = new TextMessage(Constants.prefix + messages.getString("maze-pathwidth-changed"), false);
		MESSAGE_WALLWIDTH_CHANGED = new TextMessage(Constants.prefix + messages.getString("maze-wallwidth-changed"), false);
		MESSAGE_WALLHEIGHT_CHANGED = new TextMessage(Constants.prefix + messages.getString("maze-wallheight-changed"), false);
		MESSAGE_MAZE_BUILDING_STARTED = new TextMessage(Constants.prefix + messages.getString("maze-building-started"), false);
		MESSAGE_MAZE_UNBUILDING_STARTED = new TextMessage(Constants.prefix + messages.getString("maze-unbuilding-started"), false);
		MESSAGE_NO_MAZE_TO_UNBUILD = new TextMessage(Constants.prefix + messages.getString("no-maze-to-unbuild"), false);
	
		ConfigurationSection errors = langConfig.getConfigurationSection("errors");
		
		ERROR_NO_BUILD_PERMISSION = new TextMessage(ChatColor.RED + errors.getString("insufficient-permission"), false);
		ERROR_CLIPBOARD_NOT_STARTED = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-started"), false);
		ERROR_CLIPBOARD_NOT_FINISHED = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-finished"), false);
		ERROR_MAZE_NOT_STARTED = new TextMessage(ChatColor.RED + errors.getString("maze-not-started"), false);
		ERROR_CLIPBOARD_NOT_TOUCHING_MAZE = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-touching-maze"), false);
		ERROR_NO_MAZE_BORDER_CLICKED = new TextMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + errors.getString("no-maze-border-clicked"), false);
		ERROR_NO_MAZE_EXIT_SET = new TextMessage(ChatColor.RED + errors.getString("no-maze-exit-set"), false);
		ERROR_NO_BUILD_BLOCKS_SPECIFIED = new TextMessage(ChatColor.RED + errors.getString("no-build-blocks-specified"), false);
		ERROR_NO_MATCHING_BLOCK_TYPE = new TextMessage(ChatColor.RED + errors.getString("argument-not-matching-block"), false);
		ERROR_INVALID_NUMBER = new TextMessage(ChatColor.RED + errors.getString("invalid-number"), false);
	}
	
	public static void reloadMessages(FileConfiguration langConfig) {
		
		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");

		COMMAND_WAND.setText(ChatColor.GREEN + helpPages.getString("wand-command") , false);
		COMMAND_START.setText(ChatColor.GREEN + helpPages.getString("start-command"), true);
		COMMAND_DISCARD.setText(ChatColor.GREEN + helpPages.getString("discard-command"), true);
		COMMAND_SELECT.setText(ChatColor.GREEN + helpPages.getString("select-command"), true);
		COMMAND_ADD_CUT.setText(ChatColor.GREEN + helpPages.getString("add-cut-command"), true);
		COMMAND_UNDO.setText(ChatColor.GREEN + helpPages.getString("undo-command"), true);
		COMMAND_DIMENSIONS.setText(ChatColor.GREEN + helpPages.getString("pathwidth-wallwidth-wallheight-command"), true);
		COMMAND_BUILD.setText(ChatColor.GREEN + helpPages.getString("build-command"), true);
		COMMAND_TELEPORT.setText(ChatColor.GREEN + helpPages.getString("teleport-command"), true);

		ConfigurationSection tools = helpPages.getConfigurationSection("tools");

		TOOL_RECT.setText(ChatColor.GREEN + tools.getString("rectangle"), false);
		TOOL_CIRCLE.setText(ChatColor.GREEN + tools.getString("circle"), false);
		TOOL_BRUSH.setText(ChatColor.GREEN + tools.getString("brush"), false);
		TOOL_EXIT.setText(ChatColor.GREEN + tools.getString("exit"), false);
		
		ConfigurationSection messages = langConfig.getConfigurationSection("messages");
		
		MESSAGE_PLUGIN_RELOADED.setText(Constants.prefix + messages.getString("plugin-reloaded"), false);
		MESSAGE_TOOL_SWITCHED.setText(Constants.prefix + messages.getString("tool-switched"), false);
		MESSAGE_TOOL_FOR_MAZE_ONLY.setText(Constants.prefix + messages.getString("tool-for-floor-plan-only"), false);
		MESSAGE_PATHWIDTH_CHANGED.setText(Constants.prefix + messages.getString("maze-pathwidth-changed"), false);
		MESSAGE_WALLWIDTH_CHANGED.setText(Constants.prefix + messages.getString("maze-wallwidth-changed"), false);
		MESSAGE_WALLHEIGHT_CHANGED.setText(Constants.prefix + messages.getString("maze-wallheight-changed"), false);
		MESSAGE_MAZE_BUILDING_STARTED.setText(Constants.prefix + messages.getString("maze-building-started"), false);
	
		ConfigurationSection errors = langConfig.getConfigurationSection("errors");
		
		ERROR_NO_BUILD_PERMISSION.setText(ChatColor.RED + errors.getString("insufficient-permission"), false);
		ERROR_CLIPBOARD_NOT_STARTED.setText(ChatColor.RED + errors.getString("clipboard-not-started"), false);
		ERROR_CLIPBOARD_NOT_FINISHED.setText(ChatColor.RED + errors.getString("clipboard-not-finished"), false);
		ERROR_MAZE_NOT_STARTED.setText(ChatColor.RED + errors.getString("maze-not-started"), false);
		ERROR_CLIPBOARD_NOT_TOUCHING_MAZE.setText(ChatColor.RED + errors.getString("no-maze-border-clicke"), false);
		ERROR_NO_MAZE_EXIT_SET.setText(ChatColor.RED + errors.getString("no-maze-exit-set"), false);
		ERROR_NO_BUILD_BLOCKS_SPECIFIED.setText(ChatColor.RED + errors.getString("no-build-blocks-specified"), false);
		ERROR_NO_MATCHING_BLOCK_TYPE.setText(ChatColor.RED + errors.getString("argument-not-matching-block"), false);
		ERROR_INVALID_NUMBER.setText(ChatColor.RED + errors.getString("number-not-valid"), false);
		MESSAGE_NO_MAZE_TO_UNBUILD.setText(ChatColor.RED + errors.getString("maze-not-built"), false);
	}
}
