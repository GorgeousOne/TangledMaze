package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;

public abstract class Renderer implements Listener {
	
	private static HashMap<ShapeSelection, Boolean> shapeVisibilities = new HashMap<>();
	private static HashMap<Maze, Boolean> mazeVisibilities = new HashMap<>();
	
	public static boolean isSelectionVisible(ShapeSelection selection) {
		return shapeVisibilities.get(selection);
	}
	
	public static boolean isMazeVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	public static void registerShape(ShapeSelection s) {
		shapeVisibilities.put(s, false);
	}
	
	@SuppressWarnings("deprecation")
	public static void showSelection(ShapeSelection selection) {
		
		if(selection.getPlayer() == null || isSelectionVisible(selection))
			Bukkit.broadcastMessage("" + isSelectionVisible(selection));
		
		shapeVisibilities.put(selection, true);
		Player p = selection.getPlayer();
		
		if(selection.isComplete())
			for(ArrayList<Location> chunk : selection.getBorder().values())
				for(Location point : chunk)
					p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
		
		for(Location vertex : selection.getVertices())
			p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public static void showMaze(Maze maze) {
		
		if(maze.getPlayer() == null || isMazeVisible(maze))
			return;
		
		mazeVisibilities.put(maze, true);
		Player p = maze.getPlayer();
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location exit : maze.getExits())
			p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
		
		if(!maze.getExits().isEmpty())
			p.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
	}
	
	public static void hideSelection(ShapeSelection selection, Player p) {
		if(!isSelectionVisible(selection))
			return;
		
		shapeVisibilities.put(selection, false);
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
}
