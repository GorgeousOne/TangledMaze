package me.tangledmazes.gorgeousone.mazestuff;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tangledmazes.gorgeousone.main.Constants;
import me.tangledmazes.gorgeousone.main.TangledMain_go;
import me.tangledmazes.gorgeousone.shapestuff.Shape;

/**
 * A class that stores the information about a maze, e.g. where the are of the maze is and where preset walls of the maze should stand
 * @author GorgeousOne
 */
public class Maze {
	
	private Player p;
	private ArrayList<Location> fill, border;
	private ArrayList<Shape> borderAreas;
	
	public Maze(Player creator, Shape borderArea) {
		
		p = creator;
		fill = borderArea.getFill();
		border = borderArea.getBorder();
		
		borderAreas = new ArrayList<>();
		borderAreas.add(borderArea);
	}
	
	public void add(Shape s) {
		ArrayList<Location> newBorder = new ArrayList<>();
		
		for(Location point : s.getBorder())
			if(!contains(point))
				newBorder.add(point.clone());
		
		if(newBorder.isEmpty())
			return;
		
		for (int i = border.size()-1; i >= 0; i--) {
			if(s.contains(border.get(i)) && !s.borderContains(border.get(i)))
				border.remove(i);
		}
				
		ArrayList<Location> newFill = new ArrayList<>();
		
		for(Location point : s.getFill())
			if(!contains(point))
				fill.add(point.clone());
		
		borderAreas.add(s);
		border.addAll(newBorder);
		fill.addAll(newFill);
	}
	
	public void subtract(Shape s) {
	}
	
	/**
	 * @param point 
	 * @return if the point is inside the area of the maze.
	 */
	public boolean contains(Location point) {
		for(Shape area : borderAreas)
			if(area.contains(point))
				return true;
		return false;
	}
	
	public void show() {
		for(Location point : fill)
			TangledMain_go.sendBlockLater(p, point, Constants.SELECTION_BORDER);
		for(Location point : border)
			TangledMain_go.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
	
	public void hide() {
		for(Location point : border)
			TangledMain_go.sendBlockLater(p, point, point.getBlock().getType());
		for(Location point : fill)
			TangledMain_go.sendBlockLater(p, point, point.getBlock().getType());
	}
	
//	/**
//	 * @return the minimum point of the maze.
//	 */
//	public Vector getMin() {
//		return new Vector(minX, 0, minZ);
//	}
//	
//	/**
//	 * @return the maximum point of the maze.
//	 */
//	public Vector getMax() {
//		return new Vector(minX+length, 0, minZ+depth);
//	}
//	
//	/**
//	 * @param point
//	 * @return if a wall is planned at this point of the maze.
//	 */
//	public boolean isWall(Vector point) {
//		if(point.getX() < minX || point.getX() >= minX+length || 
//		   point.getZ() < minZ || point.getZ() >= minZ+depth)
//			return false;
//		
//		return wallGrid[point.getBlockX()-minX][point.getBlockZ()-minZ];
//	}
//	
//	/**
//	 * Sets if there shall be a wall at a specific point of the maze
//	 * @param point
//	 * @param state 
//	 */
//	public void setWall(Vector point, boolean state) {
//		wallGrid[point.getBlockX()][point.getBlockZ()] = state; 
//	}
}