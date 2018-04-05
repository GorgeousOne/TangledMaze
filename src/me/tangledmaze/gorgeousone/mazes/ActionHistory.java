package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

/**
 * A class storing every action applied to a maze in order to make them undoable one after each other 
 * @author GeorgeousOne
 */
public class ActionHistory {
	
	private Maze maze;
	private ArrayList<MazeAction> actions;
	
	
	public ActionHistory (Maze maze) {
		this.maze = maze;
		this.actions = new ArrayList<>();
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	public void addAction(MazeAction action) {
		actions.add(action);
	}
	
	public void undoLast() {
		if(actions.isEmpty())
			return;
		actions.get(actions.size()-1).undo();
		actions.remove(actions.size()-1);
	}
}

