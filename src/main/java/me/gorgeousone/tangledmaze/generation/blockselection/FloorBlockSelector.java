package me.gorgeousone.tangledmaze.generation.blockselection;

import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;

public class FloorBlockSelector extends AbstractBlockSelector {


	@Override
	public Set<BlockState> getRelevantBlocks(TerrainMap terrainMap) {

		Set<BlockState> relevantBlocks = new HashSet<>();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if(terrainMap.getAreaType(x, z) != MazeAreaType.NOT_MAZE) {
					Block block = new Location(terrainMap.getMaze().getWorld(), x, terrainMap.getFloorHeight(x, z), z).getBlock();
					relevantBlocks.add(block.getState());
				}
			}
		}

		return relevantBlocks;
	}
}
