package me.gorgeousone.tangledmaze.data;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ConfigSettings {

	public static ItemStack MAZE_WAND;
	public static Material MAZE_WAND_MATERIAL;
	
	private ConfigSettings() {}

	public static void loadSettings(FileConfiguration config) {

		MAZE_WAND_MATERIAL = Material.matchMaterial(config.getString("wand-item"));
		
		if(MAZE_WAND_MATERIAL == null)
			MAZE_WAND_MATERIAL = Material.GOLDEN_SHOVEL;
		
		createMazeWand();
	}
	
	private static void createMazeWand() {
		
		MAZE_WAND = new ItemStack(ConfigSettings.MAZE_WAND_MATERIAL);
		
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