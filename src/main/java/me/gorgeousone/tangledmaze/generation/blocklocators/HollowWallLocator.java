package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.BlockDataState;
import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class HollowWallLocator extends AbstractBlockLocator {
	
	@Override
	public Set<BlockDataState> locateBlocks(TerrainMap terrainMap) {
		
		Set<BlockDataState> relevantBlocks = new HashSet<>();
		Maze maze = terrainMap.getMaze();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;
				
				int floorHeight = terrainMap.getFloorHeight(x, z);
				int wallHeight = terrainMap.getWallHeight(x, z);
				int increment = isSurfaceWall(new Vec2(x, z), terrainMap) ? 1 : wallHeight - 1;
				
				for (int relHeight = 1; relHeight <= wallHeight; relHeight += increment) {
					
					Block block = new Location(maze.getWorld(), x, floorHeight + relHeight, z).getBlock();
					
					if (!BlockUtils.isReallySolid(block.getType()))
						relevantBlocks.add(new BlockDataState(block));
				}
				
//				if(increment != 1 && terrainMap.getWallHeight(x, z) > wallHeight) {
//
//					for(int height = wallHeight; height < terrainMap.getWallHeight(x, z); height++) {
//
//						Block block = new Location(maze.getWorld(), x, floorHeight + height, z).getBlock();
//
//						if (!BlockUtils.isReallySolid(block.getType()))
//							relevantBlocks.add(new BlockDataState(block));
//					}
//				}
			}
		}
		
		return relevantBlocks;
	}
	
//	protected Vec2 getLowestNeighborWall(int x, int z, TerrainMap terrainMap) {
//
//		Vec2 minNeighborWall = null;
//		int minNeighborWallHeight = terrainMap.getRoofHeight(x, z);
//
//		for (Direction dir : Direction.values()) {
//
//			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
//
//			if (!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) != MazeAreaType.WALL)
//				continue;
//
//			int neighborWallHeight = terrainMap.getWallHeight(neighbor);
//
//			if (neighborWallHeight < minNeighborWallHeight) {
//
//				minNeighborWall = neighbor;
//				minNeighborWallHeight = neighborWallHeight;
//			}
//		}
//		return minNeighborWall;
//	}
	
	private boolean isSurfaceWall(Vec2 point, TerrainMap terrainMap) {
		
		for (Direction dir : Direction.values()) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) != MazeAreaType.WALL)
				return true;
		}
		return false;
	}
}
