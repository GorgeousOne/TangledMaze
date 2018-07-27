package me.gorgeousone.tangledmaze.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.core.TangledMain;

public abstract class Utils {
	
	private static ArrayList<Vector> CARDINAL_DIRS = new ArrayList<>(Arrays.asList(
			new Vector( 1, 0,  0),
			new Vector( 0, 0,  1),
			new Vector(-1, 0,  0),
			new Vector( 0, 0, -1)));

	private static ArrayList<Vector> DIRECTIONS = new ArrayList<>(Arrays.asList(
			new Vector( 1, 0,  0),
			new Vector( 1, 0,  1),
			new Vector( 0, 0,  1),
			new Vector(-1, 0,  1),
			new Vector(-1, 0,  0),
			new Vector(-1, 0, -1),
			new Vector( 0, 0, -1),
			new Vector( 1, 0, -1)));

	private static ArrayList<Material> NOT_GROUND_SOLIDS = new ArrayList<>(Arrays.asList(
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
			Material.WHEAT,
			Material.WOOD_BUTTON,
			Material.WOOD_PLATE,
			Material.WOODEN_DOOR,
			Material.WEB,
			Material.YELLOW_FLOWER));
	
	private static ArrayList<Material> REPLACABLE_SOLIDS = new ArrayList<>(Arrays.asList(
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
	
	private static ArrayList<Material> NON_BUILD_SOLIDS = new ArrayList<>(Arrays.asList(
			Material.ACACIA_DOOR,
			Material.ACTIVATOR_RAIL,
			Material.BIRCH_DOOR,
			Material.BROWN_MUSHROOM,
			Material.CACTUS,
			Material.CARROT,
			Material.COCOA,
			Material.DARK_OAK_DOOR,
			Material.DEAD_BUSH,
			Material.DETECTOR_RAIL,
			Material.DOUBLE_PLANT,
			Material.FLOWER_POT,
			Material.GOLD_PLATE,
			Material.IRON_DOOR,
			Material.IRON_PLATE,
			Material.JUNGLE_DOOR,
			Material.LADDER,
			Material.LEVER,
			Material.LONG_GRASS,
			Material.MELON_STEM,
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
			Material.YELLOW_FLOWER));
	
	public static boolean isLikeGround(Material m) {
		return m.isSolid() && !NOT_GROUND_SOLIDS.contains(m);
	}
	
	public static boolean canBeReplaced(Material m) {
		return !m.isSolid() || REPLACABLE_SOLIDS.contains(m); 
	}

	public static boolean canBeBuiltWith(Material m) {
		return m.isSolid() && !NON_BUILD_SOLIDS.contains(m);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Vector> directions() {
		return (ArrayList<Vector>) DIRECTIONS.clone();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Vector> cardinalDirs() {
		return (ArrayList<Vector>) CARDINAL_DIRS.clone(); 
	}
	
	public static void sendBlockDelayed(Player p, Location loc, Material mat) {
		BukkitRunnable r = new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				p.sendBlockChange(loc, mat, (byte) 0);
			}
		};
		r.runTask(TangledMain.getPlugin());
	}
	
	public static Location nearestSurface(Location loc) {
		Location iter = loc.clone();
		
		if(isLikeGround(iter.getBlock().getType()))
			while(iter.getY() <= 255) {
				iter.add(0, 1, 0);
				
				if(!isLikeGround(iter.getBlock().getType())) {
					iter.add(0, -1, 0);
					return iter;
				}
			}
		else 
			while(iter.getY() >= 0) {
				iter.add(0, -1, 0);
				
				if(isLikeGround(iter.getBlock().getType()))
					return iter;
			}
		
		return loc;
	}
	
//	public static int getMin(ArrayList<Integer> ints) { TODO check relevance
//		int min = 0;
//		
//		for(int i : ints)
//			if(i < min)
//				min = i;
//		
//		return min;
//	}
	
	public static int getMaxY(ArrayList<Location> locs) {
		int max = 0;
		
		for(Location loc : locs)
			if(loc.getBlockY() > max)
				max = loc.getBlockY();
		
		return max;
	}
	
	public static ArrayList<Location> calcVertices(Location v0, Location v2) {
		ArrayList<Location> vertices = new ArrayList<>();
		World w = v0.getWorld();
		
		int maxY = Math.max(v0.getBlockY(), v2.getBlockY());
		
		int minX = Math.min(v0.getBlockX(), v2.getBlockX()),
			minZ = Math.min(v0.getBlockZ(), v2.getBlockZ()),
			maxX = Math.max(v0.getBlockX(), v2.getBlockX()),
			maxZ = Math.max(v0.getBlockZ(), v2.getBlockZ());
		
		vertices = new ArrayList<>(Arrays.asList(
				Utils.nearestSurface(new Location(w, minX, maxY, minZ)),
				Utils.nearestSurface(new Location(w, maxX, maxY, minZ)),
				Utils.nearestSurface(new Location(w, maxX, maxY, maxZ)),
				Utils.nearestSurface(new Location(w, minX, maxY, maxZ))));
		
		return vertices;
	}
}