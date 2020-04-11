package me.gorgeousone.tangledmaze.handlers;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.BlockGenerator;
import me.gorgeousone.tangledmaze.generation.MazePart;
import me.gorgeousone.tangledmaze.generation.MazeBackup;
import me.gorgeousone.tangledmaze.generation.blockdatapickers.AbstractBlockDataPicker;
import me.gorgeousone.tangledmaze.generation.blocklocators.AbstractBlockLocator;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMapFactory;
import me.gorgeousone.tangledmaze.maze.Maze;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and for unbuilding the maze again.
 */
public class BuildHandler {
	
	private JavaPlugin plugin;
	private MazeHandler mazeHandler;
	
	private Map<Maze, MazeBackup> mazeBackups;
	
	public BuildHandler(JavaPlugin plugin, MazeHandler mazeHandler) {
		
		this.plugin = plugin;
		this.mazeHandler = mazeHandler;
		
		mazeBackups = new HashMap<>();
	}
	
	public MazeBackup getMazeBackup(Maze maze) {
		
		if(!mazeBackups.containsKey(maze))
			mazeBackups.put(maze, new MazeBackup(maze));
		
		return mazeBackups.get(maze);
	}
	
	public boolean hasMazeBackup(Maze maze) {
		return mazeBackups.containsKey(maze);
	}
	
	public void setMazeBackup(Maze maze, MazeBackup backup) {
		mazeBackups.put(maze, backup);
	}
	
	private void removeBlocksFromBackup(CommandSender sender, Maze maze, MazePart mazePart) {
		
		MazeBackup mazeBackup = getMazeBackup(maze);
		mazeBackup.deleteMazePart(mazePart);
		
		if (mazeBackup.isEmpty()) {
		
			removeMaze(maze);
			maze.setConstructed(false);
			maze.updateHeights();
			mazeHandler.displayMazeOf(sender);
		}
	}
	
	public void removeMaze(Maze maze) {
		mazeBackups.remove(maze);
	}
	
	public void buildMazePart(
			Maze maze,
			MazePart mazePart,
			AbstractBlockLocator blockLocator,
			AbstractBlockDataPicker blockDataPicker,
			ActionListener callback) {
		
		if (maze.isConstructed() != mazePart.isMazeBuiltBefore())
			return;
		
		MazeBackup mazeBackup = getMazeBackup(maze);
		
		if(!maze.isConstructed())
			mazeBackup.setTerrainMap(TerrainMapFactory.createTerrainMapOf(maze));
		
		TerrainMap terrainMap = mazeBackup.getTerrainMap();
		Set<LocatedBlockData> mazePartBlockLocs = blockLocator.locateBlocks(terrainMap);
		Set<LocatedBlockData> blockBackup = deepCloneBlockSet(mazePartBlockLocs);
		
		BlockGenerator.updateBlocks(
				plugin,
				mazePartBlockLocs,
				maze.getBlockComposition(),
				blockDataPicker,
				terrainMap,
				nestedCallback -> {
					
					maze.setConstructed(true);
					mazeBackup.setBlocks(mazePart, blockBackup);
					
					if(callback != null)
						callback.actionPerformed(null);
				});
	}
	
	public void unbuildMazePart(
			Maze maze,
			MazePart mazePart,
			CommandSender sender) {
		
		if (!hasMazeBackup(maze))
			return;
		
		MazeBackup mazeBackup = getMazeBackup(maze);
		
		if (!mazeBackup.hasBlocksFor(mazePart))
			return;
		
		BlockGenerator.updateBlocks(
				plugin,
				mazeBackup.getBlocks(mazePart),
				null,
				null,
				mazeBackup.getTerrainMap(),
				callback -> removeBlocksFromBackup(sender, maze, mazePart));
	}
	
	private Set<LocatedBlockData> deepCloneBlockSet(Set<LocatedBlockData> blockSet) {
		
		Set<LocatedBlockData> clonedBlockSet = new HashSet<>();
		
		for (LocatedBlockData block : blockSet)
			clonedBlockSet.add(block.clone());
		
		return clonedBlockSet;
	}
}