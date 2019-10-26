package me.gorgeousone.tangledmaze.handler;

import java.util.*;

import me.gorgeousone.tangledmaze.generation.BlockGenerator;
import me.gorgeousone.tangledmaze.generation.blockselection.AbstractBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselection.FloorBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselection.RoofBlockSelector;
import me.gorgeousone.tangledmaze.generation.blockselection.WallBlockSelector;
import me.gorgeousone.tangledmaze.generation.typechoosing.AbstractBlockTypeChooser;
import me.gorgeousone.tangledmaze.mapmaking.PathGenerator;
import me.gorgeousone.tangledmaze.mapmaking.TerrainEditor;
import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and
 * for unbuilding the maze again.
 */
public final class BuildHandler {
	
	private static final Map<Maze, Set<BlockState>> builtWallBlocks = new HashMap<>();
	private static final Map<Maze, Set<BlockState>> builtFloorBlocks = new HashMap<>();
	private static final Map<Maze, Set<BlockState>> builtRoofBlocks = new HashMap<>();

	private static final Map<Maze, TerrainMap> terrainMaps = new HashMap<>();
	
 	private BuildHandler() {}

	public static boolean hasFloor(Maze maze) {
		return builtFloorBlocks.containsKey(maze);
	}

 	public static boolean hasRoof(Maze maze) {
 		return builtRoofBlocks.containsKey(maze);
    }

	public static void removeMaze(Maze maze) {
		builtWallBlocks.remove(maze);
		builtFloorBlocks.remove(maze);
		builtRoofBlocks.remove(maze);
		terrainMaps.remove(maze);
	}

	public static void buildWalls(
			Maze maze,
			List<BlockType> blockTypeList,
			AbstractBlockTypeChooser blockTypeChooser) {

 		if(maze.isConstructed())
 			return;

		TerrainMap terrainMap = new TerrainMap(maze);
		new PathGenerator().generatePaths(terrainMap);
		new TerrainEditor().editTerrain(terrainMap);

		AbstractBlockSelector blockSelector = new WallBlockSelector();
		Set<BlockState> wallBlocks = blockSelector.getRelevantBlocks(terrainMap);
		Set<BlockState> backupBlocks = blockSelector.getRelevantBlocks(terrainMap);

		BlockGenerator.updateBlocks(
				wallBlocks,
				blockTypeList,
				blockTypeChooser,
				terrainMap, action -> {
					terrainMaps.put(maze, terrainMap);
					builtWallBlocks.put(maze, backupBlocks);
					maze.setConstructed(true);
				});
	}

	public static void buildFloor(
			Maze maze,
			List<BlockType> blockTypeList,
			AbstractBlockTypeChooser blockTypeChooser) {

		if(!maze.isConstructed() || builtFloorBlocks.containsKey(maze))
			return;

		TerrainMap terrainMap = terrainMaps.get(maze);

		AbstractBlockSelector blockSelector = new FloorBlockSelector();
		Set<BlockState> floorBlocks = blockSelector.getRelevantBlocks(terrainMap);
		Set<BlockState> backupBlocks = blockSelector.getRelevantBlocks(terrainMap);

		BlockGenerator.updateBlocks(
				floorBlocks,
				blockTypeList,
				blockTypeChooser,
				terrainMap, action ->
				builtFloorBlocks.put(maze, backupBlocks));
	}

	public static void buildRoof(
			Maze maze,
	        List<BlockType> blockTypeList,
	        AbstractBlockTypeChooser blockTypeChooser) {

		if(!maze.isConstructed() || builtRoofBlocks.containsKey(maze))
			return;

		TerrainMap terrainMap = terrainMaps.get(maze);

		AbstractBlockSelector blockSelector = new RoofBlockSelector();
		Set<BlockState> blocksToBuild = blockSelector.getRelevantBlocks(terrainMap);
		Set<BlockState> backupBlocks = blockSelector.getRelevantBlocks(terrainMap);

		BlockGenerator.updateBlocks(
				blocksToBuild,
				blockTypeList,
				blockTypeChooser,
				terrainMap, action -> builtRoofBlocks.put(maze, backupBlocks));
	}

	public static void unbuildFloor(Maze maze) {

		if(builtFloorBlocks.containsKey(maze))
			BlockGenerator.updateBlocks(builtFloorBlocks.get(maze), null, null, null, actionEvent -> builtFloorBlocks.remove(maze));
	}

	public static void unbuildRoof(Maze maze) {

		if(builtRoofBlocks.containsKey(maze))
			BlockGenerator.updateBlocks(builtRoofBlocks.get(maze), null, null, null, actionEvent -> builtRoofBlocks.remove(maze));
	}

	public static void unbuildMaze(Maze maze) {
		
		if(!maze.isConstructed())
			return;
		
		unbuildFloor(maze);
		unbuildRoof(maze);

		if(builtWallBlocks.containsKey(maze)) {
			BlockGenerator.updateBlocks(builtWallBlocks.get(maze), null, null, null, actionEvent -> {
				builtWallBlocks.remove(maze);
				reactivateMaze(maze);
			});
		}
	}

	private static void reactivateMaze(Maze maze) {

		new BukkitRunnable() {
			
			@Override
			public void run() {
				removeMaze(maze);
				maze.setConstructed(false);
				maze.updateHeights();
				Renderer.displayMaze(maze);
			}
		}.runTaskLater(TangledMain.getInstance(), 20);
	}
}