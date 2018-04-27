package me.tangledmaze.gorgeousone.selections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Utils;

public class RectSelection {
	
	private Player p;
	private World world;
	private Shape shape;
	
	private ArrayList<Location> vertices;
	private boolean isComplete;
	
	private Class<? extends Shape> shapeType;

	public RectSelection(Block firstVertex, Player editor, Class<? extends Shape> shapeType) {
		this.p = editor;
		world = firstVertex.getWorld();
		vertices = new ArrayList<>();
		isComplete = false;
		
		vertices.add(firstVertex.getLocation());
		this.shapeType = shapeType;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public ArrayList<Location> getVertices() {
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
	
	public Shape getShape() {
		return shape;
	}
	
	public void complete(Block b) {
		worldCheck(b);
		
		if(isComplete)
			return;
		
		calcVertices(vertices.get(0), b.getLocation());
		isComplete = true;
		
		try {
			Constructor<? extends Shape> con = shapeType.getConstructor(this.getClass());
			shape = con.newInstance(this);
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void moveVertexTo(Block vertex, Block newVertex) {
		if(!isComplete() || !isVertex(vertex) || !newVertex.getWorld().equals(world))
			return;
		
		int index = indexOfVertex(vertex);
		Location opposite = vertices.get((index+2) % 4);
		
		calcVertices(newVertex.getLocation(), opposite);

		try {
			Constructor<? extends Shape> con = shapeType.getConstructor(this.getClass());
			shape = con.newInstance(this);
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public boolean contains(Location point) {
		if(isComplete())
			return point.getBlockX() >= vertices.get(0).getX() && point.getBlockX() <= vertices.get(2).getX() &&
				   point.getBlockZ() >= vertices.get(0).getZ() && point.getBlockZ() <= vertices.get(2).getZ();
		else
			return point.getBlockX() == vertices.get(0).getX() && point.getBlockZ() == vertices.get(0).getZ(); 
	}
	
	public boolean borderContains(Location point) {
		if(!isComplete())
			return false;
		
		return shape.borderContains(point);
	}
	
	public boolean isHighlighted(Block b) {
		Chunk c = b.getChunk();
		
		if(isComplete) {
			if(!shape.getBorder().containsKey(c))
				return false;
			
			for(Location point : shape.getBorder().get(c))
				if(point.getBlock().equals(b))
					return true;
		}
		
		for(Location vertex : vertices)
			if(vertex.getBlock().equals(b))
				return true;
		
		return false;
	}
	
	public boolean isVertex(Block b) {
		if(!isComplete())
			return false;

		for(Location vertex : vertices)
			if(vertex.equals(b.getLocation()))
				return true;
		return false;
	}

	private int indexOfVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return -1;
		
		for(Location vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		return -1;
	}
	
	public boolean isComplete() {
		return isComplete;
	}
	
	private void calcVertices(Location p0, Location p1) {
		int maxY = Math.max(p0.getBlockY(), p1.getBlockY());
		
		int minX = Math.min(p0.getBlockX(), p1.getBlockX()),
			minZ = Math.min(p0.getBlockZ(), p1.getBlockZ()),
			maxX = Math.max(p0.getBlockX(), p1.getBlockX()),
			maxZ = Math.max(p0.getBlockZ(), p1.getBlockZ());
		
		vertices = new ArrayList<>(Arrays.asList(
				Utils.getNearestSurface(new Location(world, minX, maxY, minZ)),
				Utils.getNearestSurface(new Location(world, maxX, maxY, minZ)),
				Utils.getNearestSurface(new Location(world, maxX, maxY, maxZ)),
				Utils.getNearestSurface(new Location(world, minX, maxY, maxZ))));
	}

	public void recalc(Location point) {
		if(shape.borderContains(point)) {
			shape.recalc(point);
		
			if(isVertex(point.getBlock())) {
				int index = indexOfVertex(point.getBlock());
				vertices.set(index, Utils.getNearestSurface(point));
			}	
		}
	}		
	
	private void worldCheck(Block b) {
		//this should only happen if I do a mistake
		if(!b.getWorld().equals(world))
			throw new IllegalArgumentException("The selection's world and the block's world do not match.");
	}
}