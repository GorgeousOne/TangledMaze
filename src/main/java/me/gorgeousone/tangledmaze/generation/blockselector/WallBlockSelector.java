package me.gorgeousone.tangledmaze.generation.blockselector;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class WallBlockSelector extends AbstractBlockSelector {
	
	public Set<BlockDataState> getBlocks(TerrainMap terrainMap) {
		
		Set<BlockDataState> relevantBlocks = new HashSet<>();
		Maze maze = terrainMap.getMaze();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;
				
				int floorHeight = terrainMap.getFloorHeight(x, z);
				
				for (int relHeight = 0; relHeight <= terrainMap.getWallHeight(x, z); relHeight++) {
					Block block = new Location(maze.getWorld(), x, floorHeight + relHeight, z).getBlock();
					
					if (!BlockUtils.isReallySolid(block.getType()))
						relevantBlocks.add(new BlockDataState(block));
				}
			}
		}
		
		return relevantBlocks;
	}
}
