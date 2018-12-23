package me.gorgeousone.tangledmaze.tools;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shapes.Shape;
import me.gorgeousone.tangledmaze.utils.Utils;

public class ClippingTool extends Tool {

	private Shape shape;
	
	private Clip clip;
	private ArrayList<Location> vertices;
	
	private boolean isComplete, isResizing;
	private int indexOfResizedVertex;
	
	public ClippingTool(World world, Shape type) {
		super(null);
		
		clip = new Clip(world);
		shape = type;
		vertices = new ArrayList<>();
	}
	
	public ClippingTool(Player builder, Shape type) {
		super(builder);
		
		clip = new Clip(builder.getWorld()); 
		shape = type;
		vertices = new ArrayList<>();
	}
	
	public void setType(Shape shape) {
		this.shape = shape;
		
		//stop code from deleting the vertex so another shape can be created with an already set vertex
		if(!isComplete)
			return;
		
		vertices.remove(3);
		vertices.remove(1);
		
		Renderer.hideClipboard(this, true);
		calculateShape();
		Renderer.showClipboard(this);
	}
	
	//think about cleanness
	public World getWorld() {
		return clip.getWorld();
	}
	
	public Shape getType() {
		return shape;
	}
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	@Override
	public void interact(Block clicked, Action action) {
		
		if(clicked.getWorld() != getWorld()) {
			reset(clicked.getWorld());
		}
		
		if(vertices.size() < shape.getVertexCount()-1) {
			vertices.add(Utils.nearestSurface(clicked.getLocation()));
			
		}else if(vertices.size() == shape.getVertexCount()-1) {
			vertices.add(Utils.nearestSurface(clicked.getLocation()));
			calculateShape();
			
		}else {

			if(isResizing) {
				resizeShape(clicked);
			
			}else if(isVertex(clicked)) {
				indexOfResizedVertex = indexOfVertex(clicked);
				isResizing = true;
				return;
				
			}else {
				Renderer.hideClipboard(this, true);
				reset(getWorld());
				vertices.add(Utils.nearestSurface(clicked.getLocation()));
			}
		}
		
		Renderer.showClipboard(this);
	}
	
	private void calculateShape() {
		
		clip = shape.createClip(vertices);
		isComplete = true;
		isResizing = false;
	}
	
	private void resizeShape(Block b) {
		
		Renderer.hideClipboard(this, true);
		Location oppositeVertex = vertices.get((indexOfResizedVertex+2) % 4);
		
		vertices.clear();
		vertices.add(oppositeVertex);
		vertices.add(Utils.nearestSurface(b.getLocation()));
		
		calculateShape();
	}
	
	public void reset(World world) {
		
		Renderer.hideClipboard(this, true);

		clip = new Clip(world);
		vertices.clear();
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
		
		if(!isComplete() || !b.getWorld().equals(getWorld()))
			return -1;
		
		for(Location vertex : vertices) {
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		}
		
		return -1;
	}

	public boolean isHighlighted(Block b) {
		
		if(!isComplete() ||!b.getWorld().equals(getWorld()))
			return false;
		
		return false;
		//TODO find solution
	}
	
	public void updateHeight(Location point) {
		
		//TODO update
//		if(!point.getWorld().equals(getWorld()))
//			return;
//		
//		if(!fillChunks.containsKey(point.getChunk()))
//			return;
//			
//		ArrayList<Location>	fill = fillChunks.get(point.getChunk());
//		
//		if(fill.contains(point)) {
//			
//			Location newPoint = Utils.nearestSurface(point);
//			fill.set(fill.indexOf(point), newPoint);
//
//			ArrayList<Location>	border = borderChunks.get(point.getChunk());
//
//			if(border.contains(point))
//				border.set(border.indexOf(point), newPoint);
//		}
	}
}