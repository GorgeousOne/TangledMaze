package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Vec2;

public final class PathMapFactory {
	
	private PathMapFactory() {}
	
	public static PathMap createPathMapOf(TerrainMap terrainMap, Vec2 pathStart, int pathWidth, int wallWidth) {
		
		if (pathStart == null)
			throw new IllegalArgumentException("Please generate (the main) exits for this maze first.");
		
		PathMap pathMap = new PathMap(terrainMap.getMinimum(), terrainMap.getMaximum(), pathStart, pathWidth, wallWidth);
		
		copyMazeOnPathMap(terrainMap, pathMap);
		return pathMap;
	}
	
	private static void copyMazeOnPathMap(TerrainMap terrainMap, PathMap pathMap) {
		
		for (int gridX = 0; gridX < pathMap.getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < pathMap.getGridHeight(); gridZ++) {
				
				if (gridX % 2 == 0 || gridZ % 2 == 0)
					markObstaclesInPathMap(terrainMap, pathMap, gridX, gridZ);
			}
		}
	}
	
	private static void markObstaclesInPathMap(TerrainMap terrainMap, PathMap pathMap, int gridX, int gridZ) {
		
		for (Vec2 fill : pathMap.getGridCell(gridX, gridZ).getFill()) {
			
			if (terrainMap.getAreaType(fill) != MazeAreaType.UNDEFINED) {
				pathMap.setGridCellType(gridX, gridZ, PathAreaType.BLOCKED);
				return;
			}
		}
	}
	
	public static void copyPathsOntoTerrainMap(TerrainMap terrainMap, PathMap pathMap) {
		
		for (int gridX = 0; gridX < pathMap.getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < pathMap.getGridHeight(); gridZ++) {
				
				if (pathMap.getGridCellType(gridX, gridZ) == PathAreaType.PATH)
					terrainMap.mapSegment(pathMap.getGridCell(gridX, gridZ), MazeAreaType.PATH);
			}
		}
	}
}