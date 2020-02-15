package me.gorgeousone.tangledmaze.tools;

import me.gorgeousone.tangledmaze.PlayerHolder;
import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipShape;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.BlockVec;
import me.gorgeousone.tangledmaze.utils.Vec2;
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
	private List<BlockVec> vertices;

	private BlockVec shiftedVertex;

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

	public List<BlockVec> getVertices() {
		return vertices;
	}

	public void setVertices(List<BlockVec> vertices) {
		this.vertices = vertices;
		shiftedVertex = null;
	}

	public void startShiftingVertex(BlockVec shiftedVertex) {

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

	public BlockVec getShiftedVertex() {
		return shiftedVertex;
	}

	public boolean isVertex(Vec2 point) {

		for (BlockVec vertex : vertices) {
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

		for (BlockVec vertex : vertices) {
			if (vertex.toVector().equals(blockPos))
				return vertex;
		}

		return null;
	}

	public Block updateHeight(Block block) {

		Block updatedBlock = BlockUtils.nearestSurface(block.getLocation());
		BlockVec vertex = getVertex(block);

		if (vertex != null)
			vertex.setY(updatedBlock.getY());

		if (hasClip())
			getClip().addFill(new Vec2(block), updatedBlock.getY());

		return updatedBlock;
	}
}