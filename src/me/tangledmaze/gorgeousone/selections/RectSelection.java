package me.tangledmaze.gorgeousone.selections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.Utils;
import me.tangledmaze.gorgeousone.shapes.Shape;

public class RectSelection {
	
	private Player p;
	private World world;
	private Shape shape;
	
	private Location firstVertex;
	private ArrayList<Location> vertices;
	private boolean isComplete, isVisible;
	
	private Class<? extends Shape> shapeType;

	public RectSelection(Block firstVertex, Player editor, Class<? extends Shape> shapeType) {
		this.p = editor;
		world = firstVertex.getWorld();
		vertices = new ArrayList<>();
		isComplete = false;
		
		this.firstVertex = Utils.getNearestSurface(firstVertex.getLocation());
		
//		if(p != null) {d
//			Utils.sendBlockLater(editor, this.firstVertex, Constants.SELECTION_BEGINNING);
//			isVisible = true;
//		}
		
		this.shapeType = shapeType;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return p;
	}
	
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
	
	public Shape getShape() {
		return shape;
	}
	
	public void complete(Block b) {
		worldCheck(b);
		
		if(isComplete)
			return;
		
		calcVertices(firstVertex, b.getLocation());
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
	
	public boolean contains(Block b) {
		if(!isComplete())
			return false;
		return b.getX() >= vertices.get(0).getX() && b.getZ() <= vertices.get(2).getX() &&
			   b.getZ() >= vertices.get(0).getZ() && b.getZ() <= vertices.get(2).getZ();
	}
	
	public boolean isVertex(Block b) {
		if(!isComplete() || !b.getWorld().equals(world))
			return false;
		
		for(Location vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
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
		int minX = Math.min(p0.getBlockX(), p1.getBlockX()),
			minZ = Math.min(p0.getBlockZ(), p1.getBlockZ()),
			maxX = Math.max(p0.getBlockX(), p1.getBlockX()),
			maxZ = Math.max(p0.getBlockZ(), p1.getBlockZ());

		
		vertices = new ArrayList<>(Arrays.asList(
				Utils.getNearestSurface(new Location(world, minX, p1.getY(), minZ)),
				Utils.getNearestSurface(new Location(world, maxX, p1.getY(), minZ)),
				Utils.getNearestSurface(new Location(world, maxX, p1.getY(), maxZ)),
				Utils.getNearestSurface(new Location(world, minX, p1.getY(), maxZ))));
	}
	
	private void worldCheck(Block b) {
		//this should only happen if I do a mistake
		if(!b.getWorld().equals(world))
			throw new IllegalArgumentException("The selection's world and the block's world do not match.");
	}
	
	@SuppressWarnings("deprecation")
	public void show() {
		if(isVisible || p == null)
			return;
		isVisible = true;
		
		if(isComplete()) {
			for(ArrayList<Location> chunk : shape.getBorder().values())
				for(Location point : chunk)
					p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
			showVertices();
		}else
			p.sendBlockChange(firstVertex, Constants.SELECTION_CORNER, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void showVertices() {
		for(Location vertex : vertices)
			p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		if(!isVisible || p == null || !isComplete() && firstVertex == null)
			return;
		isVisible = false;
		
		if(isComplete()) {
			for(Location vertex : vertices)
				p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
			for(ArrayList<Location> chunk : shape.getBorder().values())
				for(Location point : chunk)
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}else
			p.sendBlockChange(firstVertex, firstVertex.getBlock().getType(), firstVertex.getBlock().getData());
	}
}