package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.util.Vector;

public class MazeAction {
	
	@SuppressWarnings("unused")
	private Maze maze;
	@SuppressWarnings("unused")
	private HashMap<Vector, Boolean> prevSettings;
	
	public MazeAction(Maze maze, ArrayList<Vector> points, boolean isWall) {
		this.maze = maze;
		this.prevSettings = new HashMap<>();
		
//		for(Vector point : points) {
//			prevSettings.put(point.clone(), maze.isWall(point));
//			maze.setWall(point, isWall);
//		}
	}
	
	public void undo() {
//		for(Vector point : prevSettings.keySet())
//			maze.setWall(point, prevSettings.get(point));
	}
}
