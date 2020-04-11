package me.gorgeousone.tangledmaze.maze;

import me.gorgeousone.tangledmaze.clip.ActionHistory;
import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipChange;
import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.MathHelper;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class Maze {
	
	private transient IllegalStateException notAlterableException;
	
	private World world;
	private transient ActionHistory history;
	private Clip clip;
	private Stack<Vec2> exits;
	
	private Map<MazeDimension, Integer> dimensions;
	private BlockComposition blockComposition;
	private boolean isConstructed;
	
	public Maze(World world) {
		this.world = world;
		
		history = new ActionHistory();
		exits = new Stack<>();
		
		dimensions = new HashMap<>();
		blockComposition = new BlockComposition();
		blockComposition.addBlock(Bukkit.createBlockData(Material.STONE), 1);
		
		for (MazeDimension dimension : MazeDimension.values())
			dimensions.put(dimension, dimension.getDefault());
		
		notAlterableException = new IllegalStateException("The maze cannot be altered when it is constructed.");
	}
	
	public World getWorld() {
		return world;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public boolean hasClip() {
		return clip != null;
	}
	
	public Maze setClip(Clip newClip) {
		
		if (newClip.size() == 0)
			throw new IllegalArgumentException("Cannot set clip to an empty Clip");
		
		clip = newClip;
		world = newClip.getWorld();
		exits.clear();
		history.clear();
		
		return this;
	}
	
	public Vec2 getEntrance() {
		return hasExits() ? exits.peek().clone() : null;
	}
	
	public Stack<Vec2> getExits() {
		
		Stack<Vec2> deepClone = new Stack<>();
		
		for (Vec2 exit : exits)
			deepClone.add(exit.clone());
		
		return deepClone;
	}
	
	public Stack<Vec2> getSecondaryExits() {
		
		Stack<Vec2> deepClone = new Stack<>();
		
		if (exits.size() <= 1)
			return deepClone;
		
		for (int i = 0; i < exits.size() - 1; i++)
			deepClone.add(exits.get(i).clone());
		
		return deepClone;
	}
	
	public void addExit(Block block) {
		
		if (!canBeExit(block) || isExit(block))
			return;
		
		exits.push(new Vec2(block));
	}
	
	public void removeExit(Block block) {
		
		if (!canBeExit(block) || !isExit(block))
			return;
		
		exits.remove(new Vec2(block));
	}
	
	public boolean hasExits() {
		return !exits.isEmpty();
	}
	
	public boolean exitsContain(Vec2 point) {
		return exits.contains(point);
	}
	
	public boolean canBeExit(Block block) {
		return getClip().isBorderBlock(block) && getClip().sealsBorder(new Vec2(block), Direction.fourCardinals());
	}
	
	public boolean isExit(Block block) {
		
		Vec2 blockVec = new Vec2(block);
		return exits.contains(blockVec) && getClip().getHeight(blockVec) == block.getY();
	}
	
	public Map<MazeDimension, Integer> getDimensions() {
		return new HashMap<>(dimensions);
	}
	
	public int getDimension(MazeDimension size) {
		return dimensions.get(size);
	}
	
	public void setDimension(MazeDimension size, int newValue) {
		dimensions.put(size, MathHelper.clamp(newValue, 1, size.getMaxValue()));
	}
	
	public BlockComposition getBlockComposition() {
		return blockComposition;
	}
	
	public void setBlockComposition(BlockComposition composition) {
		this.blockComposition = composition;
	}
	
	public ActionHistory getActionHistory() {
		return history;
	}
	
	public boolean isConstructed() {
		return isConstructed;
	}
	
	public void setConstructed(boolean state) {
		isConstructed = state;
	}
	
	public void processAction(ClipChange action, boolean saveToHistory) {
		
		if (isConstructed())
			throw notAlterableException;
		
		for (Vec2 border : action.getRemovedBorder())
			getClip().removeBorder(border);
		
		for (Vec2 fill : action.getRemovedFill().keySet())
			getClip().removeFill(fill);
		
		getClip().addAllFill(action.getAddedFill());
		
		for (Vec2 border : action.getAddedBorder())
			getClip().addBorder(border);
		
		exits.removeAll(action.getRemovedExits());
		
		if (saveToHistory)
			getActionHistory().pushAction(action);
	}
	
	public Block updateHeight(Block block) {
		
		if (isConstructed())
			throw notAlterableException;
		
		Block updatedBlock = BlockUtils.nearestSurface(block.getLocation());
		getClip().addFill(new Vec2(block), updatedBlock.getY());
		
		return updatedBlock;
	}
	
	public void updateHeights() {
		
		if (isConstructed())
			throw notAlterableException;
		
		for (Entry<Vec2, Integer> fill : getClip().getFillEntries()) {
			getClip().addFill(fill.getKey(), BlockUtils.nearestSurfaceY(fill.getKey(), fill.getValue(), getWorld()));
		}
	}
}