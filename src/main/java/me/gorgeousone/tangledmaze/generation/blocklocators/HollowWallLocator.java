package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class HollowWallLocator extends AbstractBlockLocator {
	
	@Override
	public Set<LocatedBlockData> locateBlocks(TerrainMap terrainMap) {
		
		Set<LocatedBlockData> wallBlocks = new HashSet<>();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;
				
				int roofHeight = terrainMap.getRoofHeight(x, z);
				int startHeight = isSurfaceWall(x, z, terrainMap) ?
						terrainMap.getFloorHeight(x, z) + 1 : getLowestNeighborRoof(x, z, terrainMap);
				
				for (int y = startHeight; y <= roofHeight; y++) {
					Block block = new Location(terrainMap.getWorld(), x, y, z).getBlock();
					
					if (!BlockUtils.isReallySolid(block.getType()))
						wallBlocks.add(new LocatedBlockData(block));
				}
			}
		}
		
		return wallBlocks;
	}
	
	private boolean isSurfaceWall(int x, int z, TerrainMap terrainMap) {
		
		for (Direction dir : Direction.values()) {
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if (!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) != MazeAreaType.WALL)
				return true;
		}
		return false;
	}

	private int getLowestNeighborRoof(int x, int z, TerrainMap terrainMap) {

		int minNeighborRoofHeight = terrainMap.getRoofHeight(x, z);

		for (Direction dir : Direction.values()) {

			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			int neighborWallHeight = terrainMap.getRoofHeight(neighbor);

			if (neighborWallHeight < minNeighborRoofHeight)
				minNeighborRoofHeight = neighborWallHeight;
		}
		return minNeighborRoofHeight;
	}
}
