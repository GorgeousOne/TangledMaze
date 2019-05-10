package me.gorgeousone.tangledmaze.tool;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shape.Shape;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

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
	
	@Override
	public String getName() {
		return shape.getClass().getSimpleName().toLowerCase();
	}
	
	public World getWorld() {
		return clip.getWorld();
	}
	
	public Shape getType() {
		return shape;
	}

	public boolean isStarted() {
		return !vertices.isEmpty();
	}

	public boolean isComplete() {
		return isComplete;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public void setType(Shape shape) {
		
		Renderer.hideClipboard(this, true);
		this.shape = shape;

		if(isComplete) {

			vertices.remove(3);
			vertices.remove(1);
			calculateShape();
		}
		
		Renderer.displayClipboard(this);
	}
	
	@Override
	public void interact(Block clicked, Action interaction) {
		
		if(clicked.getWorld() != getWorld())
			reset();
		
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
				reset();
				vertices.add(Utils.nearestSurface(clicked.getLocation()));
			}
		}
		
		Renderer.displayClipboard(this);
	}
	
	private void calculateShape() {
		
		clip = shape.createClip(vertices);
		isComplete = true;
		isResizing = false;
	}
	
	private void resizeShape(Block block) {
		
		Renderer.hideClipboard(this, true);
		Location oppositeVertex = vertices.get((indexOfResizedVertex+2) % 4);
		
		vertices.clear();
		vertices.add(oppositeVertex);
		vertices.add(Utils.nearestSurface(block.getLocation()));
		
		calculateShape();
	}
	
	public void reset() {
		
		clip = new Clip(getPlayer() != null ? getPlayer().getWorld() : getWorld());
		vertices.clear();
		isComplete = false;
		isResizing = false;
	}
	
	public ArrayList<Location> getVertices() {
		return vertices;
	}
	
	public boolean isVertex(Block block) {
		
		for(Location vertex : vertices) {
			
			if(vertex.equals(block.getLocation())) {
				return true;
			}
		}
		
		return false;
	}
	
	public int indexOfVertex(Block block) {
		
		for(Location vertex : vertices) {
			
			if(block.getX() == vertex.getX() &&
			   block.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		}
		
		return -1;
	}

	public Block updateHeight(Block block) {
		
		if(!isComplete())
			return null;
		
		Location updatedBlock = Utils.nearestSurface(block.getLocation());
		Vec2 blockVec = new Vec2(block);
		
		getClip().addFill(blockVec, updatedBlock.getBlockY());
			
		return updatedBlock.getBlock();
	}
}