package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public final class BlockGenerator {

	private BlockGenerator() {}
	
	public static void generateBlocks(BuildMap map) {
		
		BukkitRunnable async = new BukkitRunnable() {
			@Override
			public void run() {
				
				simplifyMap(map);
				flattenProtrusiveMapParts(map);
				buildBlocksContinuously(getMazeBlocks(map));
			}
		};
		
		async.runTaskAsynchronously(TangledMain.getPlugin());
	}
	
	private static void buildBlocksContinuously(ArrayList<BlockState> blocksToUpdate) {
		
		BukkitRunnable builder = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!blocksToUpdate.isEmpty()) {
					
					blocksToUpdate.get(0).update(true, false);
					blocksToUpdate.remove(0);
					
					if(System.currentTimeMillis() - timer >= 49)
						return;
				}
				
				this.cancel();
			}
		};
		builder.runTaskTimer(TangledMain.getPlugin(), 0, 1);
	}
	
	@SuppressWarnings("deprecation")
	private static ArrayList<BlockState> getMazeBlocks(BuildMap map) {
		
		Maze maze = map.getMaze();
		Random rnd = new Random();
		
		ArrayList<MaterialData> composition = maze.getWallComposition();
		ArrayList<BlockState> blocksToUpdate = new ArrayList<>();
		
		int mazeMinX = map.getMinX(),
			mazeMinZ = map.getMinZ();
		
		int wallHeight = maze.getWallHeight();
		
		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				MazeFillType type = map.getType(x, z);
				
				if(type != MazeFillType.WALL) {
					continue;
				}
				
				int height = map.getHeight(x, z),
					maxNeighborHeight = getMaxNeighborPathHeight(x, z, map);
				
				for(int i = height + 1; i <= Math.max(height + wallHeight, maxNeighborHeight + 2); i++) {
					
					BlockState block = new Location(maze.getWorld(), x + mazeMinX, i, z + mazeMinZ).getBlock().getState();
					
					if(!Utils.canBeOverbuild(block.getType())) {
						continue;
					}
					
					MaterialData rndMatData = composition.get(rnd.nextInt(composition.size()));
					
					block.setType(rndMatData.getItemType());
					block.setRawData(rndMatData.getData());
					blocksToUpdate.add(block);
				}
			}
		}
		
		return blocksToUpdate;
	}
	
	private static void simplifyMap(BuildMap map) {
		
		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				MazeFillType type = map.getType(x, z);
				
				if(type == MazeFillType.UNDEFINED) {
					map.setType(x, z, MazeFillType.WALL);
					
				}else if(type == MazeFillType.EXIT) {
					map.setType(x, z, MazeFillType.PATH);
				}
			}
		}
	}
		
	private static void flattenProtrusiveMapParts(BuildMap map) {
		
		int wallHeight = map.getMaze().getWallHeight();
		
		HashMap<Vec2, Integer> protrudingPoints = new HashMap<>();
		
		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
		
				if(map.getType(x, z) != MazeFillType.WALL) {
					continue;
				}
				
				int height = map.getHeight(x, z),
					heightDiff = getNeighborWallHeightDiff(map, x, z),
					maxNeighborHeight = getMaxNeighborPathHeight(x, z, map);
				
				//point is not higher than anything around, not smoothing needed
				if(heightDiff >= 0) {
					continue;
				}
				
				//point is so high, it could be a wall, e.g. trees, set height lower so there will be no wall built
				if(groundProtrudesLikeAWall(height, maxNeighborHeight, wallHeight)) {
					protrudingPoints.put(new Vec2(x, z), height + heightDiff);
					continue;
				
				//smoothing the wall down would erase it completely, don't do that
				}else if(wallHeight + heightDiff < 1) {
					continue;
				}
				
				//check that wall is so high that it cannot be jumped on from path directly around 
				if(!wallCanBeClimbed(height, wallHeight, heightDiff, maxNeighborHeight)) {
					protrudingPoints.put(new Vec2(x, z), height + heightDiff);
				}
			}
		}
		
		for(Vec2 point : protrudingPoints.keySet()) {
			map.setHeight(point, protrudingPoints.get(point));
		}
	}
	
	//	private static void smoothMap(BuildMap map) {
	//		
	//		int wallHeight = map.getMaze().getWallHeight();
	//
	//		for(int x = 0; x < map.getDimX(); x++) {
	//			for(int z = 0; z < map.getDimZ(); z++) {
	//		
	//				if(map.getType(x, z) != MazeFillType.WALL) {
	//					continue;
	//				}
	//			}
	//		}
	//	}
	
	private static int getMaxNeighborPathHeight(int x, int z, BuildMap map) {
		
		ArrayList<Integer> neighborHeights = new ArrayList<>();
		
		for(Directions dir : Directions.values()) {
			
			int neighborX = x + dir.toVec2().getIntX(),
				neighborZ = z + dir.toVec2().getIntZ();
			
			if(neighborX < 0 || neighborX >= map.getDimX() ||
			   neighborZ < 0 || neighborZ >= map.getDimZ()) {
				continue;
			}
			
			
			if(map.getType(neighborX, neighborZ) != MazeFillType.PATH) {
				continue;
			}

			neighborHeights.add(map.getHeight(neighborX, neighborZ));
		}
		
		if(neighborHeights.isEmpty()) {
			return -1;
		}
		
		return Utils.getMax(neighborHeights);
	}

	private static int getNeighborWallHeightDiff(BuildMap map, int x, int z) {
		
		int height = map.getHeight(x, z),
			heightDiff = 0,
			neighborCount = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 facing= dir.toVec2();
			
			int neighborX = x + facing.getIntX(),
				neighborZ = z + facing.getIntZ();
			
			if(neighborX < 0 || neighborX >= map.getDimX() ||
			   neighborZ < 0 || neighborZ >= map.getDimZ())
				continue;
			
			if(map.getType(neighborX, neighborZ) != MazeFillType.WALL) {
				continue;
			}
			
			heightDiff += map.getHeight(neighborX, neighborZ) - height;
			neighborCount++;
		}
		
		return heightDiff / neighborCount;
	}
	
	private static boolean wallCanBeClimbed(int height, int wallHeight, int heightDiff, int maxNeighborHeight) {
		return height + (wallHeight - heightDiff) < maxNeighborHeight + 2;
	}
	
	private static boolean groundProtrudesLikeAWall(int height, int maxNeighborHeight, int wallHeight) {
		return height > maxNeighborHeight + wallHeight;
	}
}