package me.gorgeousone.tangledmaze.handler;

import java.util.HashMap;
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
import me.gorgeousone.tangledmaze.generation.FloorGenerator;
import me.gorgeousone.tangledmaze.generation.PathGenerator;

/**
 * This class handles the process of constructing the maze.
 * It stores information about mazes that can be accessed by the generators and
 * for unbuilding the maze again.
 */
public final class BuildHandler {
	
	private static Map<Maze, List<BlockState>> builtWallBlocks = new HashMap<>();
	private static Map<Maze, TerrainMap> terrainMaps = new HashMap<>();
	
 	private BuildHandler() {}
	
	public static void setBuiltWallBlocks(Maze maze, List<BlockState> wallBlocks) {
		builtWallBlocks.put(maze, wallBlocks);
	}
	
	public static List<BlockState> getBuiltWallBlocks(Maze maze) {
		return builtWallBlocks.get(maze);
	}
	
	public static TerrainMap getTerrainMap(Maze maze) {
		return terrainMaps.get(maze);
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
				blockGenerator.generatePart(terrainMap, wallMaterials);
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
				generator.generatePart(terrainMaps.get(maze), blockMaterials);
			}
			
		}.runTaskAsynchronously(TangledMain.getInstance());
		
	}
	
	public static void unbuildMaze(Maze maze) {
		
		if(!maze.isConstructed())
			return;
		
		List<BlockState> blocksToUpdate = getBuiltWallBlocks(maze);
		
		BukkitRunnable builder = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!blocksToUpdate.isEmpty()) {
					
					blocksToUpdate.get(0).update(true, false);
					blocksToUpdate.remove(0);
					
					if(System.currentTimeMillis() - timer >= 49)
						return;
				}
				
				this.cancel();
				removeMaze(maze);
				reactivateMaze(maze);
			}
		};
		
		builder.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
	
	public static void removeMaze(Maze maze) {
		builtWallBlocks.remove(maze);
		terrainMaps.remove(maze);
	}
	
	private static void reactivateMaze(Maze maze) {

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