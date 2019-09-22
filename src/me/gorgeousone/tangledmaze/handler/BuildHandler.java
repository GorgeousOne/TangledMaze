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
import me.gorgeousone.tangledmaze.generation.TerrainMap;
import me.gorgeousone.tangledmaze.generation.PathGenerator;
import me.gorgeousone.tangledmaze.generation.TerrainEditor;

public final class BuildHandler {
	
	private static Map<Maze, List<BlockState>> builtWallBlocks = new HashMap<>();

	private BuildHandler() {}
	
	public static void removeMaze(Maze maze) {
		builtWallBlocks.remove(maze);
	}
	
	public static boolean isConstructed(Maze maze) {
		return builtWallBlocks.containsKey(maze);
	}
	
	public static void setBuiltWallBlocks(Maze maze, List<BlockState> wallBlocks) {
		builtWallBlocks.put(maze, wallBlocks);
	}
	
	public static List<BlockState> getBuiltWallBlocks(Maze maze) {
		return builtWallBlocks.get(maze);
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
				pathGenerator.generatePaths(terrainMap);
				terrainEditor.editTerrain(terrainMap);
				blockGenerator.generateWalls(terrainMap, wallMaterials);
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
				maze.setConstructed(false);
				maze.updateHeights();
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						Renderer.displayMaze(maze);
					}
				}.runTaskLater(TangledMain.getInstance(), 2);
			}
		};
		
		builder.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
}