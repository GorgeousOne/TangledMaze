package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.utils.Constants;

public class MazeHandler {
	
	private HashMap<UUID, Maze> mazes;
	private HashMap<UUID, Integer> mazeHeights;
	private HashMap<Maze, Boolean> mazeVisibilities;
	
	public MazeHandler() {
		mazes            = new HashMap<>();
		mazeHeights      = new HashMap<>();
		mazeVisibilities = new HashMap<>();
	}
	
	public void reload() {
		for(Maze maze : mazes.values())
			hide(maze);
	}
	
	public ArrayList<Maze> getMazes() {
		return new ArrayList<Maze>(mazes.values());
	}
	
	public boolean hasMaze(Player p) {
		return mazes.containsKey(p.getUniqueId());
	}
	
	public Maze getMaze(Player p) {
		return mazes.get(p.getUniqueId());
	}
	
	public boolean isVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	public void setMazeHeight(Player p, int height) {
		mazeHeights.put(p.getUniqueId(), height);
		
		if(mazes.containsKey(p.getUniqueId()))
			mazes.get(p.getUniqueId()).setWallHeight(height);
	}
	
	public Integer getMazeHeight(Player p) {
		return mazeHeights.get(p.getUniqueId());
	}

	public void deselctMaze(Player p) {
		if(hasMaze(p)) {
			UUID uuid = p.getUniqueId();
			
			hide(mazes.get(uuid));
			mazeVisibilities.remove(mazes.get(uuid));
			mazes.remove(uuid);
		}
	}
	
	public void remove(Player p) {
		UUID uuid = p.getUniqueId();
		
		mazeVisibilities.remove(mazes.get(uuid));
		mazeHeights.remove(uuid);
		mazes.remove(uuid);
	}
	
	public void startMaze(Player p, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		UUID uuid = p.getUniqueId();

		if(mazes.containsKey(uuid))
			hide(mazes.get(uuid));
		
		Maze maze = new Maze(selection.getShape(), p);
		mazes.put(uuid, maze);
		mazeVisibilities.put(maze, false);
		
		if(!mazeHeights.containsKey(uuid))
			mazeHeights.put(uuid, 3);
		
		maze.setWallHeight(mazeHeights.get(uuid));
	}
	
	public void addSelectionToMaze(Maze maze, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");

		MazeAction action = maze.getAddition(selection.getShape());
		
		if(action.getAddedFill().size() == selection.getShape().size())
			throw new IllegalArgumentException("The passed selection does not intersect the maze properly.");
			
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, action));
	}
	
	public void cutSelctionFromMaze(Maze maze, RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		MazeAction action = maze.getSubtraction(selection.getShape());
		
		if(action.getRemovedFill().size() == 0)
			throw new IllegalArgumentException("The passed selection does not intersect the maze properly.");
		
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, action));
	}
	
	@SuppressWarnings("deprecation")
	public void show(Maze maze) {
		Player p = maze.getPlayer();
		
		if(p == null)
			return;
		
		mazeVisibilities.put(maze, true);
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location exit : maze.getExits())
			p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
		
		if(!maze.getExits().isEmpty())
			p.sendBlockChange(maze.getExits().get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void hide(Maze maze) {
		Player p = maze.getPlayer();
		
		if( p == null)
			return;
		
		mazeVisibilities.put(maze, false);
		
		for(ArrayList<Location> chunk : maze.getBorder().values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
}