package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;

public class TerrainEditor {
	
	public void editTerrain(TerrainMap terrainMap) {
		
		cullSpikes2(terrainMap);
//		raiseTooLowWalls(terrainMap);
	}
	
	private void cullSpikes2(TerrainMap terrainMap) {
		
//		int wallHeight = terrainMap.getMaze().getWallHeight();
		
		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				int floorHeight = terrainMap.getFloorHeight(x, z);
				int maxNeighborFloorHeight = getHighestNeighborFloor(x, z, terrainMap);
				
				if(floorHeight >= maxNeighborFloorHeight + 2)
					terrainMap.setFloorHeight(x, z, floorHeight + getAverageHeightDiffToNeighborFloors(terrainMap, x, z));
			}
		}
	}
	
	private int getHighestNeighborFloor(int x, int z, TerrainMap terrainMap) {
		
		int maxHeight = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if(!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) == MazeAreaType.NOT_MAZE)
				continue;
			
			int neighborHeight = terrainMap.getFloorHeight(neighbor);
			
			if(neighborHeight > maxHeight) {
				maxHeight = neighborHeight;
			}
		}

		return maxHeight;
	}
	
	/*
	 * This method changes the height of the fllor or of the ceiling in 2 cases:
	 * 
	 * 1. If the floor height differs in average to neighbor floor heights the method will try to smooth
	 *    the floor height at that point
	 *    
	 * 2. If the ceiling height of a point
	 */
//	private void cullSpikes(TerrainMap terrainMap) {
//		
//		int wallHeight = terrainMap.getMaze().getWallHeight();
//
//		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
//			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
//				
//				if(terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
//					continue;
//				
//				Vec2 maxCeilHeightNeighbor = getHighestNeighborCeil(x, z, terrainMap, null);
//				
//				int ceilHeight = terrainMap.getCeilHeight(x, z);
//				int defaultCeilHeight = terrainMap.getFloorHeight(maxCeilHeightNeighbor) + wallHeight;
//				
//				
//				if(ceilHeight <= defaultCeilHeight)
//					continue;
//				
//				int averageFloorDiffToNeighbors = getAverageFloorHeightDiffToNeighbors(terrainMap, x, z);
//				
//				//adapt floor height of path locations to surrounding floor heights
//				if(terrainMap.getAreaType(x, z) == MazeAreaType.PATH)
//					terrainMap.setFloorHeight(x, z, terrainMap.getFloorHeight(x, z) + averageFloorDiffToNeighbors);
//				
//				//adapt wall height of wall points to default wall height or neighbor wall heights
//				else
//					terrainMap.setCeilHeight(x, z, Math.min(defaultCeilHeight, ceilHeight + averageFloorDiffToNeighbors));
//			}
//		}
//	}
	
	//raises walls with a low height to surrounding paths
//	private void raiseTooLowWalls(TerrainMap terrainMap) {
//		
//		int wallHeight = terrainMap.getMaze().getWallHeight();
//
//		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
//			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
//				
//				if(terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
//					continue;
//				
//				Vec2 maxNeighbor = getHighestNeighborCeil(x, z, terrainMap, MazeAreaType.PATH);
//				
//				if(maxNeighbor == null)
//					continue;
//				
//				int maxNeighborsWallHeight = terrainMap.getWallHeight(maxNeighbor);
//		
//				if(maxNeighborsWallHeight <= 0)
//					continue;
//				
//				int mazeHeight = terrainMap.getCeilHeight(x, z),
//					maxNeighborsGroundHeight = terrainMap.getFloorHeight(maxNeighbor);
//				
//				if(mazeHeight < maxNeighborsGroundHeight + wallHeight)
//					terrainMap.setCeilHeight(x, z, maxNeighborsGroundHeight + wallHeight);
//			}
//		}
//	}
	
	
	private Vec2 getHighestNeighborCeil(int x, int z, TerrainMap terrainMap, MazeAreaType areaTypeLimit) {
		
		Vec2 maxNeighbor = null;
		int maxHeight = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if(!terrainMap.contains(neighbor))
				continue;
			
			if(terrainMap.getAreaType(neighbor) == MazeAreaType.NOT_MAZE || areaTypeLimit != null &&
			   terrainMap.getAreaType(neighbor) != areaTypeLimit) {
				continue;
			}
			
			int neighborHeight = terrainMap.getCeilHeight(neighbor);
			
			if(maxNeighbor == null || neighborHeight > maxHeight) {
				maxNeighbor = neighbor;
				maxHeight = neighborHeight;
			}
		}
		
		return maxNeighbor;
	}

	private int getAverageHeightDiffToNeighborFloors(TerrainMap terrainMap, int x, int z) {
		
		int floorHeight = terrainMap.getFloorHeight(x, z);
		int heightDiff = 0;
		int neighborsCount = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if(!terrainMap.contains(neighbor) || terrainMap.getAreaType(neighbor) == MazeAreaType.NOT_MAZE)
				continue;
			
			heightDiff += terrainMap.getFloorHeight(neighbor) - floorHeight;
			neighborsCount++;
		}
		
		return heightDiff / neighborsCount;
	}
}
