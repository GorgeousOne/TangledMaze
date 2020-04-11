package me.gorgeousone.tangledmaze.data;

import me.gorgeousone.tangledmaze.messages.PlaceHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public enum Messages {
	
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
	COMMAND_BACKUP,
	COMMAND_LOAD,
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
	MESSAGE_BACKUP_SAVED,
	MESSAGE_BACKUP_LOADED,
	ERROR_INSUFFICIENT_PERMISSION,
	ERROR_CLIPBOARD_NOT_STARTED,
	ERROR_CLIPBOARD_NOT_COMPLETED,
	ERROR_MAZE_NOT_STARTED,
	ERROR_CLIPBOARD_NOT_TOUCHING_MAZE,
	ERROR_NO_MAZE_EXIT_SET,
	ERROR_MAZE_PART_ALREADY_BUILT,
	ERROR_MAZE_NOT_BUILT,
	ERROR_INVALID_DIMENSION,
	ERROR_INVALID_MAZE_PART,
	ERROR_INVALID_BLOCK_NAME,
	ERROR_INVALID_BLOCK_PROPERTY,
	ERROR_INVALID_BLOCK_PROPERTY_VALUE,
	ERROR_MISSING_BLOCK_PROPERTY_VALUE,
	BACKUP_FILE_NOT_FOUND,
	MESSAGE_BACKUP_FILE_ALREADY_EXISTS,
	BACKUP_FILE_LOAD_ERROR;
	
	String[] paragraphs;
	
	private void setText(String message, boolean readColorCodes) {
		
		String alteredMessage = readColorCodes ? ChatColor.translateAlternateColorCodes('&', message) : message;
		paragraphs = alteredMessage.split("\\\\n");
		
		if (paragraphs.length < 2)
			return;
		
		for (int i = 1; i < paragraphs.length; i++) {
			paragraphs[i] = ChatColor.getLastColors(paragraphs[i - 1]) + paragraphs[i];
		}
	}
	
	public void sendTo(CommandSender receiver) {
		
		for (String paragraph : paragraphs) {
			receiver.sendMessage(paragraph);
		}
	}
	
	public void sendTo(CommandSender receiver, PlaceHolder... placeHolders) {
		
		for (String paragraph : paragraphs) {
			String alteredParagraph = paragraph;
			
			for (PlaceHolder placeHolder : placeHolders)
				alteredParagraph = placeHolder.apply(alteredParagraph);
			
			receiver.sendMessage(alteredParagraph);
		}
	}
	
	public static void loadMessages(FileConfiguration langConfig) {
		
		ConfigurationSection helpPages = langConfig.getConfigurationSection("help-pages");
		
		COMMAND_WAND       .setText(ChatColor.DARK_GREEN + "/maze wand\n" + ChatColor.GREEN + helpPages.getString("wand-command"), false);
		COMMAND_START      .setText(ChatColor.DARK_GREEN + "/maze start\n" + ChatColor.GREEN + helpPages.getString("start-command"), true);
		COMMAND_DISCARD    .setText(ChatColor.DARK_GREEN + "/maze discard\n" + ChatColor.GREEN + helpPages.getString("discard-command"), true);
		COMMAND_TELEPORT   .setText(ChatColor.DARK_GREEN + "/maze teleport\n" + ChatColor.GREEN + helpPages.getString("teleport-command"), true);
		COMMAND_SELECT     .setText(ChatColor.DARK_GREEN + "/maze select <tool>\n" + ChatColor.GREEN + helpPages.getString("select-command"), true);
		COMMAND_ADD_CUT    .setText(ChatColor.DARK_GREEN + "/maze add / cut\n" + ChatColor.GREEN + helpPages.getString("add-cut-command"), true);
		COMMAND_UNDO       .setText(ChatColor.DARK_GREEN + "/maze undo\n" + ChatColor.GREEN + helpPages.getString("undo-command"), true);
		COMMAND_DIMENSIONS .setText(ChatColor.DARK_GREEN + "/maze set <dimension> <integer>\n" + ChatColor.GREEN + helpPages.getString("set-dimension-command"), true);
		COMMAND_BUILD      .setText(ChatColor.DARK_GREEN + "/maze build <block> ...\n" + ChatColor.GREEN + helpPages.getString("build-command"), true);
		COMMAND_UNBUILD    .setText(ChatColor.DARK_GREEN + "/maze unbuild\n" + ChatColor.GREEN + helpPages.getString("unbuild-command"), true);
		COMMAND_BACKUP     .setText(ChatColor.DARK_GREEN + "/maze backup <filename> <overwrite>\n" + ChatColor.GREEN + helpPages.getString("backup-command"), true);
		COMMAND_LOAD       .setText(ChatColor.DARK_GREEN + "/maze load <filename>\n" + ChatColor.GREEN + helpPages.getString("load-command"), true);
		
		ConfigurationSection tools = helpPages.getConfigurationSection("tools");
		
		TOOL_RECT   .setText(ChatColor.DARK_GREEN + "rectangle\n" + ChatColor.GREEN + tools.getString("rectangle"), false);
		TOOL_CIRCLE .setText(ChatColor.DARK_GREEN + "circle\n" + ChatColor.GREEN + tools.getString("circle"), false);
		TOOL_BRUSH  .setText(ChatColor.DARK_GREEN + "brush\n" + ChatColor.GREEN + tools.getString("brush"), false);
		TOOL_EXIT   .setText(ChatColor.DARK_GREEN + "exit\n" + ChatColor.GREEN + tools.getString("exit"), false);
		
		ConfigurationSection dimensions = helpPages.getConfigurationSection("dimensions");
		
		DIMENSION_WALL_HEIGHT .setText(ChatColor.DARK_GREEN + "wall height\n" + ChatColor.GREEN + dimensions.getString("wall-height"), false);
		DIMENSION_PATH_WIDTH  .setText(ChatColor.DARK_GREEN + "path width\n" + ChatColor.GREEN + dimensions.getString("path-width"), false);
		DIMENSION_WALL_WIDTH  .setText(ChatColor.DARK_GREEN + "wall width\n" + ChatColor.GREEN + dimensions.getString("wall-width"), false);
		DIMENSION_ROOF_WIDTH  .setText(ChatColor.DARK_GREEN + "roof width\n" + ChatColor.GREEN + dimensions.getString("roof-width"), false);
		DIMENSION_PATH_LENGTH .setText(ChatColor.DARK_GREEN + "path length\n" + ChatColor.GREEN + dimensions.getString("path-length"), false);
		
		ConfigurationSection messages = langConfig.getConfigurationSection("messages");
		
		MESSAGE_PLUGIN_RELOADED            .setText(Constants.prefix + messages.getString("plugin-reloaded"), false);
		MESSAGE_TOOL_SWITCHED              .setText(Constants.prefix + messages.getString("tool-switched"), false);
		MESSAGE_TOOL_FOR_MAZE_ONLY         .setText(Constants.prefix + messages.getString("tool-for-floor-plan-only"), false);
		MESSAGE_DIMENSION_CHANGED          .setText(Constants.prefix + messages.getString("dimension-changed"), false);
		MESSAGE_MAZE_BUILDING_COMPLETED    .setText(Constants.prefix + messages.getString("maze-building-completed"), false);
		MESSAGE_MAZE_UNBUILDING_STARTED    .setText(Constants.prefix + messages.getString("maze-unbuilding-started"), false);
		MESSAGE_NO_MAZE_TO_UNBUILD         .setText(Constants.prefix + messages.getString("no-maze-to-unbuild"), false);
		MESSAGE_BACKUP_SAVED               .setText(Constants.prefix + messages.getString("backup-file-saved"), true);
		MESSAGE_BACKUP_LOADED              .setText(Constants.prefix + messages.getString("backup-file-loaded"), true);
		MESSAGE_BACKUP_FILE_ALREADY_EXISTS .setText(Constants.prefix + messages.getString("backup-file-already-exists"), true);
		
		ConfigurationSection errors = langConfig.getConfigurationSection("errors");
		
		ERROR_INSUFFICIENT_PERMISSION      .setText(ChatColor.RED + errors.getString("insufficient-permission"), false);
		ERROR_CLIPBOARD_NOT_STARTED        .setText(ChatColor.RED + errors.getString("clipboard-not-started"), false);
		ERROR_CLIPBOARD_NOT_COMPLETED      .setText(ChatColor.RED + errors.getString("clipboard-not-completed"), false);
		ERROR_MAZE_NOT_STARTED             .setText(ChatColor.RED + errors.getString("maze-not-started"), false);
		ERROR_CLIPBOARD_NOT_TOUCHING_MAZE  .setText(ChatColor.RED + errors.getString("clipboard-not-touching-maze"), false);
		ERROR_NO_MAZE_EXIT_SET             .setText(ChatColor.RED + errors.getString("no-maze-exit-set"), false);
		ERROR_MAZE_PART_ALREADY_BUILT      .setText(ChatColor.RED + errors.getString("maze-part-already-built"), false);
		ERROR_MAZE_NOT_BUILT               .setText(ChatColor.RED + errors.getString("no-maze-to-unbuild"), false);
		ERROR_INVALID_DIMENSION            .setText(ChatColor.RED + errors.getString("invalid-dimension"), false);
		ERROR_INVALID_MAZE_PART            .setText(ChatColor.RED + errors.getString("invalid-maze-part"), false);
		ERROR_INVALID_BLOCK_NAME           .setText(ChatColor.RED + errors.getString("invalid-block-name"), false);
		ERROR_INVALID_BLOCK_PROPERTY       .setText(ChatColor.RED + errors.getString("invalid-block-property"), false);
		ERROR_INVALID_BLOCK_PROPERTY_VALUE .setText(ChatColor.RED + errors.getString("invalid-block-property-value"), false);
		ERROR_MISSING_BLOCK_PROPERTY_VALUE .setText(ChatColor.RED + errors.getString("missing-block-property-value"), false);
		BACKUP_FILE_NOT_FOUND              .setText(ChatColor.RED + errors.getString("backup-file-not-found"), false);
		BACKUP_FILE_LOAD_ERROR             .setText(ChatColor.RED + errors.getString("backup-file-load-error"), true);
	}
}