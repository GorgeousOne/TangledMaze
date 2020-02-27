package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.terrainmap.paths.ExitSegment;
import me.gorgeousone.tangledmaze.terrainmap.paths.ExitSegmentFactory;
import me.gorgeousone.tangledmaze.terrainmap.paths.PathGenerator;
import me.gorgeousone.tangledmaze.terrainmap.paths.PathMap;
import me.gorgeousone.tangledmaze.terrainmap.paths.PathMapFactory;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.Map;

public final class TerrainMapFactory {
	
	private TerrainMapFactory() {}
	
	public static TerrainMap createTerrainMapOf(Maze maze) {
		
		Clip mazeClip = maze.getClip();
		Map.Entry<Vec2, Vec2> clipBounds = Utils.calculateClipBounds(mazeClip);

		TerrainMap terrainMap = new TerrainMap(clipBounds.getKey(), clipBounds.getValue(), maze);
		copyClipOntoMap(mazeClip, terrainMap, maze.getDimension(MazeDimension.WALL_HEIGHT));
	
		return terrainMap;
	}
	
	public static void populateMap(TerrainMap terrainMap) {
		
		Maze maze = terrainMap.getMaze();
		
		int pathWidth = maze.getDimension(MazeDimension.PATH_WIDTH);
		int wallWidth = maze.getDimension(MazeDimension.WALL_WIDTH);
		
		ExitSegment entrance = ExitSegmentFactory.createEntranceSegment(terrainMap, maze.getEntrance(), pathWidth, wallWidth);
		terrainMap.mapSegment(entrance, MazeAreaType.PATH);
		
		PathMap pathMap = PathMapFactory.createPathMapOf(terrainMap, entrance.getEndPoint(), pathWidth, wallWidth);
		
		for(Vec2 exitPoint : maze.getSecondaryExits()) {
			ExitSegment exit = ExitSegmentFactory.createExitSegment(exitPoint, terrainMap, pathMap.getPathGridOffset(), pathWidth, pathMap.getPathGridMeshSize());
			terrainMap.mapSegment(exit, MazeAreaType.EXIT);
		}
		
		PathGenerator.createPathsInPathMap(pathMap);
		PathMapFactory.copyPathsOntoTerrainMap(terrainMap, pathMap);
		
		flipMap(terrainMap);
		
		new TerrainEditor().editTerrain(terrainMap);
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
}