package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.utils.Constants;

public class MazeHandler {
	
	private HashMap<Player, Maze> mazes;
	private HashMap<Player, Integer> mazeHeights;
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
		return mazes.containsKey(p);
	}
	
	public Maze getMaze(Player p) {
		return mazes.get(p);
	}
	
	public boolean isVisible(Maze maze) {
		return mazeVisibilities.get(maze);
	}
	
	public void setMazeHeight(Player p, int height) {
		mazeHeights.put(p, height);
		
		if(mazes.containsKey(p))
			mazes.get(p).setWallHeight(height);
	}
	
	public Integer getMazeHeight(Player p) {
		return mazeHeights.get(p);
	}

	public void deselctMaze(Player p) {
		if(hasMaze(p)) {
			hide(mazes.get(p));
			mazeVisibilities.remove(mazes.get(p));
			mazes.remove(p);
		}
	}
	
	public void remove(Player p) {
		mazeVisibilities.remove(mazes.get(p));
		mazeHeights.remove(p);
		mazes.remove(p);
	}
	
	public void startMaze(Player p, RectSelection selection) throws IllegalArgumentException {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		if(mazes.containsKey(p))
			hide(mazes.get(p));
		
		Maze maze = new Maze(selection.getShape(), p);
		mazes.put(p, maze);
		mazeVisibilities.put(maze, false);
		
		if(!mazeHeights.containsKey(p))
			mazeHeights.put(p, 3);
		
		maze.setWallHeight(mazeHeights.get(p));
	}
	
	public void addSelectionToMaze(Player p, RectSelection selection) throws Exception {
		if(!mazes.containsKey(p))
			throw new NullPointerException("Could not find a maze created by " + p.getName() + ".");
		
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		Maze maze = mazes.get(p);
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, maze.getAddition(selection.getShape())));
	}
	
	public void cutSelctionFromMaze(Player p, RectSelection selection)  throws Exception {
		if(!mazes.containsKey(p))
			throw new NullPointerException("Could not find a maze created by " + p.getName() + ".");
		
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		Maze maze = mazes.get(p);
		Bukkit.getPluginManager().callEvent(new MazeShapeEvent(maze, maze.getSubtraction(selection.getShape())));
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