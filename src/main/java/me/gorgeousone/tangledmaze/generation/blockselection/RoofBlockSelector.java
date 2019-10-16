package me.gorgeousone.tangledmaze.generation.blockselection;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;

public class RoofBlockSelector extends AbstractBlockSelector{

	@Override
	public Set<BlockState> getRelevantBlocks(TerrainMap terrainMap) {

		Set<BlockState> relevantBlocks = new HashSet<>();
		Maze maze = terrainMap.getMaze();
		int roofWidth = maze.getDimension(MazeDimension.ROOF_WIDTH);

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if(terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
					continue;

				int roofHeight = terrainMap.getRoofHeight(x, z);
				int maxSurroundingRoofHeight = getHighestSurroundingRoof(x, z, terrainMap);

				for(int y = roofHeight+1; y < maxSurroundingRoofHeight+1 + roofWidth; y++) {
					Block block = new Location(maze.getWorld(), x, y, z).getBlock();
					relevantBlocks.add(block.getState());
				}
			}
		}
		return relevantBlocks;
	}

	protected int getHighestSurroundingRoof(int x, int z, TerrainMap terrainMap) {

		int maxRoofHeight = terrainMap.getRoofHeight(x, z);

		for(Directions dir : Directions.values()) {

			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());

			if(!terrainMap.contains(neighbor))
				continue;

			int neighborRoofHeight = terrainMap.getRoofHeight(neighbor);

			if(neighborRoofHeight > maxRoofHeight)
				maxRoofHeight = neighborRoofHeight;
		}

		return maxRoofHeight;
	}
}