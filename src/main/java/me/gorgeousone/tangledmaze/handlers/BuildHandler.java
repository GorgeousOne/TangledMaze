package me.gorgeousone.tangledmaze.handlers;

import java.util.*;

import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.generation.BlockGenerator;
import me.gorgeousone.tangledmaze.generation.blockselector.AbstractBlockSelector;
import me.gorgeousone.tangledmaze.generation.datapicker.AbstractBlockDataPicker;
import me.gorgeousone.tangledmaze.maze.MazePartBlockBackup;
import me.gorgeousone.tangledmaze.maze.MazePart;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and
 * for unbuilding the maze again.
 */
public final class BuildHandler {

	private static final Map<Maze, TerrainMap> terrainMaps = new HashMap<>();
	private static final Map<Maze, MazePartBlockBackup> mazeBlockBackups = new HashMap<>();

	private BuildHandler() {}

	public static MazePartBlockBackup getBlockBackup(Maze maze) {
		return mazeBlockBackups.get(maze);
	}

	public static boolean hasBlockBackup(Maze maze) {
		return mazeBlockBackups.containsKey(maze);
	}

	public static void removeMaze(Maze maze) {
		mazeBlockBackups.remove(maze);
		terrainMaps.remove(maze);
	}

	public static void buildMazePart(
			Maze maze,
			MazePart mazePart,
			AbstractBlockSelector blockSelector,
			AbstractBlockDataPicker blockDataPicker) {

		if(maze.isConstructed() != mazePart.isMazeBuiltBefore())
			return;

		TerrainMap terrainMap;

		if(mazePart.isMazeBuiltBefore())
			terrainMap = terrainMaps.get(maze);
		else
			terrainMap = new TerrainMap(maze);

		Set<BlockDataState> mazePartBlockLocs = blockSelector.getBlocks(terrainMap);
		Set<BlockDataState> blockBackup = deepCloneBlockSet(mazePartBlockLocs);

		BlockGenerator.updateBlocks(
				mazePartBlockLocs,
				maze.getBlockComposition(),
				blockDataPicker,
				terrainMap,
				callback -> {

					if(!mazePart.isMazeBuiltBefore()) {
						maze.setConstructed(true);
						mazeBlockBackups.put(maze, new MazePartBlockBackup());
					}

					getBlockBackup(maze).setBackup(mazePart, blockBackup);
					Messages.MESSAGE_MAZE_BUILDING_COMPLETED.sendTo(maze.getPlayer(), new PlaceHolder("count", blockBackup.size()));
					terrainMaps.put(maze, terrainMap);
				}
		);
	}

	public static void unbuildMazePart(
		Maze maze,
		MazePart mazePart) {

		if(!hasBlockBackup(maze))
			return;

		MazePartBlockBackup mazeBackup = getBlockBackup(maze);

		if(!mazeBackup.hasBackup(mazePart))
			return;

		BlockGenerator.updateBlocks(
				mazeBackup.getPartBackup(mazePart),
				null,
				null,
				terrainMaps.get(maze),
				callback -> {
					mazeBackup.deleteBackup(mazePart);

					if(mazeBackup.isEmpty())
						reactivateMaze(maze);
				});
	}

	private static void reactivateMaze(Maze maze) {

		removeMaze(maze);
		maze.setConstructed(false);
		maze.updateHeights();

		new BukkitRunnable() {

			@Override
			public void run() {
				Renderer.displayMaze(maze);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}

	private static Set<BlockDataState> deepCloneBlockSet(Set<BlockDataState> blockSet) {
		Set<BlockDataState> clonedBlockSet = new HashSet<>();

		for (BlockDataState block : blockSet)
			clonedBlockSet.add(block.clone());

		return clonedBlockSet;
	}
}