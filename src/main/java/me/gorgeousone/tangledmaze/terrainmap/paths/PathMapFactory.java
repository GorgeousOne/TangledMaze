package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.terrainmap.MazeAreaType;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.AbstractMap;
import java.util.Map;

public final class PathMapFactory {
	
	private PathMapFactory() {
	}
	
	public static PathMap createPathMap(TerrainMap terrainMap) {
		
		Vec2 pathStart = terrainMap.getPathStart();
		
		if (pathStart == null)
			throw new IllegalArgumentException("Please generate (the main) exits for this maze first.");
		
		Maze maze = terrainMap.getMaze();
		
		int pathWidth = maze.getDimension(MazeDimension.PATH_WIDTH);
		int wallWidth = maze.getDimension(MazeDimension.WALL_WIDTH);
		
		Map.Entry<Vec2, Vec2> clipBounds = calculateClipBounds(maze.getClip());
		PathMap pathMap = new PathMap(clipBounds.getKey(), clipBounds.getValue(), pathStart, pathWidth, wallWidth);
		
		copyMazeOnPathMap(terrainMap, pathMap);
		return pathMap;
	}
	
	private static Map.Entry<Vec2, Vec2> calculateClipBounds(Clip clip) {
		
		Vec2 min = null;
		Vec2 max = null;
		
		for (Vec2 point : clip.getFill()) {
			
			if (min == null) {
				min = point.clone();
				max = point.clone();
				continue;
			}
			
			if (point.getX() < min.getX())
				min.setX(point.getX());
			else if (point.getX() > max.getX())
				max.setX(point.getX());
			
			if (point.getZ() < min.getZ())
				min.setZ(point.getZ());
			else if (point.getZ() > max.getZ())
				max.setZ(point.getZ());
		}
		
		return new AbstractMap.SimpleEntry<>(min, max);
	}
	
	private static void copyMazeOnPathMap(TerrainMap terrainMap, PathMap pathMap) {
	
	}
	
	public  static void copyPathsOnTerrainMap(TerrainMap terrainMap, PathMap pathMap) {
	
		for(int gridX = 0; gridX < pathMap.getGridWidth(); gridX++) {
			for(int gridZ = 0; gridZ < pathMap.getGridHeight(); gridZ++) {
				
				if(pathMap.getPathAreaType(gridX, gridZ) != PathAreaType.PATH)
					continue;
				
				mapPath(terrainMap, pathMap.getSegmentStart(gridX, gridZ), pathMap.getSegmentSize(gridX, gridZ));
			}
		}
	}
	
	private static void mapPath(TerrainMap terrainMap, Vec2 pathStart, Vec2 pathSize) {
		
		for(int x = 0; x < pathSize.getX(); x++) {
			for(int z = 0; z < pathSize.getZ(); z++) {
				terrainMap.setType(pathStart.getX() + x, pathStart.getZ() + z, MazeAreaType.PATH);
			}
		}
	}
}