package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class BlockGenerator {

	public void generateBlocks(BuildMap map) {
		
		simplifyMap(map);
		flattenTrees(map);
		raiseLowMapParts(map);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				updateBlocksContinuously(getMazeWallBlocks(map));
			}
		}.runTask(TangledMain.getInstance());
	}
	
	protected void updateBlocksContinuously(List<BlockState> blocksToUpdate) {
		
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
		
		builder.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
	
	protected List<BlockState> getMazeWallBlocks(BuildMap map) {
		
		Maze maze = map.getMaze();
		Random rnd = new Random();
		
		List<Material> wallMaterials = maze.getWallMaterials();
		List<BlockState> blocksToUpdate = new ArrayList<>();
		
		int mazeMinX = map.getMinX(),
			mazeMinZ = map.getMinZ();
	
		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				if(map.getType(x, z) != MazeFillType.WALL) {
					continue;
				}
				
				for(int i = map.getGroundHeight(x, z) + 1; i <= map.getMazeHeight(x, z); i++) {
					
					BlockState block = new Location(maze.getWorld(), x + mazeMinX, i, z + mazeMinZ).getBlock().getState();
					
					if(!Utils.canBeOverbuild(block.getType())) {
						continue;
					}
					
					Material rndMaterial = wallMaterials.get(rnd.nextInt(wallMaterials.size()));
					
					block.setType(rndMaterial);
					blocksToUpdate.add(block);
				}
			}
		}
		
		return blocksToUpdate;
	}
	
	protected void simplifyMap(BuildMap map) {
		
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
	
	protected void flattenTrees(BuildMap map) {
		
		int wallHeight = map.getMaze().getWallHeight();

		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				if(map.getType(x, z) == MazeFillType.NOT_MAZE) {
					continue;
				}
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, map, null);
				
				int mazeHeight = map.getMazeHeight(x, z),
					minimumMazeHeight = map.getGroundHeight(maxNeighbor) + wallHeight;
				
				if(mazeHeight <= minimumMazeHeight) {
					continue;
				}
				
				int groundHeightDiff = getNeighborGroundHeightDiff(map, x, z);
				
				if(map.getType(x, z) == MazeFillType.PATH) {
					map.setGroundHeight(x, z, map.getGroundHeight(x, z) + groundHeightDiff);
					
				}else {
					map.setMazeHeight(x, z, Math.min(minimumMazeHeight, mazeHeight + groundHeightDiff));
				}
			}
		}
	}
	
	protected void raiseLowMapParts(BuildMap map) {
		
		int wallHeight = map.getMaze().getWallHeight();

		for(int x = 0; x < map.getDimX(); x++) {
			for(int z = 0; z < map.getDimZ(); z++) {
				
				if(map.getType(x, z) == MazeFillType.NOT_MAZE) {
					continue;
				}
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, map, MazeFillType.PATH);
				
				if(maxNeighbor == null) {
					continue;
				}
				
				int maxNeighborsWallHeight = map.getWallHeight(maxNeighbor);
		
				if(maxNeighborsWallHeight <= 0) {
					continue;
				}
				
				int mazeHeight = map.getGroundHeight(x, z),
					maxNeighborsGroundHeight = map.getGroundHeight(maxNeighbor);
				
				if(mazeHeight < maxNeighborsGroundHeight + wallHeight) {
					map.setMazeHeight(x, z, maxNeighborsGroundHeight + wallHeight);
				}
			}
		}
	}
	
	protected Vec2 getHeighestNeighbor(int x, int z, BuildMap map, MazeFillType limitation) {
		
		Vec2 maxNeighbor = null;
		int maxHeight = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.toVec2());
			
			if(neighbor.getIntX() < 0 || neighbor.getIntX() >= map.getDimX() ||
			   neighbor.getIntZ() < 0 || neighbor.getIntZ() >= map.getDimZ()) {
				continue;
			}
			
			if(map.getType(neighbor) == MazeFillType.NOT_MAZE || limitation != null &&
			   map.getType(neighbor) != limitation) {
				continue;
			}
			
			int neighborHeight = map.getMazeHeight(neighbor);
			
			if(maxNeighbor == null || neighborHeight > maxHeight) {
				maxNeighbor = neighbor;
				maxHeight = neighborHeight;
			}
		}
		
		return maxNeighbor;
	}

	protected int getNeighborGroundHeightDiff(BuildMap map, int x, int z) {
		
		int groundHeight = map.getGroundHeight(x, z),
			heightDiff = 0,
			neighborsCount = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.toVec2());
			
			if(neighbor.getIntX() < 0 || neighbor.getIntX() >= map.getDimX() ||
			   neighbor.getIntZ() < 0 || neighbor.getIntZ() >= map.getDimZ() ||
				map.getType(neighbor) == MazeFillType.NOT_MAZE) {
				continue;
			}
			
			heightDiff += map.getGroundHeight(neighbor) - groundHeight;
			neighborsCount++;
		}
		
		return heightDiff / neighborsCount;
	}
}