package me.tangledmaze.gorgeousone.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.TangledMain;

public abstract class Utils {
	
	private static ArrayList<Vector> DIRECTIONS = new ArrayList<>(Arrays.asList(
			new Vector( 1, 0,  0),
			new Vector( 1, 0,  1),
			new Vector( 0, 0,  1),
			new Vector(-1, 0,  1),
			new Vector(-1, 0,  0),
			new Vector(-1, 0, -1),
			new Vector( 0, 0, -1),
			new Vector( 1, 0, -1)));
	
	private static ArrayList<Vector> CARDINAL_DIRS = new ArrayList<>(Arrays.asList(
			new Vector( 1, 0,  0),
			new Vector( 0, 0,  1),
			new Vector(-1, 0,  0),
			new Vector( 0, 0, -1)));

	private static ArrayList<Material> UNSATABLE_SOLIDS = new ArrayList<>(Arrays.asList(
			Material.ACACIA_DOOR,
			Material.ACTIVATOR_RAIL,
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
			Material.WHEAT,
			Material.WOOD_PLATE,
			Material.WOOD_BUTTON,
			Material.WOODEN_DOOR,
			Material.WEB,
			Material.YELLOW_FLOWER));
	
	private static ArrayList<Material> REPLACABLE_SOLIDS = new ArrayList<>(Arrays.asList(
			Material.BROWN_MUSHROOM,
			Material.CACTUS,
			Material.CARPET,
			Material.CARROT,
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
			Material.SUGAR_CANE_BLOCK,
			Material.VINE,
			Material.WATER_LILY,
			Material.WHEAT,
			Material.WEB,
			Material.YELLOW_FLOWER));
	
	public static boolean isReallySolid(Block b) {
		return b.getType().isSolid() && !UNSATABLE_SOLIDS.contains(b.getType());
	}

	public static boolean canBeReplaced(Block b) {
		return !b.getType().isSolid() || REPLACABLE_SOLIDS.contains(b.getType()); 
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Vector> directions() {
		return (ArrayList<Vector>) DIRECTIONS.clone();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Vector> cardinalDirs() {
		return (ArrayList<Vector>) CARDINAL_DIRS.clone(); 
	}
	
	public static void sendBlockLater(Player p, Location loc, Material mat) {
		BukkitRunnable r = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				p.sendBlockChange(loc, mat, (byte) 0);
			}
		};
		r.runTask(TangledMain.getPlugin());
	}
	
	public static Location getNearestSurface(Location loc) {
		Location iter = loc.clone();
		
		if(isReallySolid(iter.getBlock()))
			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);
				
				if(!isReallySolid(iter.getBlock())) {
					iter.add(0, -1, 0);
					return iter;
				}
			}
		else 
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(isReallySolid(iter.getBlock()))
					return iter;
			}
		
		return loc;
	}
	
	public static int getMax(ArrayList<Integer> ints) {
		int max = 0;
		
		for(int i : ints)
			if(i > max)
				max = i;
		
		return max;
	}
}