package me.gorgeousone.tangledmaze.core;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.clip.*;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Maze {
	
	private UUID builder;
	
	private ActionHistory history;
	private Clip clip;
	private Stack<Vec2> exits;
	private List<Material> wallMaterials;
	private List<BlockState> builtBlocks;
	
	private Vector dimensions;
	private boolean isStarted, isConstructed;
	
	private IllegalStateException notAlterableException = new IllegalStateException("The maze cannot be altered when it is constructed");
	
	public Maze(World world) {
		
		clip = new Clip(world);
		history = new ActionHistory();
		exits = new Stack<>();
		dimensions = new Vector(1, 2, 1);
	}

	public Maze(Player builder) {
		
		this(builder.getWorld());
		this.builder = builder.getUniqueId();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(builder);
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
	
	public void setClip(Clip clip) {
		
		if(getClip().size() != 0)
			Renderer.hideMaze(this);

		this.clip = clip;
		isStarted = true;
		Renderer.displayMaze(this);
	}
	
	public Stack<Vec2> getExits() {
		return exits;
	}
	
	public Vec2 getMainExit() {
		return getExits().isEmpty() ? null : getExits().peek();
	}
	
	public ActionHistory getActionHistory() {
		return history;
	}
	
	public int getPathWidth() {
		return dimensions.getBlockX();
	}
	
	public void setPathWidth(int pathWidth) {
		dimensions.setX(Math.max(1, pathWidth));
	}

	public int getWallHeight() {
		return dimensions.getBlockY();
	}
	
	public void setWallHeight(int wallHeight) {
		dimensions.setY(Math.max(1, wallHeight));
	}
	
	public int getWallWidth() {
		return dimensions.getBlockZ();
	}
	
	public void setWallWidth(int wallWidth) {
		dimensions.setZ(Math.max(1, wallWidth));
	}
	
	public List<Material> getWallMaterials() {
		return wallMaterials;
	}
	
	public void setWallMaterials(List<Material> composition) {
		wallMaterials = composition;
	}
	
	public List<BlockState> getBuiltBlocks() {
		return builtBlocks;
	}
	
	public void setBuiltBlocks(List<BlockState> builtBlocks) {
		
		this.builtBlocks = builtBlocks;
		
		if(builtBlocks != null)
			isConstructed = true;
		else
			isConstructed = false;
	}
	
	public boolean exitsContain(Vec2 loc) {
		return getExits().contains(loc);
	}

	public boolean isExit(Block block) {
		
		Vec2 blockVec = new Vec2(block);
		
		if(getExits().contains(blockVec) && getClip().getHeight(blockVec) == block.getY())
			return true;
		
		return false;
	}
	
	public boolean canBeExit(Block block) {
		
		if(!getClip().isBorderBlock(block))
			return false;
		
		return sealsMaze(block.getLocation(), Directions.cardinalValues());
	}
	
	public void toggleExit(Block block) {
		
		if(!getClip().isBorderBlock(block))
			return;
		
		Vec2 clickedLoc = new Vec2(block);
		
		if(!canBeExit(block)) {
			Renderer.sendBlockDelayed(getPlayer(), block.getLocation(), Constants.MAZE_BORDER);
			return;
		}
		
		if(isExit(block)) {
			
			getExits().remove(clickedLoc);
			Renderer.sendBlockDelayed(getPlayer(), block.getLocation(), Constants.MAZE_BORDER);

			if(!getExits().isEmpty())
				Renderer.sendBlockDelayed(getPlayer(), getClip().getLocation(getMainExit()), Constants.MAZE_MAIN_EXIT);
			
		}else {

			if(!getExits().isEmpty())
				Renderer.sendBlockDelayed(getPlayer(), getClip().getLocation(getMainExit()), Constants.MAZE_EXIT);
			
			getExits().push(clickedLoc);
			Renderer.sendBlockDelayed(getPlayer(), block.getLocation(), Constants.MAZE_MAIN_EXIT);
		}
	}

	public void processAction(ClipAction action, boolean saveToHistory) {
		
		if(isConstructed())
			throw notAlterableException;
		
		for(Vec2 border : action.getRemovedBorder())
			getClip().removeBorder(border);
		
		for(Vec2 fill : action.getRemovedFill().keySet())
			getClip().removeFill(fill);

		for(Vec2 fill : action.getAddedFill().keySet())
			getClip().addFill(fill, action.getAddedFill().get(fill));
		
		for(Vec2 border : action.getAddedBorder())
			getClip().addBorder(border);
		
		getExits().removeAll(action.getRemovedExits());

		if(saveToHistory)
			getActionHistory().pushAction(action);
		
		Renderer.displayMazeAction(this, action);
	}
	
	public ClipAction getAddition(Clip otherClip) {
	
		if(!getWorld().equals(otherClip.getWorld()))
			return null;
		
		ClipAction addition = new ClipAction(getClip());

		addProtrudingShapeParts(otherClip, addition);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return null;
		
		removeEnclosedBorder(otherClip, addition);
		removeExitsInsideClip(otherClip, addition);
		return addition;
	}
	
	//TODO overthink chunk usage
	private void addProtrudingShapeParts(Clip otherClip, ClipAction addition) {

		//check for new border blocks
		for(Vec2 borderPoint : otherClip.getBorder()) {
			
			if(!getClip().contains(borderPoint))
				addition.addBorder(borderPoint);
		}
		
		//add new fill blocks
		for(Vec2 fill : otherClip.getFill()) {
			
			if(!getClip().contains(fill))
				addition.addFill(fill, otherClip.getHeight(fill));
		}
	}
	
	//TODO overthink chunk usage
	private void removeEnclosedBorder(Clip otherClip, ClipAction addition) {
		
		for(Vec2 ownBorder : getClip().getBorder()) {
			
			if(otherClip.contains(ownBorder) &&
			  !otherClip.borderContains(ownBorder) ||
			  !sealsMaze(ownBorder, addition, Directions.values()))
				addition.removeBorder(ownBorder);
		}
	}
	
	private void removeExitsInsideClip(Clip otherClip, ClipAction changes) {
	
		for(Vec2 exit : exits) {
			
			if(otherClip.contains(exit))
				changes.removeExit(exit);
		}
	}
	
	public ClipAction getDeletion(Clip clip) {
		
		if(!getWorld().equals(clip.getWorld()))
			return null;
		
		ClipAction deletion = new ClipAction(getClip());
		
		removeIntrudingShapeParts(clip, deletion);
		
		if(deletion.getRemovedFill().isEmpty())
			return null;
		
		removeExcludedBorder(clip, deletion);
		removeExitsInsideClip(clip, deletion);
		return deletion;
	}
	
	private void removeIntrudingShapeParts(Clip otherClip, ClipAction deletion) {
		//remove all fill from the shape
		for(Vec2 otheFill : otherClip.getFill()) {
			if(getClip().contains(otheFill) && !otherClip.borderContains(otheFill))
				deletion.removeFill(otheFill, otherClip.getHeight(otheFill));
		}

		for(Vec2 otherBorder : otherClip.getBorder()) {
			
			if(getClip().contains(otherBorder) && !getClip().borderContains(otherBorder))
				deletion.addBorder(otherBorder);
		}
	}
	
	private void removeExcludedBorder(Clip clip, ClipAction deletion) {
		
		for(Vec2 ownBorder : getClip().getBorder()) {
			
			if(!clip.contains(ownBorder) ||
				clip.borderContains(ownBorder) &&
				sealsMaze(ownBorder, deletion, Directions.values())) {
				continue;
			}
			
			deletion.removeBorder(ownBorder);
			deletion.removeFill(ownBorder, clip.getHeight(ownBorder));
		}
	}

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
			
			Vec2 neighbor = loc.clone().add(dir.toVec2());
			int height = Utils.nearestSurfaceY(neighbor, getClip().getHeight(loc), getWorld());
			
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
			
			Vec2 neighbor = loc.clone().add(dir.toVec2());

			if(getClip().borderContains(neighbor) && !sealsMaze(neighbor, expansion, Directions.values()))
				expansion.removeBorder(neighbor);
		}
	}
	
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
			Vec2 neighbor = loc.clone().add(dir.toVec2());
			
			if(getClip().contains(neighbor) && !getClip().borderContains(neighbor))
				erasure.addBorder(neighbor);
			
			if(exitsContain(neighbor) && !sealsMaze(neighbor, erasure, Directions.cardinalValues()))
				erasure.removeExit(neighbor);
		}
	}
	
	private void removeProtrusiveBorder(Vec2 loc, ClipAction erasure) {
		//detect outstanding neighbor borders of the block
		for(Directions dir : Directions.values()) {

			Vec2 neighbor = loc.clone().add(dir.toVec2());
			
			//remove the neighbor if it still stands out
			if(getClip().borderContains(neighbor) && !sealsMaze(neighbor, erasure, Directions.values())) {
				
				int height = getClip().getHeight(neighbor);
				erasure.removeBorder(neighbor);
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
			
			Vec2 neighbor = loc.clone().add(dir.toVec2());
			
			if(!changes.clipWillContain(neighbor))
				touchesExternal = true;

			else if(!changes.clipBorderWillContain(getClip(), neighbor))
				touchesFill = true;
			
			if(touchesFill && touchesExternal)
				return true;
		}
		
		return false;
	}

	public void updateHeights() {
		
		if(isConstructed())
			throw notAlterableException;

		if(isConstructed())
			
		for(Vec2 fill : getClip().getFill())
			getClip().addFill(fill, Utils.nearestSurfaceY(fill, getClip().getHeight(fill), getWorld()));
	}
	
	public Block updateHeight(Block block) {
		
		if(isConstructed())
			throw notAlterableException;
		
		Location updatedBlock = Utils.nearestSurface(block.getLocation());
		Vec2 blockVec = new Vec2(block);
		
		getClip().addFill(blockVec, updatedBlock.getBlockY());
			
		return updatedBlock.getBlock();
	}
}