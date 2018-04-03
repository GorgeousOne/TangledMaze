package me.tangledmaze.gorgeousone.listener;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class MazeHandler {
	
	private HashMap<Player, Maze> mazes;
	
	public MazeHandler() {
		mazes = new HashMap<>();
	}
	
	public void reload() {
		for(Maze maze : mazes.values())
			maze.hide();
	}
	
	public boolean hasMaze(Player p) {
		return mazes.containsKey(p);
	}
	
	public Maze getMaze(Player p) {
		return mazes.get(p);
	}
	
	public void startMaze(Player p, RectSelection selection) throws IllegalArgumentException {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		if(mazes.containsKey(p))
			mazes.get(p).hide();
		
		Maze maze = new Maze(selection.getShape(), p);
		mazes.put(p, maze);
		
		selection.hide();
		maze.show();
	}
	
	public void addSelectionToMaze(Player p, RectSelection selection) throws Exception {
		if(!mazes.containsKey(p))
			throw new NullPointerException("Could not find a maze that was created by " + p.getName() + ".");
		
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		selection.hide();
		mazes.get(p).add(selection.getShape());
	}
	
	public void subtractSelctionFromMaze(Player p, RectSelection selection)  throws Exception {
		if(!mazes.containsKey(p))
			throw new NullPointerException("Could not find a maze that pas created by " + p.getName());
		
		if(!selection.isComplete())
			throw new IllegalArgumentException("The passed selection is incomplete.");
		
		selection.hide();
		mazes.get(p).subtract(selection.getShape());
	}
}
