package me.gorgeousone.tangledmaze.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Settings {

	public static String LANGUAGE;

	public static ItemStack MAZE_WAND;
	public static Material MAZE_WAND_ITEM;
	
	public static int
			MAX_PATH_WIDTH,
			MAX_WALL_WIDTH,
			MAX_WALL_HEIGHT;

	private Settings() {}

	public static void loadSettings(FileConfiguration config) {

		LANGUAGE = config.getString("language", "english");

		MAX_PATH_WIDTH = Utils.limitInt(config.getInt("maze.maximum-path-width", 50), 1, 255);
		MAX_WALL_WIDTH = Utils.limitInt(config.getInt("maze.maximum-wall-width", 50), 1, 255);
		MAX_WALL_HEIGHT = Utils.limitInt(config.getInt("maze.maximum-wall-height", 100), 1, 255);

		MAZE_WAND_ITEM = MaterialReader.readMaterial(config.getString("wand-item"));
		
		if(MAZE_WAND_ITEM != null) {
			return;
		}
		
		if(Constants.BUKKIT_VERSION < 13) {
			MAZE_WAND_ITEM = Material.valueOf("GOLD_SPADE");
		
		}else {
			MAZE_WAND_ITEM = Material.GOLDEN_SHOVEL;
		}
		
		createMazeWand();
	}
	
	private static void createMazeWand() {
		
		MAZE_WAND = new ItemStack(Settings.MAZE_WAND_ITEM);
		
		ItemMeta meta = MAZE_WAND.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Maze Wand");
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "A tool designed to create mazes.");
		lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Look at it's delicate curves! つ◕_◕つ");
		lore.add(ChatColor.GREEN + "Click on the ground to start a clipboard.");
		
		meta.setLore(lore);
		MAZE_WAND.setItemMeta(meta);
	}
}