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
			COMMAND_PATHLENGTH,
			COMMAND_BUILD,
			COMMAND_TELEPORT,
			COMMAND_UNBUILD,
			TOOL_RECT,
			TOOL_CIRCLE,
			TOOL_BRUSH,
			TOOL_EXIT,
			DIMENSION_WALL_HEIGHT,
			DIMENSION_PATH_WIDTH,
			DIMENSION_WALL_WIDTH,
			DIMENSION_ROOF_WIDTH,
			DIMENSION_PATH_LENGTH,
			MESSAGE_PLUGIN_RELOADED,
			MESSAGE_TOOL_SWITCHED,
			MESSAGE_TOOL_FOR_MAZE_ONLY,
			MESSAGE_DIMENSION_CHANGED,
			MESSAGE_MAZE_BUILDING_COMPLETED,
			MESSAGE_MAZE_UNBUILDING_STARTED,
			MESSAGE_NO_MAZE_TO_UNBUILD,
			ERROR_NO_BUILD_PERMISSION,
			ERROR_CLIPBOARD_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_COMPLETED,
			ERROR_MAZE_NOT_STARTED,
			ERROR_CLIPBOARD_NOT_TOUCHING_MAZE,
			ERROR_NO_MAZE_EXIT_SET,
			ERROR_MAZE_ALREADY_BUILT,
			ERROR_MAZE_NOT_BUILT,
			ERROR_INVALID_NUMBER,
			ERROR_INVALID_DIMENSION,
			ERROR_INVALID_MAZE_PART,
			ERROR_INVALID_BLOCK_NAME,
			ERROR_INVALID_BLOCK_PROPERTY,
			ERROR_INVALID_BLOCK_PROPERTY_VALUE,
			ERROR_MISSING_BLOCK_PROPERTY_VALUE,
			ERROR_SAVE_FILE_CORRUPTED;

	public static void loadMessages(FileConfiguration langConfig) {
		
		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");
		
		COMMAND_WAND       = new TextMessage(ChatColor.DARK_GREEN + "/maze wand\n" + ChatColor.GREEN + helpPages.getString("wand-command") , false);
		COMMAND_START      = new TextMessage(ChatColor.DARK_GREEN + "/maze start\n" + ChatColor.GREEN + helpPages.getString("start-command"), true);
		COMMAND_DISCARD    = new TextMessage(ChatColor.DARK_GREEN + "/maze discard\n"+ ChatColor.GREEN + helpPages.getString("discard-command"), true);
		COMMAND_SELECT     = new TextMessage(ChatColor.DARK_GREEN + "/maze select <tool>\n" + ChatColor.GREEN + helpPages.getString("select-command"), true);
		COMMAND_ADD_CUT    = new TextMessage(ChatColor.DARK_GREEN + "/maze add / cut\n" + ChatColor.GREEN + helpPages.getString("add-cut-command"), true);
		COMMAND_UNDO       = new TextMessage(ChatColor.DARK_GREEN + "/maze undo\n" + ChatColor.GREEN + helpPages.getString("undo-command"), true);
		COMMAND_DIMENSIONS = new TextMessage(ChatColor.DARK_GREEN + "/maze set <dimension> <integer>\n" + ChatColor.GREEN + helpPages.getString("set-dimension-command"), true);
		COMMAND_PATHLENGTH = new TextMessage(ChatColor.DARK_GREEN + "/maze pathlength <integer>\n" + ChatColor.GREEN + helpPages.getString("pathlength-command"), true);
		COMMAND_BUILD      = new TextMessage(ChatColor.DARK_GREEN + "/maze build <block> ...\n" + ChatColor.GREEN + helpPages.getString("build-command"), true);
		COMMAND_TELEPORT   = new TextMessage(ChatColor.DARK_GREEN + "/maze teleport\n" + ChatColor.GREEN + helpPages.getString("teleport-command"), true);
		COMMAND_BUILD      = new TextMessage(ChatColor.DARK_GREEN + "/maze unbuild\n" + ChatColor.GREEN + helpPages.getString("unbuild-command"), true);

		ConfigurationSection tools = helpPages.getConfigurationSection("tools");

		TOOL_RECT   = new TextMessage(ChatColor.DARK_GREEN + "rectangle\n" + ChatColor.GREEN + tools.getString("rectangle"), false);
		TOOL_CIRCLE = new TextMessage(ChatColor.DARK_GREEN + "circle\n" + ChatColor.GREEN + tools.getString("circle"), false);
		TOOL_BRUSH  = new TextMessage(ChatColor.DARK_GREEN + "brush\n" + ChatColor.GREEN + tools.getString("brush"), false);
		TOOL_EXIT   = new TextMessage(ChatColor.DARK_GREEN + "exit\n" + ChatColor.GREEN + tools.getString("exit"), false);

		ConfigurationSection dimensions = helpPages.getConfigurationSection("dimensions");

		DIMENSION_WALL_HEIGHT = new TextMessage(ChatColor.DARK_GREEN + "wall height\n" + ChatColor.GREEN + dimensions.getString("wall-height"), false);
		DIMENSION_PATH_WIDTH  = new TextMessage(ChatColor.DARK_GREEN + "path width\n" + ChatColor.GREEN + dimensions.getString("path-width"), false);
		DIMENSION_WALL_WIDTH  = new TextMessage(ChatColor.DARK_GREEN + "wall width\n" + ChatColor.GREEN + dimensions.getString("wall-width"), false);
		DIMENSION_ROOF_WIDTH  = new TextMessage(ChatColor.DARK_GREEN + "roof width\n" + ChatColor.GREEN + dimensions.getString("roof-width"), false);
		DIMENSION_PATH_LENGTH = new TextMessage(ChatColor.DARK_GREEN + "path length\n" + ChatColor.GREEN + dimensions.getString("path-length"), false);

		ConfigurationSection messages = langConfig.getConfigurationSection("messages");
		
		MESSAGE_PLUGIN_RELOADED         = new TextMessage(Constants.prefix + messages.getString("plugin-reloaded"), false);
		MESSAGE_TOOL_SWITCHED           = new TextMessage(Constants.prefix + messages.getString("tool-switched"), false);
		MESSAGE_TOOL_FOR_MAZE_ONLY      = new TextMessage(Constants.prefix + messages.getString("tool-for-floor-plan-only"), false);
		MESSAGE_DIMENSION_CHANGED       = new TextMessage(Constants.prefix + messages.getString("dimension-changed"), false);
		MESSAGE_MAZE_BUILDING_COMPLETED   = new TextMessage(Constants.prefix + messages.getString("maze-building-completed"), false);
		MESSAGE_MAZE_UNBUILDING_STARTED = new TextMessage(Constants.prefix + messages.getString("maze-unbuilding-started"), false);
		MESSAGE_NO_MAZE_TO_UNBUILD      = new TextMessage(Constants.prefix + messages.getString("no-maze-to-unbuild"), false);
	
		ConfigurationSection errors = langConfig.getConfigurationSection("errors");
		
		ERROR_NO_BUILD_PERMISSION          = new TextMessage(ChatColor.RED + errors.getString("insufficient-permission"), false);
		ERROR_CLIPBOARD_NOT_STARTED        = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-started"), false);
		ERROR_CLIPBOARD_NOT_COMPLETED      = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-completed"), false);
		ERROR_MAZE_NOT_STARTED             = new TextMessage(ChatColor.RED + errors.getString("maze-not-started"), false);
		ERROR_CLIPBOARD_NOT_TOUCHING_MAZE  = new TextMessage(ChatColor.RED + errors.getString("clipboard-not-touching-maze"), false);
		ERROR_NO_MAZE_EXIT_SET             = new TextMessage(ChatColor.RED + errors.getString("no-maze-exit-set"), false);
		ERROR_MAZE_ALREADY_BUILT           = new TextMessage(ChatColor.RED + errors.getString("maze-already-built"), false);
		ERROR_MAZE_NOT_BUILT               = new TextMessage(ChatColor.RED + errors.getString("maze-not-built"), false);
		ERROR_INVALID_NUMBER               = new TextMessage(ChatColor.RED + errors.getString("invalid-number"), false);
		ERROR_INVALID_DIMENSION            = new TextMessage(ChatColor.RED + errors.getString("invalid-dimension"), false);
		ERROR_INVALID_MAZE_PART            = new TextMessage(ChatColor.RED + errors.getString("invalid-maze-part"), false);
		ERROR_INVALID_BLOCK_NAME           = new TextMessage(ChatColor.RED + errors.getString("invalid-block-name"), false);
		ERROR_INVALID_BLOCK_PROPERTY       = new TextMessage(ChatColor.RED + errors.getString("invalid-block-property"), false);
		ERROR_INVALID_BLOCK_PROPERTY_VALUE = new TextMessage(ChatColor.RED + errors.getString("invalid-block-property-value"), false);
		ERROR_MISSING_BLOCK_PROPERTY_VALUE = new TextMessage(ChatColor.RED + errors.getString("missing-block-property-value"), false);
		ERROR_SAVE_FILE_CORRUPTED          = new TextMessage(ChatColor.RED + errors.getString("save-file-corrupted"), false);
	}
}