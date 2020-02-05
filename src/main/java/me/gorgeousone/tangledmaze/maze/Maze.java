package me.gorgeousone.tangledmaze.maze;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.util.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.*;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Maze {
	
	private static IllegalStateException notAlterableException = new IllegalStateException("The maze cannot be altered when it is constructed.");
	
	private UUID builder;
	private ActionHistory history;
	private Clip clip;
	private Stack<Vec2> exits;
	
	private Map<MazeDimension, Integer> dimensions;
	private BlockComposition blockComposition;
	private boolean isStarted, isConstructed;
	
	public Maze(World world) {
		
		clip = new Clip(world);
		history = new ActionHistory();
		exits = new Stack<>();

		dimensions = new HashMap<>();
		blockComposition = new BlockComposition();
		blockComposition.addBlock(Bukkit.createBlockData(Material.STONE), 1);

		for(MazeDimension dimension : MazeDimension.values())
			dimensions.put(dimension, dimension.getDefault());
	}

	public Maze(Player builder) {
		
		this(builder.getWorld());
		this.builder = builder.getUniqueId();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(builder);
	}

	public void setPlayer(Player newBuilder) {
		builder = newBuilder.getUniqueId();
	}

	public World getWorld() {
		return clip.getWorld();
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public boolean isConstructed() {
		return isConstructed;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public Maze setClip(Clip newClip) {
		
		clip = newClip;
		exits.clear();
		history.clear();
		
		isStarted = true;
		return this;
	}
	
	public Stack<Vec2> getExits() {
		Stack<Vec2> deepCopy = new Stack<>();
		
		for(Vec2 exit : exits)
			deepCopy.push(exit.clone());
		
		return deepCopy;
	}
	
	public boolean hasExits() {
		return !exits.isEmpty();
	}
	
	public Vec2 getMainExit() {
		return hasExits() ? exits.peek().clone() : null;
	}
	
	public ActionHistory getActionHistory() {
		return history;
	}

	public int getDimension(MazeDimension size) {
		return dimensions.get(size);
	}

	public BlockComposition getBlockComposition() {
		return blockComposition;
	}

	public void setDimension(MazeDimension size, int newValue) {
		dimensions.put(size, Utils.clamp(newValue, 1, size.getMaxValue()));
	}

	public void setBlockComposition(BlockComposition composition) {
		this.blockComposition = composition;
	}
	
	public void setConstructed(boolean state) {
		isConstructed = state;
	}

	public boolean exitsContain(Vec2 loc) {
		return exits.contains(loc);
	}

	public boolean addExit(Vec2 point) {

		if(!getClip().borderContains(point))
			return false;

		return addExit(getClip().getLocation(point).getBlock());
	}

	public boolean addExit(Block block) {
		
		if(!canBeExit(block) || isExit(block))
			return false;
		
		exits.push(new Vec2(block));
		return true;
	}

	public boolean removeExit(Block block) {
		
		if(!canBeExit(block) || !isExit(block))
			return false;
		
		exits.remove(new Vec2(block));
		return true;
	}

	public boolean canBeExit(Block block) {
		return
			getClip().isBorderBlock(block) &&
			sealsMaze(block.getLocation(), Directions.cardinalValues());
	}
	
	public boolean isExit(Block block) {
		
		Vec2 blockVec = new Vec2(block);
		return exits.contains(blockVec) && getClip().getHeight(blockVec) == block.getY();
	}

	/**Applies a ClipAction which can be created e.g. with {@link #getAddition(Clip)}.
	 * The method will change the clip of the maze according to the ClipAction,
	 * the visual change of the maze to the player is left to the method caller (see {@link me.gorgeousone.tangledmaze.handler.Renderer#displayMazeAction(Maze, ClipAction)}).
	 * @param action beforehand created ClipAction
	 * @param saveToHistory option to add the action to the maze's {@link ActionHistory} (for accessing it later e. g. to undo it)
	 */
	public void processAction(ClipAction action, boolean saveToHistory) {
		
		if(isConstructed())
			throw notAlterableException;
		
		for(Vec2 border : action.getRemovedBorder())
			getClip().removeBorder(border);
		
		for(Vec2 fill : action.getRemovedFill().keySet())
			getClip().removeFill(fill);

		getClip().addAllFill(action.getAddedFill());
		
		for(Vec2 border : action.getAddedBorder())
			getClip().addBorder(border);
		
		exits.removeAll(action.getRemovedExits());

		if(saveToHistory)
			getActionHistory().pushAction(action);
	}

	/**Creates a ClipAction of the part of the passed clip that will be added to the maze.
	 * Returns null if worlds are not matching or the clip is completely covered by the maze.
	 * To apply the action use {@link #processAction(ClipAction, boolean)}
	 * @param clip Clip to be added to the maze
	 * @return ClipAction of the addition
	 */
	public ClipAction getAddition(Clip clip) {
	
		if(!getWorld().equals(clip.getWorld()))
			return null;
		
		ClipAction addition = new ClipAction(getClip());
		addOtherProtrudingFill(clip, addition);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return null;

		addOtherProtrudingBorder(clip, addition);
		removeOwnEnclosedBorder(addition);
		removeNewEnclosedBorder(addition);
		removeExitsInsideClip(clip, addition);
		return addition;
	}
	
	//every fill not included by this maze yet is being added
	private void addOtherProtrudingFill(Clip otherClip, ClipAction addition) {

		for(Entry<Vec2, Integer> otherFill : otherClip.getFillEntries()) {
			if(!getClip().contains(otherFill.getKey()))
				addition.addFill(otherFill.getKey(), otherFill.getValue());
		}
	}
	
	//right afterwards also the border of the other clip is being added
	private void addOtherProtrudingBorder(Clip otherClip, ClipAction addition) {
	
		for(Vec2 otherBorder : otherClip.getBorder()) {
			if(!getClip().contains(otherBorder))
				addition.addBorder(otherBorder);
		}
	}
	
	//then own outdated border is being removed. there are also cases where thicker border next to the actual clip has to be removed
	private void removeOwnEnclosedBorder(ClipAction addition) {
		
		for(Vec2 ownBorder : getClip().getBorder()) {
			if(!touchesExternal(ownBorder, addition, Directions.values()))
				addition.removeBorder(ownBorder);
		}
	}
	
	//now the recently added border needs undergo another check, if it is actually sufficient and also not too thick
	private void removeNewEnclosedBorder(ClipAction addition) {
		addition.getAddedBorder().removeIf(newBorder -> !touchesExternal(newBorder, addition, Directions.values()));
	}
	
	private void removeExitsInsideClip(Clip otherClip, ClipAction changes) {
	
		for(Vec2 exit : exits) {
			if(otherClip.contains(exit))
				changes.removeExit(exit);
		}
	}

	/**Creates a ClipAction of the part of the passed clip that will be removed from the maze.
	 * Returns null if worlds are not matching or the clip is not intersecting the maze.
	 * To apply the action use {@link #processAction(ClipAction, boolean)}
	 * @param clip Clip to be removed from the maze
	 * @return ClipAction of the deletion
	 */
	public ClipAction getDeletion(Clip clip) {
		
		if(!getWorld().equals(clip.getWorld()))
			return null;
		
		ClipAction deletion = new ClipAction(getClip());
		removeOtherOverlappingClip(clip, deletion);
		
		if(deletion.getRemovedFill().isEmpty())
			return null;
		
		addOtherIntersectingBorder(clip, deletion);
		removeOwnExcludedBorder(clip, deletion);
		removeExitsInsideClip(clip, deletion);
		return deletion;
	}
	
	//every fill of the other clip overlapping the maze is being removed from the maze
	private void removeOtherOverlappingClip(Clip otherClip, ClipAction deletion) {
		
		for(Entry<Vec2, Integer> otherFill : otherClip.getFillEntries()) {
			if(!otherClip.borderContains(otherFill.getKey()) && getClip().contains(otherFill.getKey()))
				deletion.removeFill(otherFill.getKey(), otherFill.getValue());
		}
	}
	
	private void addOtherIntersectingBorder(Clip otherClip, ClipAction deletion) {
		
		for(Vec2 otherBorder : otherClip.getBorder()) {
			if(!getClip().borderContains(otherBorder) && getClip().contains(otherBorder))
				deletion.addBorder(otherBorder);
		}
		
		//remove every part of the new added border, which is not functional border anyway
		Iterator<Vec2> iterator = deletion.getAddedBorder().iterator();
		
		while (iterator.hasNext()) {
			Vec2 newBorder = iterator.next();
			
			if(!touchesFill(newBorder, deletion, Directions.values())) {
				iterator.remove();
				deletion.removeFill(newBorder, getClip().getHeight(newBorder));
			}
		}
	}

	private void removeOwnExcludedBorder(Clip otherClip, ClipAction deletion) {
		
		for(Vec2 ownBorder : getClip().getBorder()) {
			
			if(otherClip.contains(ownBorder) && !touchesFill(ownBorder, deletion, Directions.values())) {
				deletion.removeBorder(ownBorder);
				deletion.removeFill(ownBorder, getClip().getHeight(ownBorder));
			}
		}
	}

	/**Creates a ClipAction with information about blocks in order to expand the maze border at the given block.
	 * Returns null if the block is not part of the maze border (see {@link Clip#isBorderBlock(Block)}).
	 * To apply the action use {@link #processAction(ClipAction, boolean)}
	 * @param block the block where the maze border should be expanded
	 * @return ClipAction of the expansion
	 */
	public ClipAction getExpansion(Block block) {
		
		if(!getClip().isBorderBlock(block))
			return null;
		
		Vec2 blockVec = new Vec2(block);
		ClipAction expansion = new ClipAction(getClip());
		
		expandBorder(blockVec, expansion);
		removeIntrusiveBorder(blockVec, expansion);
		return expansion;
	}
	
	private void expandBorder(Vec2 loc, ClipAction expansion) {
		
		expansion.removeBorder(loc);
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			int height = BlockUtils.nearestSurfaceY(neighbor, getClip().getHeight(loc), getWorld());
			
			if(!getClip().contains(neighbor)) {
				
				expansion.addFill(neighbor, height);
				expansion.addBorder(neighbor);
				
			}else if(exitsContain(neighbor) && !sealsMaze(neighbor, expansion, Directions.cardinalValues()))
				expansion.removeExit(neighbor);
		}
	}
	
	private void removeIntrusiveBorder(Vec2 loc, ClipAction expansion) {
		//look for neighbors, that are now intruding the border unnecessarily
		for(Directions dir : Directions.values()) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());

			if(getClip().borderContains(neighbor) && !sealsMaze(neighbor, expansion, Directions.values()))
				expansion.removeBorder(neighbor);
		}
	}

	/**Creates a ClipAction with information about blocks in order to reduce the maze border at the given block.
	 * Returns null if the block is not part of the maze border (see {@link Clip#isBorderBlock(Block)}).
	 * To apply the action use {@link #processAction(ClipAction, boolean)}
	 * @param block the block where the maze border should be reduced/erased
	 * @return ClipAction of the expansion
	 */
	public ClipAction getErasure(Block block) {
		
		if(!getClip().isBorderBlock(block))
			return null;
		
		Vec2 blockVec = new Vec2(block);
		ClipAction action = new ClipAction(getClip());
		
		action.removeBorder(blockVec);
		
		reduceBorder(blockVec, action);
		removeProtrusiveBorder(blockVec, action);
		return action;
	}
	
	private void reduceBorder(Vec2 loc, ClipAction erasure) {
		
		if(exitsContain(loc))
			erasure.removeExit(loc);
		
		erasure.removeBorder(loc);
		erasure.removeFill(loc, getClip().getHeight(loc));
		
		if(!sealsMaze(loc, erasure, Directions.values()))
			return;
		
		for(Directions dir : Directions.values()) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			
			if(getClip().contains(neighbor) && !getClip().borderContains(neighbor))
				erasure.addBorder(neighbor);
			
			if(exitsContain(neighbor) && !sealsMaze(neighbor, erasure, Directions.cardinalValues()))
				erasure.removeExit(neighbor);
		}
	}
	
	private void removeProtrusiveBorder(Vec2 loc, ClipAction erasure) {
		//detect outstanding neighbor borders of the block
		for(Directions dir : Directions.values()) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			
			//remove the neighbor if it still stands out
			if(getClip().borderContains(neighbor) && !sealsMaze(neighbor, erasure, Directions.values())) {
				
				int height = getClip().getHeight(neighbor);
				erasure.removeFill(neighbor, height);
			}
		}
	}
	
	public boolean sealsMaze(Location loc, Directions[] directions) {
		return sealsMaze(new Vec2(loc), new ClipAction(getClip()), directions);
	}
	
	public boolean sealsMaze(Vec2 loc, ClipAction changes, Directions[] directions) {
		
		boolean touchesFill = false;
		boolean touchesExternal = false;
		
		for(Directions dir : directions) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			
			if(!changes.clipWillContain(neighbor))
				touchesExternal = true;

			else if(!changes.clipBorderWillContain(neighbor))
				touchesFill = true;
			
			if(touchesFill && touchesExternal)
				return true;
		}
		
		return false;
	}

	public boolean touchesFill(Vec2 loc, ClipAction changes, Directions[] directions) {
		
		for(Directions dir : directions) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			
			if(!changes.clipBorderWillContain(neighbor) && changes.clipWillContain(neighbor))
				return true;
		}
		
		return false;
	}

	public boolean touchesExternal(Vec2 loc, ClipAction changes, Directions[] directions) {
		
		for(Directions dir : directions) {
			Vec2 neighbor = loc.clone().add(dir.getVec2());
			
			if(!changes.clipWillContain(neighbor))
				return true;
		}
		
		return false;
	}

	public void updateHeights() {
		
		if(isConstructed())
			throw notAlterableException;

		for(Entry<Vec2, Integer> fill : getClip().getFillEntries())
			getClip().addFill(fill.getKey(), BlockUtils.nearestSurfaceY(fill.getKey(), fill.getValue(), getWorld()));
	}
	
	public Location updateHeight(Block block) {
		
		if(isConstructed())
			throw notAlterableException;
		
		Location updatedBlock = BlockUtils.nearestSurface(block.getLocation());
		getClip().addFill(new Vec2(block), updatedBlock.getBlockY());
			
		return updatedBlock;
	}
}