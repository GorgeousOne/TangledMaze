package me.tangledmazes.gorgeousone.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * A class to store the vertices of an Rectangle during being created
 * @author Aaron
 */
public class RectSelection {
	
	private Player p;
	private World world;
	private ArrayList<Vector> unboundVertices, vertices;
	private boolean isComplete;
	
	/**
	 * @param p Player who is creating this rectangle
	 */
	public RectSelection(Player p) {
		this.p = p;
		world = p.getWorld();
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
	 * Adds a block as vertex to the rectangle if it is not yet defined by 2 vertices and the area of the rectangle was greater than 1 
	 * @param b
	 * @return if the block was added as vertex
	 */
	@SuppressWarnings("deprecation")
	public void addVertex(Block b) {
		worldCheck(b);
		
		if(isComplete)
			return;
		
		if(unboundVertices.isEmpty()) {
			unboundVertices.add(b.getLocation().toVector());
			p.sendBlockChange(b.getLocation(), Constants.SELECTION_BEGINNING, (byte) 0);

		}else {
			if(b.getX() == unboundVertices.get(0).getX() &&
			   b.getZ() == unboundVertices.get(0).getZ())
				return;
			else {
				unboundVertices.add(b.getLocation().toVector());
				calcVertices();
			}
		}
	}
	
	/**
	 * Moves a vertex to another block
	 * @param index 
	 * @param newVertex
	 */
	public void moveVertexTo(Block vertex, Block newVertex) {
		worldCheck(newVertex);
		
		if(!isComplete() || !isVertex(newVertex))
			return;
		
		int index = indexOfVertex(newVertex);
		vertices.get(index).setX(newVertex.getX()).setZ(newVertex.getZ());
		
		if(index % 2 == 0) {
			vertices.get(index+1 % 4).setX(newVertex.getX());
			vertices.get(index+3 % 4).setZ(newVertex.getZ());
		}else {
			vertices.get(index+1 % 4).setZ(newVertex.getZ());
			vertices.get(index+3 % 4).setX(newVertex.getX());
		}
	}
	
//	/**
//	 * @param b
//	 * @return if the given block is inside the rectangular shape.
//	 */
//	public boolean contains(Block b) {
//		if(!isComplete())
//			return false;
//		return b.getX() >= vertices.get(0).getX() && b.getZ() <= vertices.get(2).getX() &&
//			   b.getX() >= vertices.get(0).getX() && b.getZ() <= vertices.get(2).getX();
//	}
	
	/**
	 * @param b
	 * @return if the block is a vertex of the rectangle
	 */
	public boolean isVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return false;
		
		for(Vector vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return true;
		return false;
	}

	/**
	 * @param b
	 * @return the index of the vertex in the private list
	 */
	private int indexOfVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return -1;
		
		for(Vector vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		return -1;
	}
	
	/**
	 * @return if 2 require blocks are set for defining the rectangle
	 */
	public boolean isComplete() {
		return isComplete;
	}
	
	@SuppressWarnings("deprecation")
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
		
		int currentY = p.getEyeLocation().getBlockY();
		
		for(Vector vertice : vertices) {
			
			Location start = vertice.setY(currentY).toLocation(world);
			
			if(start.getBlock().getType() == Material.AIR) {
				for(; currentY >= 0; currentY--) {
					start.add(0, -1, 0);
					
					if(start.getBlock().getType() != Material.AIR) {
						p.sendBlockChange(start, Constants.SELECTION_BORDER, (byte) 0);
						vertice.setY(currentY);
						break;
					}
				}
				
			}else {
				for(; currentY >= 0; currentY++) {
					start.add(0, 1, 0);
					
					if(start.getBlock().getType() == Material.AIR) {
						start.add(0, -1, 0);
						p.sendBlockChange(start, Constants.SELECTION_BORDER, (byte) 0);
						vertice.setY(currentY-1);
						break;
					}
				}
			}
		}
		isComplete = true;
	}
	
	public void worldCheck(Block b) {
		if(!b.getWorld().equals(world))
			throw new IllegalArgumentException("The selection's world and the block's world do not match.");
	}
}