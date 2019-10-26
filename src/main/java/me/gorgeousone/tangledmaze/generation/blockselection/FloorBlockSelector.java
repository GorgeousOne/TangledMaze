package me.gorgeousone.tangledmaze.generation.blockselection;

import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;
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

				if(terrainMap.getAreaType(x, z) != MazeAreaType.PATH)
					continue;

				int floorHeight = terrainMap.getFloorHeight(x, z);
				int minSurroundingFloorHeight = getLowestSurroundingFloorHeight(x, z, terrainMap);

				for(int y = minSurroundingFloorHeight; y <= floorHeight; y++) {
					Block block = new Location(terrainMap.getMaze().getWorld(), x, y, z).getBlock();
					relevantBlocks.add(block.getState());
				}
			}
		}

		return relevantBlocks;
	}

	protected int getLowestSurroundingFloorHeight(int x, int z, TerrainMap terrainMap) {

		int minFloorHeight = terrainMap.getFloorHeight(x, z);

		for(Directions dir : Directions.values()) {

			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());

			if(!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) == MazeAreaType.NOT_MAZE)
				continue;

			int neighborFloorHeight = terrainMap.getFloorHeight(neighbor);

			if(neighborFloorHeight < minFloorHeight)
				minFloorHeight = neighborFloorHeight;
		}

		return minFloorHeight;
	}
}
