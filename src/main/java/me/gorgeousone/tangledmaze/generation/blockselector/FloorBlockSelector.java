package me.gorgeousone.tangledmaze.generation.blockselector;

import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class FloorBlockSelector extends AbstractBlockSelector {

	@Override
	public Set<BlockDataState> getBlocks(TerrainMap terrainMap) {

		Set<BlockDataState> relevantBlocks = new HashSet<>();

		for (int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if (terrainMap.getAreaType(x, z) == MazeAreaType.PATH) {
					Location blockLoc = new Location(terrainMap.getMaze().getWorld(), x, terrainMap.getFloorHeight(x, z), z);
					relevantBlocks.add(new BlockDataState(blockLoc));
				}
			}
		}

		return relevantBlocks;
	}
}
