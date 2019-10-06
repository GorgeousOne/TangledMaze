package me.gorgeousone.tangledmaze.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.generation.WallGenerator;
import me.gorgeousone.tangledmaze.mapmaking.TerrainEditor;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.generation.AbstractGenerator;
import me.gorgeousone.tangledmaze.generation.FloorGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;

/**
 * This class handles the process of constructing and deconstructing mazes.
 * It stores information about mazes that can be accessed by generators and
 * for unbuilding the maze again.
 */
public final class BuildHandler {
	
	private static Map<Maze, List<BlockState>> builtWallBlocks = new HashMap<>();
	private static Map<Maze, List<BlockState>> builtFloorBlocks = new HashMap<>();

	private static Map<Maze, TerrainMap> terrainMaps = new HashMap<>();
	
 	private BuildHandler() {}
	
	public static void setBuiltWallBlocks(Maze maze, List<BlockState> wallBlocks) {
		builtWallBlocks.put(maze, wallBlocks);
	}
	
	public static List<BlockState> getWallBlocks(Maze maze) {
		return builtWallBlocks.get(maze);
	}

	public static void setBuiltFloorBlocks(Maze maze, List<BlockState> blocks) {
		builtFloorBlocks.put(maze, blocks);
	}

	public static List<BlockState> getFloorBlocks(Maze maze) {
		return builtFloorBlocks.get(maze);
	}
	
	public static TerrainMap getTerrainMap(Maze maze) {
		return terrainMaps.get(maze);
	}
	
	public static void removeMaze(Maze maze) {
		builtWallBlocks.remove(maze);
		terrainMaps.remove(maze);
	}
	
	public static void buildMaze(
			Maze maze,
			List<Material> wallMaterials,
			PathGenerator pathGenerator,
			TerrainEditor terrainEditor,
			WallGenerator blockGenerator) {
		
		if(maze.isConstructed())
			return;
		
		maze.setConstructed(true);

		new BukkitRunnable() {
			@Override
			public void run() {
				
				TerrainMap terrainMap = new TerrainMap(maze);
				terrainMaps.put(maze, terrainMap);
				
				pathGenerator.generatePaths(terrainMap);
				terrainEditor.editTerrain(terrainMap);
				blockGenerator.generatePart(terrainMap, wallMaterials, null);
			}
		}.runTaskAsynchronously(TangledMain.getInstance());
	}
	
	public static void buildMazeFloor(
			Maze maze,
			List<Material> blockMaterials,
			FloorGenerator generator) {
		
		if(!maze.isConstructed())
			return;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				generator.generatePart(terrainMaps.get(maze), blockMaterials, null);
			}
			
		}.runTaskAsynchronously(TangledMain.getInstance());
		
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
				
				allBlocks.addAll(getWallBlocks(maze));
				allBlocks.addAll(getFloorBlocks(maze));
				
				return allBlocks;
			}

		};
		
		degenerator.generatePart(null, null, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reactivateMaze(maze);
			}
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
}