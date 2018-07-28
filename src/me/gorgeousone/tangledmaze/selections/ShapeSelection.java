package me.gorgeousone.tangledmaze.selections;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shapes.Shape;
import me.gorgeousone.tangledmaze.utils.Utils;

public class ShapeSelection extends Selection {

	private World world;
	private Shape shape;
	
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> vertices;
	private int size, borderSize;
	
	private boolean isComplete, isResizing;
	private int indexOfResizedVertex;
	
	
	public ShapeSelection(Player builder, Shape type) {
		super(builder);
		
		if(builder != null)
			world = builder.getWorld();
		
		shape = type;
		
		vertices = new ArrayList<>();
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();
	}
	
	public void setType(Shape type) {
		reset();
		shape = type;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Shape getType() {
		return shape;
	}
	
	public boolean isComplete() {
		return isComplete;
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

	public int borderSize() {
		return borderSize;
	}
	
	@Override
	public void interact(Block b, Action a) {
		
		if(b.getWorld() != world) {
			reset();
			world = b.getWorld();
			Renderer.showSelection(this);

		}else if(vertices.isEmpty()) {
			vertices.add(Utils.nearestSurface(b.getLocation()));

		}else if(vertices.size() == 1) {
			completeShape(b);
			
		}else {

			if(isResizing) {
				resizeShape(b);
			
			}else if(isVertex(b)) {
				indexOfResizedVertex = indexOfVertex(b);
				isResizing = true;
				return;
				
			}else {
				Renderer.hideShape(this, true);
				reset();
				vertices.add(Utils.nearestSurface(b.getLocation()));
			}
		}
		
		Renderer.showSelection(this);
	}
	
	private void completeShape(Block b) {
		
		vertices.add(Utils.nearestSurface(b.getLocation()));
		shape.calcFillAndBorder(vertices, fillChunks, borderChunks);
		isComplete = true;
		
		for(ArrayList<Location> chunk : fillChunks.values())
			size += chunk.size();
		
		for(ArrayList<Location> chunk : borderChunks.values())
			borderSize += chunk.size();
	}
	
	private void resizeShape(Block b) {
		
		Renderer.hideShape(this, true);
		Location oppositeVertex = vertices.get((indexOfResizedVertex+2) % 4);
		
		vertices.clear();
		vertices.add(oppositeVertex);
		vertices.add(Utils.nearestSurface(b.getLocation()));
		
		size = 0;
		borderSize = 0;
		fillChunks.clear();
		borderChunks.clear();
		
		isResizing = false;
		completeShape(b);
	}
	
	public void reset() {
		Renderer.hideShape(this, true);

		fillChunks.clear();
		borderChunks.clear();
		vertices.clear();
		
		size = 0;
		borderSize = 0;
		
		isComplete = false;
		isResizing = false;
	}
	
	public ArrayList<Location> getVertices() {
		return vertices;
	}
	
	public boolean isVertex(Block b) {
		if(!isComplete)
			return false;

		for(Location vertex : vertices) {
			if(vertex.equals(b.getLocation()))
				return true;
		}
		
		return false;
	}
	
	public int indexOfVertex(Block b) {
		if(!isComplete || !b.getWorld().equals(world))
			return -1;
		
		for(Location vertex : vertices) {
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		}
		
		return -1;
	}

	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Chunk chunk = point.getChunk();
		
		if(!fillChunks.containsKey(chunk))
			return false;
		
		for(Location point2 : fillChunks.get(chunk)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}

	public boolean borderContains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Chunk chunk = point.getChunk();
		
		if(!borderChunks.containsKey(chunk))
			return false;
		
		for(Location point2 : borderChunks.get(chunk)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
}