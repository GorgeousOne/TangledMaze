package me.gorgeousone.tangledmaze.handler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.generation.AbstractGenerator;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and
 * for unbuilding the maze again.
 */
public final class BuildHandler {
	
	private static Map<Maze, List<BlockState>> builtWallBlocks = new HashMap<>();
	private static Map<Maze, List<BlockState>> builtFloorBlocks = new HashMap<>();
	private static Map<Maze, List<BlockState>> builtRoofBlocks = new HashMap<>();

	private static Map<Maze, TerrainMap> terrainMaps = new HashMap<>();
	
 	private BuildHandler() {}

	public static void setBuiltWallBlocks(Maze maze, List<BlockState> wallBlocks) {
		builtWallBlocks.put(maze, wallBlocks);
	}

	public static void setBuiltFloorBlocks(Maze maze, List<BlockState> blocks) {
		builtFloorBlocks.put(maze, blocks);
	}

	public static void setBuiltRoofBlocks(Maze maze, List<BlockState> blocks) {
		builtRoofBlocks.put(maze, blocks);
	}

	public static List<BlockState> getWallBlocks(Maze maze) {
		return builtWallBlocks.get(maze);
	}

	public static List<BlockState> getFloorBlocks(Maze maze) {
		return builtFloorBlocks.get(maze);
	}

	public static List<BlockState> getRoofBlocks(Maze maze) {
		return builtRoofBlocks.get(maze);
	}

	public static void setTerrainMap(Maze maze, TerrainMap terrainMap) {
 		terrainMaps.put(maze, terrainMap);
	}

	public static TerrainMap getTerrainMap(Maze maze) {
		return terrainMaps.get(maze);
	}
	
	public static void removeMaze(Maze maze) {
		builtWallBlocks.remove(maze);
		terrainMaps.remove(maze);
	}

	public static void unbuildMaze(Maze maze) {
		
		if(!maze.isConstructed() || !builtWallBlocks.containsKey(maze))
			return;
		
		AbstractGenerator degenerator = new AbstractGenerator() {

			@Override
			protected void chooseBlockMaterial(BlockState block, List<Material> blockMaterials) {}

			@Override
			protected List<BlockState> getRelevantBlocks(TerrainMap terrainMap) {
				
				List<BlockState> allBlocks = new LinkedList<>();

				try {
					allBlocks.addAll(getWallBlocks(maze));
					allBlocks.addAll(getFloorBlocks(maze));
					allBlocks.addAll(getRoofBlocks(maze));
				}catch(NullPointerException ignored) {}

				return allBlocks;
			}

		};
		
		degenerator.generatePart(null, null, action -> reactivateMaze(maze));
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
}