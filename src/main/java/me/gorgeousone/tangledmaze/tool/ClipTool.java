package me.gorgeousone.tangledmaze.tool;

import java.util.ArrayList;
import java.util.List;

import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.util.BlockUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.event.block.Action;

public class ClipTool extends Tool {

	private Clip clip;
	private ClipShape shape;
	private World world;
	private List<Location> controlPoints;
	
//	private boolean isComplete, isBeingResized;
//	private int indexOfResizedVertex;
	
	public ClipTool(Player player, ClipShape type) {
		super(player);
		
		world = player.getWorld();
		shape = type;
		controlPoints = new ArrayList<>();
	}
	
	@Override
	public String getName() {
		return shape.getClass().getSimpleName().toLowerCase();
	}

	@Override
	public void interact(Block clickedBlock, Action interaction) {
		//TODO remove interact method in Tool.class
	}

	public World getWorld() {
		return world;
	}
	
	public ClipShape getShape() {
		return shape;
	}

	public boolean isStarted() {
		return !controlPoints.isEmpty();
	}

	public boolean hasClip() {
		return clip != null;
	}

//	public boolean isBeingResized() {
//		return isBeingResized;
//	}

//	public int getIndexOfResizedVertex() {
//		return indexOfResizedVertex;
//	}

//	public void setBeingResizing(boolean state) {
//		isBeingResized = state;
//	}

//	public void setIndexOfResizedVertex(int indexOfResizedVertex) {
//		this.indexOfResizedVertex = indexOfResizedVertex;
//	}

	public Clip getClip() {
		return clip;
	}

	public void setClip(Clip clip) {
		this.clip = clip;
	}

	//	public void setType(ClipShape shape) {
//
//		Renderer.hideClipboard(this, true);
//		this.shape = shape;
//
//		if(isComplete) {
//
//			vertices.remove(3);
//			vertices.remove(1);
//			calculateShape();
//		}
//
//		Renderer.displayClipboard(this);
//	}
	
//	@Override
//	public void interact(Block clickedBlock, Action interaction) {
//
//		if(clickedBlock.getWorld() != getWorld())
//			reset();
//
//		if(vertices.size() < shape.getVertexCount()-1) {
//			vertices.add(BlockUtils.nearestSurface(clickedBlock.getLocation()));
//
//		}else if(vertices.size() == shape.getVertexCount()-1) {
//
//			vertices.add(BlockUtils.nearestSurface(clickedBlock.getLocation()));
//			calculateShape();
//
//		}else {
//
//			if(isBeingResized) {
//				resizeShape(clickedBlock);
//
//			}else if(isVertex(clickedBlock)) {
//
//				indexOfResizedVertex = indexOfVertex(clickedBlock);
//				isBeingResized = true;
//				return;
//
//			}else {
//
//				Renderer.hideClipboard(this, true);
//				reset();
//				vertices.add(BlockUtils.nearestSurface(clickedBlock.getLocation()));
//			}
//		}
//
//		Renderer.displayClipboard(this);
//	}
	
//	private void calculateShape() {
//
//		clip = shape.createClip(vertices);
//		isComplete = true;
//		isBeingResized = false;
//	}
	
//	private void resizeShape(Block block) {
//
//		Renderer.hideClipboard(this, true);
//		Location oppositeVertex = vertices.get((indexOfResizedVertex+2) % 4);
//
//		vertices.clear();
//		vertices.add(oppositeVertex);
//		vertices.add(BlockUtils.nearestSurface(block.getLocation()));
//
//		calculateShape();
//	}
	
//	public void reset() {
//
//		clip = new Clip(getPlayer() != null ? getPlayer().getWorld() : getWorld());
//		vertices.clear();
//		isComplete = false;
//		isBeingResized = false;
//	}
	
	public List<Location> getControlPoints() {
		return controlPoints;
	}

	public void setControlPoints(List<Location> controlPoints) {
		this.controlPoints = controlPoints;
	}

	public boolean verticesContain(Vec2 loc) {
		
		for(Location vertex : controlPoints) {
			if(vertex.getX() == loc.getX() && vertex.getZ() == loc.getZ())
				return true;
		}
		
		return false;
	}

	public boolean isVertex(Block block) {
		
		if(getWorld() != block.getWorld())
			return false;
		
		Location loc = block.getLocation();
		
		for(Location vertex : controlPoints) {
			if(vertex.equals(loc))
				return true;
		}
		
		return false;
	}
	
	public int indexOfVertex(Block block) {
		
		for(Location vertex : controlPoints) {
			
			if(block.getX() == vertex.getX() &&
			   block.getZ() == vertex.getZ())
				return controlPoints.indexOf(vertex);
		}
		
		return -1;
	}

	public Location updateHeight(Block block) {
		
		Location updatedBlock = null;

		if(isVertex(block)) {
			updatedBlock = BlockUtils.nearestSurface(block.getLocation());
			controlPoints.get(indexOfVertex(block)).setY(updatedBlock.getBlockY());
		}
		
		if(!hasClip())
			return updatedBlock;
		else
			updatedBlock = BlockUtils.nearestSurface(block.getLocation());

		getClip().addFill(new Vec2(block), updatedBlock.getBlockY());
		
		return updatedBlock;
	}
}