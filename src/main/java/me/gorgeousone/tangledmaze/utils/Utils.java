package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class Utils {

	public static boolean isMazeWand(ItemStack item) {

		if (item == null || item.getType() != ConfigSettings.MAZE_WAND_MATERIAL)
			return false;

		ItemMeta itemMeta = item.getItemMeta();

		return
				itemMeta.hasDisplayName() &&
						itemMeta.getDisplayName().equals(ConfigSettings.MAZE_WAND.getItemMeta().getDisplayName());
	}

	public static ItemStack getMazeWand() {

		ItemStack wand = ConfigSettings.MAZE_WAND.clone();
		ItemMeta rndMeta = wand.getItemMeta();
		List<String> lore = rndMeta.getLore();

		lore.set(0, ChatColor.GRAY + getRndMazeWandEnchantment());
		rndMeta.setLore(lore);

		wand.setItemMeta(rndMeta);
		return wand;
	}

	private static String getRndMazeWandEnchantment() {

		int rndIndex = (int) (Math.random() * Constants.MAZE_WAND_ENCHANTS.length);
		return Constants.MAZE_WAND_ENCHANTS[rndIndex];
	}

	public static boolean canBeOverbuild(Material mat) {
		return !mat.isSolid() || Constants.REPLACEABLE_SOLIDS.contains(mat);
	}

	public static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}

	public static int getMaxHeight(ArrayList<Location> locs) {

		int min = 0;

		for (Location point : locs) {

			if (point.getBlockY() > min)
				min = point.getBlockY();
		}

		return min;
	}

	//	public static YamlConfiguration loadSaveFile(String configName) throws TextException {
	//
	//		File configFile = new File(TangledMain.getInstance().getDataFolder() + File.separator + configName + ".yml");
	//
	//		if(!configFile.exists())
	//			throw new TextException(Messages.ERROR_INVALID_MAZE_PART);
	//
	//		return YamlConfiguration.loadConfiguration(new File(TangledMain.getInstance().getDataFolder() + File.separator + configName + ".yml"));
	//	}

	public static YamlConfiguration loadDefaultConfig(String configName) {
		InputStream defConfigStream = TangledMain.getInstance().getResource(configName + ".yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}

	public static YamlConfiguration loadConfig(String configName) {

		File configFile = new File(TangledMain.getInstance().getDataFolder() + File.separator + configName + ".yml");
		YamlConfiguration defConfig = loadDefaultConfig(configName);

		if (!configFile.exists()) {
			try {
				defConfig.save(configFile);
			} catch (IOException ignored) {
			}
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(defConfig);
		config.options().copyDefaults(true);

		return config;
	}
}