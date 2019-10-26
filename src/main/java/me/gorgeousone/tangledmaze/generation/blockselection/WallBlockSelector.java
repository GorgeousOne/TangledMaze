package me.gorgeousone.tangledmaze.generation.blockselection;

import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;

public class WallBlockSelector extends AbstractBlockSelector {

	public Set<BlockState> getRelevantBlocks(TerrainMap terrainMap) {

		Set<BlockState> relevantBlocks = new HashSet<>();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if(terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;

				int floorHeight = terrainMap.getFloorHeight(x, z);

				for(int relHeight = 0; relHeight <= terrainMap.getWallHeight(x, z); relHeight++) {

					Block block = new Location(terrainMap.getMaze().getWorld() , x, floorHeight + relHeight, z).getBlock();

					if(Utils.canBeOverbuild(block.getType()))
						relevantBlocks.add(block.getState());
				}
			}
		}

		return relevantBlocks;
	}
}
