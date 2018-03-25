package me.tangledmazes.gorgeousone.selectionstuff;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmazes.gorgeousone.main.Constants;
import me.tangledmazes.gorgeousone.main.TangledMain_go;
import me.tangledmazes.gorgeousone.shapestuff.Ellipse;
import me.tangledmazes.gorgeousone.shapestuff.Shape;

/**
 * A class to store the vertices of an Rectangle during being created
 */
public class RectSelection {
	
	private Player p;
	private World world;
	private Location firstVertex;
	private ArrayList<Location> vertices;
	private boolean isComplete;
	
	private Shape shape;
	
	/**
	 * Begins a rectangular selection with the first vertex already given
	 * @param creator Player who is creating this rectangle
	 * @param firstVertex first vertex of the rectangle
	 */
	public RectSelection(Player creator, Block firstVertex) {
		this.p = creator;
		world = creator.getWorld();
		vertices = new ArrayList<>();
		isComplete = false;
		
		this.firstVertex = firstVertex.getLocation();
		TangledMain_go.sendBlockLater(creator, this.firstVertex, Constants.SELECTION_BEGINNING);
	}
	
	/**
	 * @return the word this selection is being created in.
	 */
	public World getWorld() {
		return world;
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
	public ArrayList<Location> getVertices() {
		if(!isComplete())
			return null;
		return vertices;
	}

	public int getWidth() {
		if(!isComplete)
			return 0;
		return vertices.get(2).getBlockX() - vertices.get(0).getBlockX() + 1;
	}
	
	public int getDepth() {
		if(!isComplete)
			return 0;
		return vertices.get(2).getBlockZ() - vertices.get(0).getBlockZ() + 1;
	}
	
	//TODO add an actual variable selection
	public Shape getShape() {
		return shape;
	}
	
	/**
	 * Completes the selection by adding a second block as second, opposite vertex
	 * @param b
	 * @return if the block was added as vertex
	 */
	public void complete(Block b) {
		worldCheck(b);
		
		if(isComplete)
			return;
		
		if(b.getX() == firstVertex.getX() &&
		   b.getZ() == firstVertex.getZ()) {
			show();
			return;
		}
			
		calcVertices(firstVertex, b.getLocation());
		isComplete = true;
		shape = new Ellipse(this);
	}
	
	/**
	 * Moves a vertex to another block
	 * @param index 
	 * @param newVertex
	 */
	public void moveVertexTo(Block vertex, Block newVertex) {
		if(!isComplete() || !isVertex(vertex) || !newVertex.getWorld().equals(world))
			return;
		
		hide();
		int index = indexOfVertex(vertex);
		Location opposite = vertices.get((index+2) % 4);
		
		calcVertices(newVertex.getLocation(), opposite);
		show();
	}
	
	/**
	 * @param b
	 * @return if the given block is inside the rectangular shape.
	 */
	public boolean contains(Block b) {
		if(!isComplete())
			return false;
		return b.getX() >= vertices.get(0).getX() && b.getZ() <= vertices.get(2).getX() &&
			   b.getZ() >= vertices.get(0).getZ() && b.getZ() <= vertices.get(2).getZ();
	}
	
	/**
	 * @param b
	 * @return if the block is a vertex of the rectangle
	 */
	public boolean isVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return false;
		
		for(Location vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return true;
		return false;
	}

	/**
	 * @param b
	 * @return the index of the vertex in the private list
	 */
	public int indexOfVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return -1;
		
		for(Location vertex : vertices)
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
	
	/**
	 * Calculates the
	 * @param p0 first corner of the rectangle, optimally the first block that was clicked
	 * @param p1 second, opposite corner of the rectangle, optimally the last block that was clicked
	 */
	private void calcVertices(Location p0, Location p1) {
		int minX = Math.min(p0.getBlockX(), p1.getBlockX()),
			minZ = Math.min(p0.getBlockZ(), p1.getBlockZ()),
			maxX = Math.max(p0.getBlockX(), p1.getBlockX()),
			maxZ = Math.max(p0.getBlockZ(), p1.getBlockZ());

		
		vertices = new ArrayList<>(Arrays.asList(
			TangledMain_go.getNearestSurface(new Location(world, minX, p1.getY(), minZ)),
			TangledMain_go.getNearestSurface(new Location(world, maxX, p1.getY(), minZ)),
			TangledMain_go.getNearestSurface(new Location(world, maxX, p1.getY(), maxZ)),
			TangledMain_go.getNearestSurface(new Location(world, minX, p1.getY(), maxZ))));
	}
	
	private void worldCheck(Block b) {
		if(!b.getWorld().equals(world))
			throw new IllegalArgumentException("The selection's world and the block's world do not match.");
	}
	
	public void show() {
		if(isComplete())
			for(Location vertex : vertices)
				TangledMain_go.sendBlockLater(p, vertex, Constants.SELECTION_CORNER);
		else
			TangledMain_go.sendBlockLater(p, firstVertex, Constants.SELECTION_BEGINNING);
	}
	
	
	@SuppressWarnings("deprecation")
	public void hide() {
		if(!isComplete() && firstVertex == null)
			return;
		
		if(isComplete())
			for(Location vertex : vertices)
				p.sendBlockChange(vertex, vertex.getBlock().getType(), (byte) 0);
		else
			p.sendBlockChange(firstVertex, firstVertex.getBlock().getType(), (byte) 0);
	}
}