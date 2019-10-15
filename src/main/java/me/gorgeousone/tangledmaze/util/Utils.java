package me.gorgeousone.tangledmaze.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Settings;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Utils {

	public static boolean isMazeWand(ItemStack item) {

		if(item == null)
			return false;
		
		if(item.getType() != Settings.MAZE_WAND_MATERIAL)
			return false;

		ItemMeta itemMeta = item.getItemMeta();

		return
			itemMeta.getDisplayName() != null &&
			itemMeta.getDisplayName().equals(Settings.MAZE_WAND.getItemMeta().getDisplayName());
	}

	public static ItemStack getMazeWand() {
		
		ItemStack wand = Settings.MAZE_WAND.clone();
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
	
	public static boolean isLikeGround(Material mat) {
		return mat.isSolid() && !Constants.NOT_SOLIDS.contains(mat);
	}
	
	public static boolean canBeOverbuild(Material mat) {
		return !mat.isSolid() || Constants.REPLACEABLE_SOLIDS.contains(mat);
	}

	public static Location nearestSurface(Location loc) {
		
		Location iter = loc.clone();
		
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
		
		return loc;
	}
	
	public static int nearestSurfaceY(Vec2 loc, int height, World world) {
		
		Location iter = new Location(world, loc.getX(), height, loc.getZ());
		
		if(isLikeGround(iter.getBlock().getType())) {
			
			while(iter.getY() <= 255) {
				
				iter.add(0, 1, 0);
				
				if(!isLikeGround(iter.getBlock().getType())) {
					iter.add(0, -1, 0);
					return iter.getBlockY();
				}
			}
		
		}else {
			
			while(iter.getY() >= 0) {
				
				iter.add(0, -1, 0);
				
				if(isLikeGround(iter.getBlock().getType())) {
					return iter.getBlockY();
				}
			}
		}
		
		return height;
	}
	
	public static int limit(int value, int min, int max) {
		return Math.min(max, Math.max(min, value));
	}
	
	public static int getMaxHeight(ArrayList<Location> locs) {
		
		int min = 0;
		
		for(Location point : locs) {
			
			if(point.getBlockY() > min)
				min = point.getBlockY();
		}
		
		return min;
	}

	public static YamlConfiguration loadDataFile(String fileName) {

		File dataFile = new File(TangledMain.getInstance().getDataFolder() + File.separator + fileName + ".yml");

		if(!dataFile.exists())
			throw new NullPointerException("No data file found: " + fileName);

		return YamlConfiguration.loadConfiguration(dataFile);
	}

	public static YamlConfiguration loadDefaultConfig(String configName) {
		InputStream defConfigStream = TangledMain.getInstance().getResource(configName + ".yml");
		return YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
	}

	public static YamlConfiguration loadConfig(String configName) {

		File configFile = new File(TangledMain.getInstance().getDataFolder() + File.separator + configName + ".yml");
		YamlConfiguration defConfig = loadDefaultConfig(configName);

		if(!configFile.exists()) {
			try {
				defConfig.save(configFile);
			} catch (IOException ignored) {}
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.setDefaults(defConfig);
		config.options().copyDefaults(true);

		return config;
	}


}