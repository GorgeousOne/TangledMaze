package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class WallBlockLocator extends AbstractBlockLocator {
	
	public Set<LocatedBlockData> locateBlocks(TerrainMap terrainMap) {
		
		Set<LocatedBlockData> relevantBlocks = new HashSet<>();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;
				
				int floorHeight = terrainMap.getFloorHeight(x, z);
				
				for (int relHeight = 0; relHeight <= terrainMap.getWallHeight(x, z); relHeight++) {
					Block block = new Location(terrainMap.getWorld(), x, floorHeight + relHeight, z).getBlock();
					
					if (!BlockUtils.isReallySolid(block.getType()))
						relevantBlocks.add(new LocatedBlockData(block));
				}
			}
		}
		
		return relevantBlocks;
	}
}
