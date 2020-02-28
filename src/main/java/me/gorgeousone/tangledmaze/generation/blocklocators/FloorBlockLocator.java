package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class FloorBlockLocator extends AbstractBlockLocator {
	
	@Override
	public Set<BlockDataState> locateBlocks(TerrainMap terrainMap) {
		
		Set<BlockDataState> relevantBlocks = new HashSet<>();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) == MazeAreaType.PATH) {
					Location blockLoc = new Location(terrainMap.getMaze().getWorld(), x, terrainMap.getFloorHeight(x, z), z);
					relevantBlocks.add(new BlockDataState(blockLoc));
				}
			}
		}
		
		return relevantBlocks;
	}
}
