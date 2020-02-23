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
	
		
	}
	
	public static void copyPathsOnTerrainMap(TerrainMap terrainMap, PathMap pathMap) {
		
		for (int gridX = 0; gridX < pathMap.getGridWidth(); gridX++) {
			for (int gridZ = 0; gridZ < pathMap.getGridHeight(); gridZ++) {
				
				Vec2 start = pathMap.getSegmentStart(gridX, gridZ);
				System.out.println(start.toString() + " " + pathMap.getPathAreaType(gridX, gridZ));
				
				if (pathMap.getPathAreaType(gridX, gridZ) == PathAreaType.PATH)
					mapSegment(
							terrainMap,
							pathMap.getSegmentStart(gridX, gridZ),
							pathMap.getSegmentSize(gridX, gridZ),
							MazeAreaType.PATH);
			}
		}
	}
	
	private static void mapSegment(TerrainMap terrainMap, Vec2 segment, Vec2 segmentSize, MazeAreaType mazeAreaType) {
		
		for (int x = 0; x < segmentSize.getX(); x++) {
			for (int z = 0; z < segmentSize.getZ(); z++) {
				
				MazeAreaType type = terrainMap.getAreaType(segment.getX() + x, segment.getZ() + z);
				//				System.out.println(segment.getX() + x + ", " + (segment.getZ() + z) + " " + type);
				
				//TODO remove this condition when the PathMap has full access to the maze shape data
				if (type != MazeAreaType.NOT_MAZE && type != MazeAreaType.WALL)
					terrainMap.setType(segment.getX() + x, segment.getZ() + z, mazeAreaType);
			}
		}
	}
}