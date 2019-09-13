package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;

public class TerrainEditor {
	
	public void editTerrain(TerrainMap terrainMap) {
		
		cullTrees(terrainMap);
		raiseTooLowWalls(terrainMap);
	}
	
	//lowers wall heights at points where spikes of wall would stick out of the maze
	private void cullTrees(TerrainMap terrainMap) {
		
		int wallHeight = terrainMap.getMaze().getWallHeight();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				if(terrainMap.getType(x, z) == MazeAreaType.NOT_MAZE)
					continue;
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, terrainMap, null);
				
				int mazeHeight = terrainMap.getMazeHeight(x, z);
				int defaultMazeHeight = terrainMap.getGroundHeight(maxNeighbor) + wallHeight;
				
				if(mazeHeight <= defaultMazeHeight)
					continue;
				
				int groundDiffToNeighbors = getGroundDiffToNeighbors(terrainMap, x, z);
				
				//adapt ground height of path points to surrounding ground height
				if(terrainMap.getType(x, z) == MazeAreaType.PATH)
					terrainMap.setGroundHeight(x, z, terrainMap.getGroundHeight(x, z) + groundDiffToNeighbors);
				//adapt wall height of wall points to default wall height or neighbor wall heights
				else
					terrainMap.setMazeHeight(x, z, Math.min(defaultMazeHeight, mazeHeight + groundDiffToNeighbors));
			}
		}
	}
	
	//raises walls with a low height to surrounding paths
	private void raiseTooLowWalls(TerrainMap terrainMap) {
		
		int wallHeight = terrainMap.getMaze().getWallHeight();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				if(terrainMap.getType(x, z) == MazeAreaType.NOT_MAZE)
					continue;
				
				Vec2 maxNeighbor = getHeighestNeighbor(x, z, terrainMap, MazeAreaType.PATH);
				
				if(maxNeighbor == null)
					continue;
				
				int maxNeighborsWallHeight = terrainMap.getWallHeight(maxNeighbor);
		
				if(maxNeighborsWallHeight <= 0)
					continue;
				
				int mazeHeight = terrainMap.getMazeHeight(x, z),
					maxNeighborsGroundHeight = terrainMap.getGroundHeight(maxNeighbor);
				
				if(mazeHeight < maxNeighborsGroundHeight + wallHeight)
					terrainMap.setMazeHeight(x, z, maxNeighborsGroundHeight + wallHeight);
			}
		}
	}
	private Vec2 getHeighestNeighbor(int x, int z, TerrainMap terrainMap, MazeAreaType limitation) {
		
		Vec2 maxNeighbor = null;
		int maxHeight = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if(!terrainMap.contains(neighbor))
				continue;
			
			if(terrainMap.getType(neighbor) == MazeAreaType.NOT_MAZE || limitation != null &&
			   terrainMap.getType(neighbor) != limitation) {
				continue;
			}
			
			int neighborHeight = terrainMap.getMazeHeight(neighbor);
			
			if(maxNeighbor == null || neighborHeight > maxHeight) {
				maxNeighbor = neighbor;
				maxHeight = neighborHeight;
			}
		}
		
		return maxNeighbor;
	}

	private int getGroundDiffToNeighbors(TerrainMap terrainMap, int x, int z) {
		
		int groundHeight = terrainMap.getGroundHeight(x, z);
		int heightDiff = 0;
		int neighborsCount = 0;
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = new Vec2(x, z).add(dir.getVec2());
			
			if(!terrainMap.contains(neighbor) || terrainMap.getType(neighbor) == MazeAreaType.NOT_MAZE)
				continue;
			
			heightDiff += terrainMap.getGroundHeight(neighbor) - groundHeight;
			neighborsCount++;
		}
		
		return heightDiff / neighborsCount;
	}
}
