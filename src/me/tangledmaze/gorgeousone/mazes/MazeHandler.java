package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class MazeHandler {
	
	private HashMap<Player, Maze> mazes;
	private HashMap<Player, Integer> mazeHeights;
	
	public MazeHandler() {
		mazes = new HashMap<>();
		mazeHeights = new HashMap<>();
	}
	
	public void reload() {
		for(Maze maze : mazes.values())
			maze.hide();
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
	
	public void deselctMaze(Player p) {
		if(hasMaze(p)) {
			mazes.get(p).hide();
			mazes.remove(p);
		}
	}
	
	public void setMazeHeight(Player p, int height) {
		mazeHeights.put(p, height);
	}
	
	public Integer getMazeHeight(Player p) {
		return mazeHeights.get(p);
	}

	public void remove(Player p) {
		mazes.remove(p);
		mazeHeights.remove(p);
	}
	
	public void startMaze(Player p, RectSelection selection) throws IllegalArgumentException {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		if(mazes.containsKey(p))
			mazes.get(p).hide();
		
		Maze maze = new Maze(selection.getShape(), p);
		mazes.put(p, maze);
		
		if(!mazeHeights.containsKey(p))
			mazeHeights.put(p, 2);
		
		maze.show();
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
}