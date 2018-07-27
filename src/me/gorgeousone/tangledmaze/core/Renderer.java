package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;

public abstract class Renderer implements Listener {
	
	private static HashMap<ShapeSelection, Boolean> shapeVisibilities = new HashMap<>();
	private static HashMap<Maze, Boolean> mazeVisibilities = new HashMap<>();
	
	
	public static void unregister() {
		for(ShapeSelection selection : shapeVisibilities.keySet()) {
			if(isShapeVisible(selection))
				hideShape(selection, false);
		}
		
		for(Maze maze : mazeVisibilities.keySet()) {
			if(isMazeVisible(maze))
				hideMaze(maze);
		}
	}
	
	public static void registerShape(ShapeSelection shape) {
		shapeVisibilities.put(shape, false);
	}
	
	public static void registerMaze(Maze maze) {
		mazeVisibilities.put(maze, false);
	}
	
	public static boolean isShapeVisible(ShapeSelection shape) {
		return shapeVisibilities.get(shape);
	}
	
	public static boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	@SuppressWarnings("deprecation")
	public static void showSelection(ShapeSelection shape) {
		
		if(shape.getPlayer() == null)
			return;
		
		shapeVisibilities.put(shape, true);
		Player p = shape.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				if(shape.isComplete()) {
					for(ArrayList<Location> chunk : shape.getBorder().values()) {
						for(Location point : chunk) {
							p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
						}
					}
				}
				
				for(Location vertex : shape.getVertices())
					p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public static void showMaze(Maze maze) {
		
		if(maze.getPlayer() == null || isMazeVisible(maze))
			return;
		
		mazeVisibilities.put(maze, true);
		Player p = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				Bukkit.broadcastMessage("mazes is shown");
				
				for(ArrayList<Location> chunk : maze.getBorder().values())
					for(Location point : chunk)
						p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				
				for(Location exit : maze.getExits())
					p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
				
				if(!maze.getExits().isEmpty())
					p.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	@SuppressWarnings("deprecation")
	public static void hideShape(ShapeSelection shape, boolean updateMaze) {
		
		if(shape.getPlayer() == null ||!isShapeVisible(shape))
			return;
		
		Player p = shape.getPlayer();
		
		if(p == null)
			return;
		
		shapeVisibilities.put(shape, false);
		Bukkit.broadcastMessage("shape is hidden");
		
		if(shape.isComplete()) {
			for(ArrayList<Location> chunk : shape.getBorder().values()) {
				for(Location point : chunk) {
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
				}
			}
		}
		
		for(Location vertex : shape.getVertices())
			p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		
		
		if(MazeHandler.hasMaze(p) && isMazeVisible(MazeHandler.getMaze(p)))
			refreshMaze(p, shape, MazeHandler.getMaze(p));
	}
	
	@SuppressWarnings("deprecation")
	public static void hideMaze(Maze maze) {
		
		if(maze.getPlayer() == null || !isMazeVisible(maze))
			return;
		
		mazeVisibilities.put(maze, false);
		Player p = maze.getPlayer();
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	@SuppressWarnings("deprecation")
	private static void refreshMaze(Player p, ShapeSelection shape, Maze maze) {

		if(shape.isComplete()) {

			for(Chunk c : maze.getBorder().keySet()) {
				if(!maze.getBorder().containsKey(c))
					continue;
				
				for(Location point : maze.getBorder().get(c)) {
					if(maze.isBorder(point.getBlock()))
						maze.getPlayer().sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				}
			}
		}

		for(Location vertex : shape.getVertices())
			if(maze.isBorder(vertex.getBlock()))
				p.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
	}
}
