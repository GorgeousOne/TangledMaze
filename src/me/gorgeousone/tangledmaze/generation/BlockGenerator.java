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

	public void generateBlocks(BuildMap buildMap) {
		
		cullTrees(buildMap);
		raiseTooLowWalls(buildMap);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				updateBlocksContinuously(getMazeWallBlocks(buildMap));
			}
		}.runTask(TangledMain.getInstance());
	}
	
	//lowers wall heights at points where spikes of wall would stick out of the maze
	private void cullTrees(BuildMap buildMap) {
		
		int wallHeight = buildMap.getMaze().getWallHeight();

		for(int x = buildMap.getMinX(); x < buildMap.getMaxX(); x++) {
			for(int z = buildMap.getMinZ(); z < buildMap.getMaxZ(); z++) {
				
				if(buildMap.getType(x, z) == MazeFillType.NOT_MAZE)
					continue;
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, buildMap, null);
				
				int mazeHeight = buildMap.getMazeHeight(x, z);
				int defaultMazeHeight = buildMap.getGroundHeight(maxNeighbor) + wallHeight;
				
				if(mazeHeight <= defaultMazeHeight)
					continue;
				
				int groundDiffToNeighbors = getGroundDiffToNeighbors(buildMap, x, z);
				
				//adapt ground height of path points to surrounding ground height
				if(buildMap.getType(x, z) == MazeFillType.PATH)
					buildMap.setGroundHeight(x, z, buildMap.getGroundHeight(x, z) + groundDiffToNeighbors);
				//adapt wall height of wall points to default wall height or neighbor wall heights
				else
					buildMap.setMazeHeight(x, z, Math.min(defaultMazeHeight, mazeHeight + groundDiffToNeighbors));
			}
		}
	}
	
	//raises walls with a low height to surrounding paths
	private void raiseTooLowWalls(BuildMap buildMap) {
		
		int wallHeight = buildMap.getMaze().getWallHeight();

		for(int x = buildMap.getMinX(); x < buildMap.getMaxX(); x++) {
			for(int z = buildMap.getMinZ(); z < buildMap.getMaxZ(); z++) {
				
				if(buildMap.getType(x, z) == MazeFillType.NOT_MAZE)
					continue;
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, buildMap, MazeFillType.PATH);
				
				if(maxNeighbor == null)
					continue;
				
				int maxNeighborsWallHeight = buildMap.getWallHeight(maxNeighbor);
		
				if(maxNeighborsWallHeight <= 0)
					continue;
				
				int mazeHeight = buildMap.getMazeHeight(x, z),
					maxNeighborsGroundHeight = buildMap.getGroundHeight(maxNeighbor);
				
				if(mazeHeight < maxNeighborsGroundHeight + wallHeight)
					buildMap.setMazeHeight(x, z, maxNeighborsGroundHeight + wallHeight);
			}
		}
	}
	
	private void updateBlocksContinuously(List<BlockState> blocksToUpdate) {
		
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
	
	private List<BlockState> getMazeWallBlocks(BuildMap buildMap) {
		
		Maze maze = buildMap.getMaze();
		Random rnd = new Random();
		
		List<Material> wallMaterials = maze.getWallMaterials();
		List<BlockState> blocksToUpdate = new ArrayList<>();
		
		for(int x = buildMap.getMinX(); x < buildMap.getMaxX(); x++) {
			for(int z = buildMap.getMinZ(); z < buildMap.getMaxZ(); z++) {
				
				if(buildMap.getType(x, z) != MazeFillType.WALL)
					continue;
				
				for(int height = buildMap.getGroundHeight(x, z) + 1; height <= buildMap.getMazeHeight(x, z); height++) {
					
					BlockState block = new Location(maze.getWorld(), x, height, z).getBlock().getState();
					
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
	
	private Vec2 getHeighestNeighbor(int x, int z, BuildMap buildMap, MazeFillType limitation) {
		
		Vec2 maxNeighbor = null;
		int maxHeight = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.toVec2());
			
			if(!buildMap.contains(neighbor))
				continue;
			
			if(buildMap.getType(neighbor) == MazeFillType.NOT_MAZE || limitation != null &&
			   buildMap.getType(neighbor) != limitation) {
				continue;
			}
			
			int neighborHeight = buildMap.getMazeHeight(neighbor);
			
			if(maxNeighbor == null || neighborHeight > maxHeight) {
				maxNeighbor = neighbor;
				maxHeight = neighborHeight;
			}
		}
		
		return maxNeighbor;
	}

	private int getGroundDiffToNeighbors(BuildMap buildMap, int x, int z) {
		
		int groundHeight = buildMap.getGroundHeight(x, z);
		int heightDiff = 0;
		int neighborsCount = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.toVec2());
			
			if(!buildMap.contains(neighbor) || buildMap.getType(neighbor) == MazeFillType.NOT_MAZE)
				continue;
			
			heightDiff += buildMap.getGroundHeight(neighbor) - groundHeight;
			neighborsCount++;
		}
		
		return heightDiff / neighborsCount;
	}
}