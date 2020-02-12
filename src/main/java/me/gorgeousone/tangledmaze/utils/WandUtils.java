package me.gorgeousone.tangledmaze.utils;

import me.gorgeousone.tangledmaze.data.ConfigSettings;
import me.gorgeousone.tangledmaze.data.Constants;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WandUtils {

	public static boolean isMazeWand(ItemStack item) {

		if(item == null || item.getType() != ConfigSettings.MAZE_WAND_MATERIAL)
			return false;

		String itemName = item.getItemMeta().getDisplayName();

		return !itemName.equals("") && itemName.equals(Constants.MAZE_WAND_NAME);
	}

	public static ItemStack getMazeWand() {

		ItemStack wand = new ItemStack(ConfigSettings.MAZE_WAND_MATERIAL);
		ItemMeta wandMeta = wand.getItemMeta();

		wandMeta.setDisplayName(Constants.MAZE_WAND_NAME);

		List<String> lore = wandMeta.getLore();
		lore.set(0, ChatColor.GRAY + getRndWandEnchantment());

		wandMeta.setLore(lore);
		wand.setItemMeta(wandMeta);
		return wand;
	}

	private static String getRndWandEnchantment() {

		int rndIndex = (int) (Math.random() * Constants.MAZE_WAND_ENCHANTS.length);
		return Constants.MAZE_WAND_ENCHANTS[rndIndex];
	}
}
