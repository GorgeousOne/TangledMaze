package me.gorgeousone.tangledmaze.terrainmap;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeDimension;
import me.gorgeousone.tangledmaze.terrainmap.paths.NewPathGenerator;
import me.gorgeousone.tangledmaze.terrainmap.paths.PathMap;
import me.gorgeousone.tangledmaze.terrainmap.paths.PathMapFactory;
import me.gorgeousone.tangledmaze.utils.Utils;
import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.Map;

public final class TerrainMapFactory {
	
	private TerrainMapFactory() {}
	
	public static TerrainMap createMapOf(Maze maze) {
		
		Clip mazeClip = maze.getClip();
		Map.Entry<Vec2, Vec2> clipBounds = Utils.calculateClipBounds(mazeClip);

		TerrainMap terrainMap = new TerrainMap(clipBounds.getKey(), clipBounds.getValue(), maze);
		copyClipOntoMap(mazeClip, terrainMap, maze.getDimension(MazeDimension.WALL_HEIGHT));
		createMazeInMap(terrainMap);
	
		return terrainMap;
	}
	
	private static void copyClipOntoMap(Clip clip, TerrainMap terrainMap, int wallHeight) {
		
		for (Vec2 point : clip.getFill()) {
			terrainMap.setType(point, MazeAreaType.UNDEFINED);
			terrainMap.setFloorHeight(point, clip.getHeight(point));
			terrainMap.setWallHeight(point, wallHeight);
		}
		
		for (Vec2 point : clip.getBorder())
			terrainMap.setType(point, MazeAreaType.WALL);
	}
	
	private static void createMazeInMap(TerrainMap terrainMap) {
		
		new PathGenerator().generateExits(terrainMap);
		
		PathMap pathMap = PathMapFactory.createPathMap(terrainMap);
		NewPathGenerator.populatePathMap(pathMap);
		PathMapFactory.copyPathsOnTerrainMap(terrainMap, pathMap);
		
		flipMap(terrainMap);
		
		new TerrainEditor().editTerrain(terrainMap);
	}
	
	public static void flipMap(TerrainMap terrainMap) {
		
		for (int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for (int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				MazeAreaType fillType = terrainMap.getAreaType(x, z);
				
				if (fillType == MazeAreaType.UNDEFINED)
					terrainMap.setType(x, z, MazeAreaType.WALL);
				
				else if (fillType == MazeAreaType.EXIT)
					terrainMap.setType(x, z, MazeAreaType.PATH);
			}
		}
	}
}