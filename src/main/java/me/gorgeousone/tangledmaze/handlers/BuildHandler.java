package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.generation.BlockGenerator;
import me.gorgeousone.tangledmaze.generation.blockselector.AbstractBlockSelector;
import me.gorgeousone.tangledmaze.generation.datapicker.AbstractBlockDataPicker;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazePart;
import me.gorgeousone.tangledmaze.maze.MazePartBlockBackup;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import me.gorgeousone.tangledmaze.utils.PlaceHolder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and
 * for unbuilding the maze again.
 */
public class BuildHandler {

	private Renderer renderer;

	private Map<Maze, TerrainMap> terrainMaps;
	private Map<Maze, MazePartBlockBackup> mazeBlockBackups;

	public BuildHandler(Renderer renderer) {

		this.renderer = renderer;

		terrainMaps = new HashMap<>();
		mazeBlockBackups = new HashMap<>();
	}

	public MazePartBlockBackup getBlockBackup(Maze maze) {
		return mazeBlockBackups.get(maze);
	}

	public boolean hasBlockBackup(Maze maze) {
		return mazeBlockBackups.containsKey(maze);
	}

	public void removeMaze(Maze maze) {
		mazeBlockBackups.remove(maze);
		terrainMaps.remove(maze);
	}

	public void buildMazePart(
			Maze maze,
			MazePart mazePart,
			AbstractBlockSelector blockSelector,
			AbstractBlockDataPicker blockDataPicker) {

		if (maze.isConstructed() != mazePart.isMazeBuiltBefore())
			return;

		TerrainMap terrainMap;

		if (mazePart.isMazeBuiltBefore())
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

					if (!mazePart.isMazeBuiltBefore()) {
						maze.setConstructed(true);
						mazeBlockBackups.put(maze, new MazePartBlockBackup());
					}

					getBlockBackup(maze).setBackup(mazePart, blockBackup);
					Messages.MESSAGE_MAZE_BUILDING_COMPLETED.sendTo(maze.getPlayer(), new PlaceHolder("count", blockBackup.size()));
					terrainMaps.put(maze, terrainMap);
				}
		);
	}

	public void unbuildMazePart(
			Maze maze,
			MazePart mazePart) {

		if (!hasBlockBackup(maze))
			return;

		MazePartBlockBackup mazeBackup = getBlockBackup(maze);

		if (!mazeBackup.hasBackup(mazePart))
			return;

		BlockGenerator.updateBlocks(
				mazeBackup.getPartBackup(mazePart),
				null,
				null,
				terrainMaps.get(maze),
				callback -> {
					mazeBackup.deleteBackup(mazePart);

					if (mazeBackup.isEmpty())
						reactivateMaze(maze);
				});
	}

	private void reactivateMaze(Maze maze) {

		removeMaze(maze);
		maze.setConstructed(false);
		maze.updateHeights();

		new BukkitRunnable() {

			@Override
			public void run() {
				renderer.displayMaze(maze);
			}
		}.runTaskLater(TangledMain.getInstance(), 2);
	}

	private Set<BlockDataState> deepCloneBlockSet(Set<BlockDataState> blockSet) {
		Set<BlockDataState> clonedBlockSet = new HashSet<>();

		for (BlockDataState block : blockSet) {
			clonedBlockSet.add(block.clone());
		}

		return clonedBlockSet;
	}
}