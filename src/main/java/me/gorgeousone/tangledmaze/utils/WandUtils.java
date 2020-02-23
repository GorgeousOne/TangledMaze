package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WandUtils {
	
	public static boolean isMazeWand(ItemStack item) {
		
		if (item == null || item.getType() != ConfigSettings.MAZE_WAND_MATERIAL)
			return false;
		
		return Constants.MAZE_WAND_NAME.equals(item.getItemMeta().getDisplayName());
	}
	
	public static ItemStack createMazeWand() {
		
		ItemStack wand = new ItemStack(ConfigSettings.MAZE_WAND_MATERIAL);
		
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GREEN + "Maze Wand");
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + getRandomCrazyEnchantment());
		lore.add(ChatColor.GREEN + "A tool designed to create mazes.");
		lore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Look at it's delicate curves! つ◕_◕つ");
		lore.add(ChatColor.GREEN + "Click on the ground to start a clipboard.");
		
		meta.setLore(lore);
		wand.setItemMeta(meta);
		
		return wand;
	}
	
	private static String getRandomCrazyEnchantment() {
		
		int rndIndex = (int) (Math.random() * Constants.MAZE_WAND_ENCHANTS.length);
		return Constants.MAZE_WAND_ENCHANTS[rndIndex];
	}
}
