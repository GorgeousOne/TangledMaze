package me.gorgeousone.tangledmaze.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public final class Settings {

	public static String LANGUAGE;
	public static Material MAZE_WAND_ITEM;

	public static int
			MAX_PATH_WIDTH,
			MAX_WALL_WIDTH,
			MAX_WALL_HEIGHT;

	private Settings() {}

	public static void loadSettings(FileConfiguration config) {

		LANGUAGE = config.getString("language", "english");

		MAX_PATH_WIDTH = bytify(config.getInt("maze.maximum-path-width", 50));
		MAX_WALL_WIDTH = bytify(config.getInt("maze.maximum-wall-width", 50));
		MAX_WALL_HEIGHT = bytify(config.getInt("maze.maximum-wall-height", 100));

		MAZE_WAND_ITEM = MaterialReader.readMaterial(config.getString("wand-item"));
		
		
		if(MAZE_WAND_ITEM != null) {
			return;
		}
		
		if(Constants.BUKKIT_VERSION < 13) {
			MAZE_WAND_ITEM = Material.valueOf("GOLD_SPADE");
		
		}else {
			MAZE_WAND_ITEM = Material.GOLDEN_SHOVEL;
		}
	}

	private static int bytify(int value) {
		return Math.min(255, Math.max(1, value));
	}
}