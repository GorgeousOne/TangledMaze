package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class RoofBlockLocator extends AbstractBlockLocator {
	
	@Override
	public Set<LocatedBlockData> locateBlocks(TerrainMap terrainMap) {
		
		Set<LocatedBlockData> relevantBlocks = new HashSet<>();
		int roofWidth = terrainMap.getDimension(MazeDimension.ROOF_WIDTH);
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
					continue;
				
				int roofHeight = terrainMap.getRoofHeight(x, z);
				int maxSurroundingRoofHeight = getHighestSurroundingRoof(x, z, terrainMap);
				
				for (int y = roofHeight + 1; y < maxSurroundingRoofHeight + 1 + roofWidth; y++) {
					Block block = new Location(terrainMap.getWorld(), x, y, z).getBlock();
					
					if (!BlockUtils.isReallySolid(block.getType()))
						relevantBlocks.add(new LocatedBlockData(block));
				}
			}
		}
		return relevantBlocks;
	}
	
	protected int getHighestSurroundingRoof(int x, int z, TerrainMap terrainMap) {
		
		int maxRoofHeight = terrainMap.getRoofHeight(x, z);
		
		for (Direction dir : Direction.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if (!terrainMap.contains(neighbor))
				continue;
			
			int neighborRoofHeight = terrainMap.getRoofHeight(neighbor);
			
			if (neighborRoofHeight > maxRoofHeight)
				maxRoofHeight = neighborRoofHeight;
		}
		
		return maxRoofHeight;
	}
}
