package me.gorgeousone.tangledmaze.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import me.gorgeousone.tangledmaze.core.TangledMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class Utils {

	private final static TreeSet<Material> NOT_SOLIDS = new TreeSet<>(Arrays.asList(
			Material.ACACIA_DOOR,
			Material.ACTIVATOR_RAIL,
			Material.ANVIL,
			Material.BIRCH_DOOR,
			Material.BREWING_STAND,
			Material.BROWN_MUSHROOM,
			Material.CACTUS,
			Material.CARPET,
			Material.CARROT,
			Material.COCOA,
			Material.CHEST,
			Material.DARK_OAK_DOOR,
			Material.DAYLIGHT_DETECTOR,
			Material.DEAD_BUSH,
			Material.DETECTOR_RAIL,
			Material.DOUBLE_PLANT,
			Material.ENDER_CHEST,
			Material.FIRE,
			Material.FLOWER_POT,
			Material.GOLD_PLATE,
			Material.IRON_DOOR,
			Material.IRON_PLATE,
			Material.JUNGLE_DOOR,
			Material.LADDER,
			Material.LEAVES,
			Material.LEAVES_2,
			Material.LEVER,
			Material.LONG_GRASS,
			Material.MELON_STEM,
			Material.PISTON_MOVING_PIECE,
			Material.POTATO,
			Material.POWERED_RAIL,
			Material.PUMPKIN_STEM,
			Material.RAILS,
			Material.RED_MUSHROOM,
			Material.RED_ROSE,
			Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_WIRE,
			Material.SAPLING,
			Material.SIGN_POST,
			Material.SKULL,
			Material.SPRUCE_DOOR,
			Material.SNOW,
			Material.STANDING_BANNER,
			Material.STONE_BUTTON,
			Material.STONE_PLATE,
			Material.SUGAR_CANE_BLOCK,
			Material.TORCH,
			Material.TRAPPED_CHEST,
			Material.TRIPWIRE,
			Material.TRIPWIRE_HOOK,
			Material.VINE,
			Material.WALL_BANNER,
			Material.WALL_SIGN,
			Material.WATER_LILY,
			Material.WEB,
			Material.WHEAT,
			Material.WOOD_BUTTON,
			Material.WOOD_PLATE,
			Material.WOODEN_DOOR,
			Material.YELLOW_FLOWER));

	private static TreeSet<Material> REPLACEABLE_SOLIDS = new TreeSet<>(Arrays.asList(
			Material.BROWN_MUSHROOM,
			Material.CACTUS,
			Material.CARPET,
			Material.CARROT,
			Material.COCOA,
			Material.DEAD_BUSH,
			Material.DOUBLE_PLANT,
			Material.FIRE,
			Material.LONG_GRASS,
			Material.MELON_STEM,
			Material.POTATO,
			Material.PUMPKIN_STEM,
			Material.RED_MUSHROOM,
			Material.RED_ROSE,
			Material.SAPLING,
			Material.SNOW,
			Material.WATER_LILY,
			Material.WHEAT,
			Material.YELLOW_FLOWER));
	
	public static boolean isLikeGround(Material m) {
		return m.isSolid() && !NOT_SOLIDS.contains(m);
	}
	
	public static boolean canBeOverbuild(Material m) {
		return !m.isSolid() || REPLACEABLE_SOLIDS.contains(m);
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
	
	public static int getMaxHeight(ArrayList<MazePoint> points) {
		
		int min = 0;
		
		for(MazePoint point : points) {
			if(point.getBlockY() > min) {
				min = point.getBlockY();
			}
		}
		
		return min;
	}

	public static int getBukkitVersion() {
		return Integer.valueOf(Bukkit.getBukkitVersion().split("\\.")[1]);
	}

	public static YamlConfiguration getDefaultConfig(String fileName) {

		InputStream defConfigStream = TangledMain.getPlugin().getResource(fileName);
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