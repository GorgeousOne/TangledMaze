package me.gorgeousone.tangledmaze.util;

import org.bukkit.configuration.file.FileConfiguration;

public final class Settings {

	public static String LANGUAGE;

	public static int
			MAZE_WAND_ITEM,
			MAX_PATH_WIDTH,
			MAX_WALL_WIDTH,
			MAX_WALL_HEIGHT;

	private Settings() {}

	public static void loadSettings(FileConfiguration config) {

		LANGUAGE = config.getString("language", "english");
		MAZE_WAND_ITEM = config.getInt("wand-item", 50);

		MAX_PATH_WIDTH = limitInt(config.getInt("maze.maximum-path-width", 50));
		MAX_WALL_WIDTH = limitInt(config.getInt("maze.maximum-wall-width", 50));
		MAX_WALL_HEIGHT = limitInt(config.getInt("maze.maximum-wall-height", 100));
	}

	private static int limitInt(int value) {
		return Math.min(255, Math.max(1, value));
	}
}
