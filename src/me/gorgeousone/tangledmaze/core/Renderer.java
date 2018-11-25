package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;
import me.gorgeousone.tangledmaze.utils.Constants;

public abstract class Renderer implements Listener {
	
	private static HashMap<ShapeSelection, Boolean> shapeVisibilities = new HashMap<>();
	private static HashMap<Maze, Boolean> mazeVisibilities = new HashMap<>();
	
	public static void reload() {
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
	
	public static void unregisterShape(ShapeSelection shape) {
		shapeVisibilities.remove(shape);
	}
	
	public static void unregisterMaze(Maze maze) {
		mazeVisibilities.remove(maze);
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
		
		if(maze.getPlayer() == null)
			return;
		
		mazeVisibilities.put(maze, true);
		Player p = maze.getPlayer();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
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
		
		if(shape.getPlayer() == null || !isShapeVisible(shape))
			return;
		
		shapeVisibilities.put(shape, false);
		Player p = shape.getPlayer();
		
		if(shape.isComplete()) {
			for(ArrayList<Location> chunk : shape.getBorder().values()) {
				for(Location point : chunk) {
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
				}
			}
		}
		
		for(Location vertex : shape.getVertices())
			p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		
		if(updateMaze && MazeHandler.getMaze(p).isStarted() && isMazeVisible(MazeHandler.getMaze(p)))
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
	public static void showMazeAction(Maze maze, MazeAction action) {
		
		if(maze.getPlayer() == null)
			return;
		
		Player p = maze.getPlayer();
		
		for(Location point : action.getRemovedExits()) {
			p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
			if(maze.getExits().indexOf(point) == 0 && maze.getExits().size() > 1)
				p.sendBlockChange(maze.getExits().get(1), Constants.MAZE_MAIN_EXIT, (byte) 0);
		}
		
		for(Location point : action.getAddedBorder())
			p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location point : action.getRemovedBorder())
			p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	@SuppressWarnings("deprecation")
	private static void refreshMaze(Player p, ShapeSelection shape, Maze maze) {

		if(shape.isComplete()) {
			
			for(Chunk chunk : shape.getBorder().keySet()) {
				
				if(!maze.getBorder().containsKey(chunk))
					continue;
				
				for(Location point : shape.getBorder().get(chunk)) {
					if(maze.isBorder(point.getBlock()))
						maze.getPlayer().sendBlockChange(point, Constants.MAZE_BORDER , (byte) 0);
				}
			}
		}

		for(Location vertex : shape.getVertices())
			if(maze.isBorder(vertex.getBlock()))
				p.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
	}
}