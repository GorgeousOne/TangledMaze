package me.gorgeousone.tangledmaze.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.gorgeousone.tangledmaze.core.TangledMain;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Utils {

	public static boolean isMazeWand(ItemStack item) {

		if(item == null)
			return false;
		
		if(item.getType() != Settings.MAZE_WAND_ITEM) {
			return false;
		}
		
		ItemMeta itemMeta = item.getItemMeta();
		
		return
			itemMeta.getDisplayName() != null &&
			itemMeta.getDisplayName().equals(Settings.MAZE_WAND.getItemMeta().getDisplayName());
	}

	public static ItemStack getMazeWand() {
		
		ItemMeta rndMeta = Settings.MAZE_WAND.getItemMeta();
		List<String> lore = rndMeta.getLore();

		lore.set(0, ChatColor.GRAY + getRndMazeWandEnchantment());
		rndMeta.setLore(lore);
		
		ItemStack wand = Settings.MAZE_WAND.clone();
		wand.setItemMeta(rndMeta);
		
		return wand;
	}
	
	private static String getRndMazeWandEnchantment() {
		
		int rndIndex = (int) (Math.random() * Constants.MAZE_WAND_ENCHANTS.length);
		return Constants.MAZE_WAND_ENCHANTS[rndIndex];
	}

	public static boolean isLikeGround(Material m) {
		return m.isSolid() && !Constants.NOT_SOLIDS.contains(m);
	}
	
	public static boolean canBeOverbuild(Material m) {
		return !m.isSolid() || Constants.REPLACEABLE_SOLIDS.contains(m);
	}

	public static MazePoint nearestSurface(Location loc) {
	
		MazePoint iter = new MazePoint(loc);
		
		if(isLikeGround(iter.getBlock().getType())) {
		
			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);
				
				if(!isLikeGround(iter.getBlock().getType())) {
					iter.add(0, -1, 0);
					return iter;
				}
			}
		
		}else {
			
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(isLikeGround(iter.getBlock().getType())) {
					return iter;
				}
			}
		}
		
		return new MazePoint(loc);
	}
	
	public static int limitInt(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static int getMaxHeight(ArrayList<MazePoint> points) {
		
		int min = 0;
		
		for(MazePoint point : points) {
			if(point.getBlockY() > min) {
				min = point.getBlockY();
			}
		}
		
		return min;
	}

	public static YamlConfiguration getDefaultConfig(String fileName) {
		
		InputStream defConfigStream = TangledMain.getInstance().getResource(fileName);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}

	public static void saveConfig(FileConfiguration config, File file) {
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}