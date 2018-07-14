package me.tangledmaze.gorgeousone.selections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Utils;

public class ShapeSelection extends Selection {
	
	protected World world;
	protected Shape shape;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private int size;
	
	protected ArrayList<Location> vertices;
	protected boolean isComplete;
	
	public ShapeSelection(Player p, Shape s) {
		super(p);
		world = p.getWorld();
		
		shape = s;
		vertices = new ArrayList<>();
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		
		isComplete = false;
	}
	
	public void interact(Block b, Action a) {
		
		
	}
	
	
	public Player getPlayer() {
		return p;
	}
	
	public World getWorld() {
		return world;
	}

	public Shape getShape() {
		return shape;
	}
	
	public ArrayList<Location> getVertices() {
		return vertices;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	public int size() {
		return size;
	}
	
	public boolean contains(Location point) {
		return shape.contains(vertices, point);
	}
	
	public boolean borderContains(Location point) {
		return shape.borderContains(vertices, point);
	}
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public  boolean frames(Location point) {
		return false;
	}
	
	public boolean isHighlighted(Block b) {
		Chunk c = b.getChunk();

		if(isComplete) {
			if(!borderChunks.containsKey(c))
				return false;
			
			for(Location point : borderChunks.get(c))
				if(point.getBlock().equals(b))
					return true;
		}
		
		for(Location vertex : vertices)
			if(vertex.getBlock().equals(b))
				return true;
		
		return false;
	}
	
	public boolean isVertex(Block b) {
		if(!isComplete)
			return false;

		for(Location vertex : vertices)
			if(vertex.equals(b.getLocation()))
				return true;
		return false;
	}
	
	public int indexOfVertex(Block b) {
		if(!isComplete || !b.getWorld().equals(world))
			return -1;
		
		for(Location vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		return -1;
	}
	
	public void recalc(Location point) {
		if(!point.getWorld().equals(world))
			return;

		Location newPoint = Utils.nearestSurface(point);

		if(isVertex(point.getBlock()))
			vertices.set(indexOfVertex(point.getBlock()), newPoint);
			
		if(!isComplete)
			return;
		
		ArrayList<Location>	fill = fillChunks.get(point.getChunk());

		if(fill.contains(point)) {

			fill.set(fill.indexOf(point), newPoint);
			ArrayList<Location>	border = borderChunks.get(point.getChunk());

			if(shape.borderContains(vertices, point))
				border.set(border.indexOf(point), newPoint);
		}
	}
	
	public static ArrayList<Location> calcRectangleVertices(Location p0, Location p1) {
		ArrayList<Location> vertices = new ArrayList<>();
		World w = p0.getWorld();
		
		int maxY = Math.max(p0.getBlockY(), p1.getBlockY());
		
		int minX = Math.min(p0.getBlockX(), p1.getBlockX()),
			minZ = Math.min(p0.getBlockZ(), p1.getBlockZ()),
			maxX = Math.max(p0.getBlockX(), p1.getBlockX()),
			maxZ = Math.max(p0.getBlockZ(), p1.getBlockZ());
		
		vertices = new ArrayList<>(Arrays.asList(
				Utils.nearestSurface(new Location(w, minX, maxY, minZ)),
				Utils.nearestSurface(new Location(w, maxX, maxY, minZ)),
				Utils.nearestSurface(new Location(w, maxX, maxY, maxZ)),
				Utils.nearestSurface(new Location(w, minX, maxY, maxZ))));
		
		return vertices;
	}
	
//	f(vertices.isEmpty()) {
//		
//		Bukkit.getPluginManager().callEvent(new SelectionStartEvent(p, b));
//		
//		//TODO cancel if b in other territory, else
//		
//		vertices.add(Utils.nearestSurface(b.getLocation()));
//	
//	}else if(vertices.size() == 1) {
//		
//		//TODO cancel if b in other territory, else
//
//		vertices = calcRectangleVertices(vertices.get(0), Utils.nearestSurface(b.getLocation()));
//		
//		Bukkit.getPluginManager().callEvent(new SelectionCompleteEvent(p, b, null));
//	
//	}else if(vertices.size() == 4) {
//		
//		vertices.clear();
//		
//		Bukkit.getPluginManager().callEvent(new SelectionStartEvent(p, b));
//		
//		//TODO cancel if in other territory, else
//		
//		vertices.add(Utils.nearestSurface(b.getLocation()));
//	}
}