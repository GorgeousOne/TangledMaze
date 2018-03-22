package me.tangledmazes.gorgeousone.model;

import java.util.ArrayList;

/**
 * A calss storing every action applied to a maze in order to make them undoable one after each other 
 * @author Aaron
 */
public class ActionHistory {
	
	private Maze maze;
	private ArrayList<MazeAction> actions;
	
	
	public ActionHistory (Maze maze) {
		this.maze = maze;
		this.actions = new ArrayList<>();
	}
	
	/**
	 * @return the maze actions get stored for
	 */
	public Maze getMaze() {
		return maze;
	}
	
	/**
	 * @param action that shall be applied on the maze and stored
	 */
	public void addAction(MazeAction action) {
		actions.add(action);
	}
	
	/**
	 * undoes the last action applied on the maze
	 */
	public void undoLast() {
		if(actions.isEmpty())
			return;
		actions.get(actions.size()-1).undo();
		actions.remove(actions.size()-1);
	}
}

