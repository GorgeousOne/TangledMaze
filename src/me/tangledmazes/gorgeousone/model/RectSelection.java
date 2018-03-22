package me.tangledmazes.gorgeousone.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * A class to store the vertices of an Rectangle during being created
 * @author Aaron
 */
public class RectSelection {
	
	private Player p;
	private ArrayList<Vector> unboundVertices, vertices;
	private boolean isComplete;
	
	/**
	 * @param p Player who is creating this rectangle
	 */
	public RectSelection(Player p) {
		this.p = p;
		unboundVertices = new ArrayList<>();
		vertices = new ArrayList<>();
		isComplete = false;
	}
	
	/**
	 * @return the player who creates the selection.
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * @return the list of vertices of the rectangle in case the rectangle is completed
	 */
	public ArrayList<Vector> getVertices() {
		if(!isComplete())
			return null;
		return vertices;
	}
	
	/**
	 * Adds a point as vertex to the rectangle if it is not yet defined by 2 vertices and the area of the rectangle was greater than 1 
	 * @param point
	 * @return if the point was added as vertex
	 */
	public void addVertex(Vector point) {
		if(isComplete)
			return;

		if(unboundVertices.isEmpty())
			unboundVertices.add(point);

		else {
			if(point.getBlockX() == unboundVertices.get(0).getBlockX() &&
			   point.getBlockZ() == unboundVertices.get(0).getBlockZ())
				return;
			else {
				unboundVertices.add(point);
				calcVertices();
			}
		}
	}
	
	/**
	 * Moves a vertex to another point
	 * @param index 
	 * @param newPoint
	 */
	public void moveVertexTo(Vector vertex, Vector newPoint) {
		if(!isComplete() || !isVertex(newPoint))
			return;
		
		int index = indexOfVertex(newPoint);
		vertices.get(index).setX(newPoint.getBlockX()).setZ(newPoint.getBlockZ());
		
		if(index % 2 == 0) {
			vertices.get(index+1 % 4).setX(newPoint.getBlockX());
			vertices.get(index+3 % 4).setZ(newPoint.getBlockZ());
		}else {
			vertices.get(index+1 % 4).setZ(newPoint.getBlockZ());
			vertices.get(index+3 % 4).setX(newPoint.getBlockX());
		}
	}

	/**
	 * @param point
	 * @return if the point is a vertex of the rectangle
	 */
	public boolean isVertex(Vector point) {
		if(!isComplete())
			return false;
		
		for(Vector vertex : vertices)
			if(point.getBlockX() == vertex.getX() &&
			   point.getBlockZ() == vertex.getZ())
				return true;
		return false;
	}

	/**
	 * @param point
	 * @return the index of the vertex in the private list
	 */
	private int indexOfVertex(Vector point) {
		if(!isComplete())
			return -1;
		
		for(Vector vertex : vertices)
			if(point.getBlockX() == vertex.getX() &&
			   point.getBlockZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		return -1;
	}
	
	/**
	 * @return if 2 points are set for defining the rectangle
	 */
	public boolean isComplete() {
		return isComplete;
	}

	private void calcVertices() {
		if(isComplete())
			return;
		
		Vector p0 = unboundVertices.get(0),
			   p1 = unboundVertices.get(1);
		
		int minX = Math.min(p0.getBlockX(), p1.getBlockX()),
			minZ = Math.min(p0.getBlockZ(), p1.getBlockZ()),
			maxX = Math.max(p0.getBlockX(), p1.getBlockX()),
			maxZ = Math.max(p0.getBlockZ(), p1.getBlockZ());
		
		vertices.addAll(Arrays.asList(new Vector(minX, 0, minZ),
									  new Vector(maxX, 0, minZ),
									  new Vector(maxX, 0, maxZ),
									  new Vector(minX, 0, maxZ)));
		isComplete = true;
	}
}