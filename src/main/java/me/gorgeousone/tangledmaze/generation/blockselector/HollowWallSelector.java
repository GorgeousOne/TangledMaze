package me.gorgeousone.tangledmaze.generation.blockselector;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.BlockDataState;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class HollowWallSelector extends AbstractBlockSelector{

	@Override
	public Set<BlockDataState> getBlocks(TerrainMap terrainMap) {

		Set<BlockDataState> relevantBlocks = new HashSet<>();
		Maze maze = terrainMap.getMaze();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if(terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;

				int floorHeight = terrainMap.getFloorHeight(x, z);
				int wallHeight = terrainMap.getWallHeight(x, z);
				int increment = isSurfaceWall(new Vec2(x, z), terrainMap) ? 1 : wallHeight-1;

				for(int relHeight = 1; relHeight <= wallHeight; relHeight += increment) {

					Block block = new Location(maze.getWorld() , x, floorHeight + relHeight, z).getBlock();

					if(Utils.canBeOverbuild(block.getType()))
						relevantBlocks.add(new BlockDataState(block));
				}
			}
		}

		return relevantBlocks;
	}

	private boolean isSurfaceWall(Vec2 point, TerrainMap terrainMap) {

		for(Directions dir : Directions.values()) {
			Vec2 neighbor = point.clone().add(dir.getVec2());

			if(!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) != MazeAreaType.WALL)
				return true;
		}
		return false;
	}
}
