package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class FloorBlockLocator extends AbstractBlockLocator {
	
	@Override
	public Set<LocatedBlockData> locateBlocks(TerrainMap terrainMap) {
		
		Set<LocatedBlockData> floorBlocks = new HashSet<>();
		
		for (int x = terrainMap.getMinX(); x <= terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z <= terrainMap.getMaxZ(); z++) {
				
				if (terrainMap.getAreaType(x, z) != MazeAreaType.PATH)
					continue;
				
				int lowestNeighborFloorPath = getLowestNeighborFloorPath(x, z, terrainMap);
				
				for(int y = lowestNeighborFloorPath; y <= terrainMap.getFloorHeight(x, z); y++) {
					Location blockLoc = new Location(terrainMap.getWorld(), x, y, z);
					floorBlocks.add(new LocatedBlockData(blockLoc));
				}
			}
		}
		
		return floorBlocks;
	}
	
	protected int getLowestNeighborFloorPath(int x, int z, TerrainMap terrainMap) {
		
		int minNeighborFloor = terrainMap.getFloorHeight(x, z);
		
		for (Direction dir : Direction.values()) {
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if (!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) != MazeAreaType.PATH)
				continue;
			
			int neighborFloorHeight = terrainMap.getFloorHeight(neighbor);
			
			if (neighborFloorHeight < minNeighborFloor)
				minNeighborFloor = neighborFloorHeight;
		}
		return minNeighborFloor;
	}
}
