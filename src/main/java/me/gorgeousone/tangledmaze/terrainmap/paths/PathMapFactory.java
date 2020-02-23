package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.Map;

public final class PathMapFactory {
	
	private PathMapFactory() {}
	
	public static PathMap createPathMap(TerrainMap terrainMap) {
		
		Vec2 pathStart = terrainMap.getPathStart();
		
		if (pathStart == null)
			throw new IllegalArgumentException("Please generate (the main) exits for this maze first.");
		
		Maze maze = terrainMap.getMaze();
		
		int pathWidth = maze.getDimension(MazeDimension.PATH_WIDTH);
		int wallWidth = maze.getDimension(MazeDimension.WALL_WIDTH);
		
		Map.Entry<Vec2, Vec2> clipBounds = Utils.calculateClipBounds(maze.getClip());
		PathMap pathMap = new PathMap(clipBounds.getKey(), clipBounds.getValue(), pathStart, pathWidth, wallWidth);
		
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
		
		for (Vec2 fill : pathMap.getSegment(gridX, gridZ).getFill()) {
			
			if (terrainMap.getAreaType(fill) != MazeAreaType.UNDEFINED) {
				pathMap.setPathAreaType(gridX, gridZ, PathAreaType.BLOCKED);
				return;
			}
		}
	}
	
	public static void copyPathsOnTerrainMap(TerrainMap terrainMap, PathMap pathMap) {
		
		for (int gridX = 0; gridX < pathMap.getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < pathMap.getGridHeight(); gridZ++) {
				
				if (pathMap.getPathAreaType(gridX, gridZ) == PathAreaType.PATH)
					mapSegment(
							terrainMap,
							pathMap.getSegment(gridX, gridZ),
							MazeAreaType.PATH);
			}
		}
	}
	
	private static void mapSegment(TerrainMap terrainMap, NewPathSegment segment, MazeAreaType mazeAreaType) {
		
		for (Vec2 fill : segment.getFill())
			terrainMap.setType(fill, mazeAreaType);
	}
}