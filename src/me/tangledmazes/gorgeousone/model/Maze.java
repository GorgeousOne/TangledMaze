package me.tangledmazes.gorgeousone.model;

import org.bukkit.util.Vector;

/**
 * A class that stores the information about a maze, e.g. where the are of the maze is and where preset walls of the maze should stand
 * @author Aaron
 */
public class Maze {

	int minX, minZ, length, depth;
	boolean[][] wallGrid;
	
	public Maze(int minX, int minZ, int length, int depth) {
		
		this.minX = minX;
		this.minZ = minZ;
		this.length = length;
		this.depth = depth;
		
		wallGrid = new boolean[length][depth];
	}

	/**
	 * @param point 
	 * @return if the point is inside the area of the maze.
	 */
	public boolean contais(Vector point) {
		return false;
	}
	
	/**
	 * @return the minimum point of the maze.
	 */
	public Vector getMin() {
		return new Vector(minX, 0, minZ);
	}
	
	/**
	 * @return the maximum point of the maze.
	 */
	public Vector getMax() {
		return new Vector(minX+length, 0, minZ+depth);
	}
	
	/**
	 * @param point
	 * @return if a wall is planned at this point of the maze.
	 */
	public boolean isWall(Vector point) {
		if(point.getX() < minX || point.getX() >= minX+length || 
		   point.getZ() < minZ || point.getZ() >= minZ+depth)
			return false;
		
		return wallGrid[point.getBlockX()-minX][point.getBlockZ()-minZ];
	}
	
	/**
	 * Sets if there shall be a wall at a specific point of the maze
	 * @param point
	 * @param state 
	 */
	public void setWall(Vector point, boolean state) {
		wallGrid[point.getBlockX()][point.getBlockZ()] = state; 
	}
}