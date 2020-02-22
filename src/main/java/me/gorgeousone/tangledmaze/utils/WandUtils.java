package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WandUtils {

	public static boolean isMazeWand(ItemStack item) {

		if (item == null || item.getType() != ConfigSettings.MAZE_WAND_MATERIAL)
			return false;

		return Constants.MAZE_WAND_NAME.equals(item.getItemMeta().getDisplayName());
	}

	public static ItemStack createMazeWand() {

		ItemStack wand = new ItemStack(ConfigSettings.MAZE_WAND_MATERIAL);
		ItemMeta wandMeta = wand.getItemMeta();

		//TODO check why NPE ...
		if(wandMeta != null) {
			wandMeta.setDisplayName(Constants.MAZE_WAND_NAME);
			
			List<String> lore = wandMeta.getLore();
			
			if(lore != null)
				lore.set(0, ChatColor.GRAY + getRndWandEnchantment());
			
			wandMeta.setLore(lore);
		}
		
		wand.setItemMeta(wandMeta);
		return wand;
	}

	private static String getRndWandEnchantment() {

		int rndIndex = (int) (Math.random() * Constants.MAZE_WAND_ENCHANTS.length);
		return Constants.MAZE_WAND_ENCHANTS[rndIndex];
	}
}
