package me.gorgeousone.tangledmaze.generation.terrainmap;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.generation.pathmap.ExitSegment;
import me.gorgeousone.tangledmaze.generation.pathmap.ExitSegmentFactory;
import me.gorgeousone.tangledmaze.generation.pathmap.PathGenerator;
import me.gorgeousone.tangledmaze.generation.pathmap.PathMap;
import me.gorgeousone.tangledmaze.generation.pathmap.PathMapFactory;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.AbstractMap;
import java.util.Map;

public final class TerrainMapFactory {
	
	private TerrainMapFactory() {}
	
	public static TerrainMap createTerrainMapOf(Maze maze) {
		
		Clip mazeClip = maze.getClip();
		Map.Entry<Vec2, Vec2> clipBounds = calculateClipBounds(mazeClip);
		
		TerrainMap terrainMap = new TerrainMap(clipBounds.getKey(), clipBounds.getValue(), maze);
		copyClipOntoMap(mazeClip, terrainMap, maze.getDimension(MazeDimension.WALL_HEIGHT));
		
		return terrainMap;
	}
	
	private static void copyClipOntoMap(Clip clip, TerrainMap terrainMap, int wallHeight) {
		
		for (Vec2 point : clip.getFill()) {
			terrainMap.setAreaType(point, MazeAreaType.UNDEFINED);
			terrainMap.setFloorHeight(point, clip.getHeight(point));
			terrainMap.setWallHeight(point, wallHeight);
		}
		
		for (Vec2 point : clip.getBorder())
			terrainMap.setAreaType(point, MazeAreaType.WALL);
	}
	
	public static void populateMap(TerrainMap terrainMap) {
		
		Maze maze = terrainMap.getMaze();
		
		int pathWidth = maze.getDimension(MazeDimension.PATH_WIDTH);
		int wallWidth = maze.getDimension(MazeDimension.WALL_WIDTH);
		
		ExitSegment entrance = ExitSegmentFactory.createEntranceSegment(terrainMap, maze.getEntrance(), pathWidth, wallWidth);
		terrainMap.mapSegment(entrance, MazeAreaType.PATH);
		
		PathMap pathMap = PathMapFactory.createPathMapOf(terrainMap, entrance.getEndPoint(), pathWidth, wallWidth);
		
		for (Vec2 exitPoint : maze.getSecondaryExits()) {
			ExitSegment exit = ExitSegmentFactory.createExitSegment(exitPoint, terrainMap, pathMap.getPathGridOffset(), pathWidth, pathMap.getPathGridMeshSize());
			terrainMap.mapSegment(exit, MazeAreaType.EXIT);
		}
		
		PathGenerator.createPathsInPathMap(pathMap, maze.getDimension(MazeDimension.PATH_LENGTH));
		PathMapFactory.copyPathsOntoTerrainMap(terrainMap, pathMap);
		
		flipMap(terrainMap);
		
		new TerrainEditor().editTerrain(terrainMap);
	}
	
	public static void flipMap(TerrainMap terrainMap) {
		
		for (int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				MazeAreaType fillType = terrainMap.getAreaType(x, z);
				
				if (fillType == MazeAreaType.UNDEFINED)
					terrainMap.setAreaType(x, z, MazeAreaType.WALL);
				
				else if (fillType == MazeAreaType.EXIT)
					terrainMap.setAreaType(x, z, MazeAreaType.PATH);
			}
		}
	}
	
	public static Map.Entry<Vec2, Vec2> calculateClipBounds(Clip clip) {
		
		Vec2 min = null;
		Vec2 max = null;
		
		for (Vec2 point : clip.getFill()) {
			
			if (min == null) {
				min = point.clone();
				max = point.clone();
				continue;
			}
			
			int x = point.getX();
			int z = point.getZ();
			
			if (x < min.getX())
				min.setX(x);
			else if (x > max.getX())
				max.setX(x);
			
			if (z < min.getZ())
				min.setZ(point.getZ());
			else if (z > max.getZ())
				max.setZ(z);
		}
		
		return new AbstractMap.SimpleEntry<>(min, max);
	}
}