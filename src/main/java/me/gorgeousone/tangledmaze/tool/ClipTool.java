package me.gorgeousone.tangledmaze.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.util.BlockUtils;
import me.gorgeousone.tangledmaze.util.BlockVec;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class ClipTool extends Tool {

	private Clip clip;
	private ClipShape shape;
	private World world;
	private List<BlockVec> vertices;

	private BlockVec shiftedVertex;
	
	public ClipTool(Player player, ClipShape type) {
		super(player);
		
		world = player.getWorld();
		shape = type;
		vertices = new ArrayList<>();
		clip = new Clip(world);
	}
	
	@Override
	public String getName() {
		return shape.getSimpleName();
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
		return !vertices.isEmpty();
	}

	public boolean hasClip() {
		return clip.size() != 0;
	}

	public Clip getClip() {
		return clip;
	}

	public void startShiftingVertex(BlockVec shiftedVertex) {

		if(!hasClip())
			throw new IllegalStateException("Cannot reshape an unfinished clip.");

		if(isBeingReshaped())
			throw new IllegalStateException("Already shifting a vertex of this clip");

		if(!vertices.contains(shiftedVertex))
			throw new IllegalArgumentException("Passed BlockVec is not a vertex of this clip");

		this.shiftedVertex = shiftedVertex;
	}

	public boolean isBeingReshaped() {
		return shiftedVertex != null;
	}

	public BlockVec getShiftedVertex() {
		return shiftedVertex;
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
	
	public List<BlockVec> getVertices() {
		return vertices;
	}

	public void setVertices(List<BlockVec> vertices) {
		this.vertices = vertices;
		shiftedVertex = null;
	}

	public boolean isVertex(Vec2 point) {

		for(BlockVec vertex : vertices) {
			if (vertex.toVec2().equals(point))
				return true;
		}

		return false;
	}

	public boolean isVertex(Block block) {
		return getVertex(block) != null;
	}

	public BlockVec getVertex(Block block) {

		Vector blockPos = block.getLocation().toVector();

		for(BlockVec vertex : vertices) {
			if (vertex.toVector().equals(blockPos))
				return vertex;
		}

		return null;
	}

	public Location updateHeight(Block block) {

		Location updatedBlock = null;
		updatedBlock = BlockUtils.nearestSurface(block.getLocation());

		BlockVec vertex = getVertex(block);

		if(vertex != null)
			vertex.setY(updatedBlock.getBlockY());

		if(hasClip())
			getClip().addFill(new Vec2(block), updatedBlock.getBlockY());

		return updatedBlock;
	}
}