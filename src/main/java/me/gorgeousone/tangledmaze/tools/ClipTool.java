package me.gorgeousone.tangledmaze.tools;

import me.gorgeousone.tangledmaze.PlayerHolder;
import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ClipTool extends PlayerHolder {
	
	private Clip clip;
	private ClipShape shape;
	private World world;
	private List<Location> vertices;
	
	private Location shiftedVertex;
	
	public ClipTool(Player player, ClipShape type) {
		super(player);
		
		world = player.getWorld();
		shape = type;
		vertices = new ArrayList<>();
		clip = new Clip(world);
	}
	
	public String getName() {
		return shape.simpleName();
	}
	
	public World getWorld() {
		return world;
	}
	
	public ClipShape getShape() {
		return shape;
	}
	
	public void setShape(ClipShape shape) {
		this.shape = shape;
	}
	
	/**
	 * Returns true if the clip has anything that can be displayed to the player e.g. just 1 vertex
	 */
	public boolean isStarted() {
		return !vertices.isEmpty();
	}
	
	public boolean hasClip() {
		return clip.size() != 0;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public void setClip(Clip clip) {
		this.clip = clip;
	}
	
	public List<Location> getVertices() {
		return vertices;
	}
	
	public void setVertices(List<Location> vertices) {
		this.vertices = vertices;
		shiftedVertex = null;
	}
	
	public Location getVertex(Block block) {
		return getVertex(new Vec2(block));
	}
	
	/**
	 * Returns the vertex of the clip that is matching the x and z coordinates of the block.
	 */
	public Location getVertex(Vec2 point) {
		
		for (Location vertex : vertices) {
			if (new Vec2(vertex).equals(point))
				return vertex;
		}
		
		return null;
	}
	
	/**
	 * Returns true if the passed block is matching the x and z coordinates of a vertex of the clip.
	 */
	public boolean isVertex(Vec2 point) {
		
		for (Location vertex : vertices) {
			if (new Vec2(vertex).equals(point))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the passed block is matching the exact position of a vertex of the clip.
	 */
	public boolean isVertexBlock(Block block) {
		
		Vector blockPos = block.getLocation().toVector();
		
		for (Location vertex : vertices) {
			if (vertex.toVector().equals(blockPos))
				return true;
		}
		
		return false;
	}
	
	public void startShiftingVertex(Location shiftedVertex) {
		
		if (!hasClip())
			throw new IllegalStateException("Cannot reshape an unfinished clip.");
		
		if (isBeingReshaped())
			throw new IllegalStateException("Already shifting a vertex of this clip");
		
		if (!vertices.contains(shiftedVertex))
			throw new IllegalArgumentException("Passed BlockVec is not a vertex of this clip");
		
		this.shiftedVertex = shiftedVertex;
	}
	
	public boolean isBeingReshaped() {
		return shiftedVertex != null;
	}
	
	public Location getShiftedVertex() {
		return shiftedVertex;
	}
	
	public Block updateHeight(Block block) {
		
		Block updatedBlock = BlockUtils.nearestSurface(block.getLocation());
		Vec2 blockPoint = new Vec2(block);
		
		if (isVertex(blockPoint))
			getVertex(blockPoint).setY(updatedBlock.getY());
		
		if (hasClip())
			getClip().addFill(new Vec2(block), updatedBlock.getY());
		
		return updatedBlock;
	}
}