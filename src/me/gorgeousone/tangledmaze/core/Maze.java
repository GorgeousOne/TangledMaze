package me.gorgeousone.tangledmaze.core;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.clip.ActionHistory;
import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Utils;

/*
 * 
 */
public class Maze {
	
	private UUID builder;
	
	private ActionHistory history;
	private Clip clip;
	private ArrayList<MazePoint> exits;
	private ArrayList<MaterialData> wallComposition;
	
	private Vector dimensions;
	
	private boolean isStarted;
	
	public Maze(World world) {
		
		clip = new Clip(world);
		history = new ActionHistory();
		exits = new ArrayList<>();
		dimensions = new Vector(1, 2, 1);
	}

	public Maze(Player builder) {
		
		this.builder = builder.getUniqueId();

		clip = new Clip(builder.getWorld());
		history = new ActionHistory();
		exits = new ArrayList<>();
		dimensions = new Vector(1, 2, 1);
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
	
	public Clip getClip() {
		return clip;
	}
	
	public ArrayList<MazePoint> getExits() {
		return exits;
	}
	
	public MazePoint getMainExit() {
		return exits.isEmpty() ? null : exits.get(exits.size()-1);
	}
	
	public ActionHistory getActionHistory() {
		return history;
	}
	
	public int getPathWidth() {
		return dimensions.getBlockX();
	}
	
	public int getWallHeight() {
		return dimensions.getBlockY();
	}
	
	public int getWallWidth() {
		return dimensions.getBlockZ();
	}
	
	public ArrayList<MaterialData> getWallComposition() {
		return wallComposition;
	}
	
	public void setPathWidth(int pathWidth) {
		dimensions.setX(Math.max(1, pathWidth));
	}
	
	public void setWallHeight(int wallHeight) {
		dimensions.setY(Math.max(1, wallHeight));
	}
	
	public void setWallWidth(int wallWidth) {
		dimensions.setZ(Math.max(1, wallWidth));
	}
	
	public void setWallComposition(ArrayList<MaterialData> composition) {
		wallComposition = composition;
	}
	
	public void setClip(Clip clip) {
		
		if(getClip().size() != 0)
			Renderer.hideMaze(this);
		
		this.clip = clip;
		isStarted = true;
		Renderer.showMaze(this);
	}
	
	public void reset() {
		
		Renderer.hideMaze(this);

		clip = new Clip(getWorld());
		exits.clear();
		history.clear();
		isStarted = false; 
	}
	
	public boolean exitsContain(Location loc) {
		return exits.contains(loc);
	}
	
	public boolean canBeExit(Block block) {
		
		MazePoint point = new MazePoint(block.getLocation());
		
		if(!isHighlighted(point.getBlock())) {
			return false;
		}
		
		return sealsMaze(point, new ClipAction(), Directions.cardinalValues());
	}
	
	public boolean isHighlighted(Block block) {
		
		MazePoint point = new MazePoint(block.getLocation());
		
		if(!getClip().borderContains(point)) {
			return false;
		}
		
		for(MazePoint borderPoint : getClip().getBorder()) {
			if(borderPoint.equals(point) && borderPoint.getY() == point.getY()) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isExit(Block block) {
		
		MazePoint point = new MazePoint(block.getLocation());
		
		for(MazePoint exit : exits) {
			if(point.equals(exit) && point.getY() == exit.getY())
				return true;
		}
		
		return false;
	}
	
	public void toggleExit(Block block) {
		
		if(!isHighlighted(block)) {
			return;
		}

		MazePoint newExit = new MazePoint(block.getLocation());
		
		if(!canBeExit(block)) {
			Renderer.sendBlockDelayed(getPlayer(), block.getLocation(), Constants.MAZE_BORDER);
			return;
		}
		
		if(isExit(block)) {
			
			exits.remove(newExit);
			Renderer.sendBlockDelayed(getPlayer(), newExit, Constants.MAZE_BORDER);

			if(!exits.isEmpty()) {
				Renderer.sendBlockDelayed(getPlayer(), exits.get(exits.size()-1), Constants.MAZE_MAIN_EXIT);
			}
			
		}else {

			if(!exits.isEmpty()) {
				Renderer.sendBlockDelayed(getPlayer(), exits.get(exits.size()-1), Constants.MAZE_EXIT);
			}
			
			exits.add(newExit);
			Renderer.sendBlockDelayed(getPlayer(), newExit, Constants.MAZE_MAIN_EXIT);
		}
	}
		
	public void updateHeight(Location point) {
		
		MazePoint updated = Utils.nearestSurface(point);
		
		if(getClip().removeFill(updated)) {
			getClip().addFill(updated);
		
		}else
			return;
		
		if(getClip().removeBorder(updated)) {
			getClip().addBorder(updated);
		}
	}
	
	//TODO normal - overthink MazeActions's storing method
	public void processAction(ClipAction action, boolean saveToHistory) {
		
		getClip().removeFill(action.getRemovedFill());
		getClip().removeBorder(action.getRemovedBorder());
		
		for(MazePoint point : action.getAddedFill())
			getClip().addFill(point);
		
		for(MazePoint point : action.getAddedBorder())
			getClip().addBorder(point);
		
		if(saveToHistory)
			history.pushAction(action);

		Renderer.showMazeAction(this, action);
	}
	
	public ClipAction getAddition(Clip clip) {
		
		ClipAction addition = new ClipAction();
		
		if(!getWorld().equals(clip.getWorld()))
			return addition;
		
		addProtrudingShapeParts(clip, addition);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return addition;
		
		removeEnclosedBorder(clip, addition);
		removeExitsInsideClip(clip, addition);
		return addition;
	}
	
	private void addProtrudingShapeParts(Clip clip, ClipAction addition) {
		//check for new border blocks
		for(Chunk chunk : clip.getBorderChunks()) {
			
			if(!getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint borderPoint : clip.getBorder(chunk)) {
				if(!getClip().contains(borderPoint)) {
					addition.addBorder(borderPoint);
				}
			}
		}
		
		//add new fill blocks
		for(Chunk chunk : clip.getChunks()) {
			
			if(!getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint fillPoint : clip.getFill(chunk)) {
				if(!getClip().contains(fillPoint)) {
					addition.addFill(fillPoint);
				}
			}
		}
	}
	
	private void removeEnclosedBorder(Clip clip, ClipAction addition) {
		
		for(Chunk chunk : clip.getChunks()) {
			
			if(!getClip().getBorderChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint ownBorder : getClip().getBorder(chunk)) {
				
				//continue if the point isn't even in the shape
				if(!clip.contains(ownBorder))
					continue;
				
				//if the point is inside the shapes border look up if is connected to blocks outside of the maze
				if(clip.borderContains(ownBorder) && sealsMaze(ownBorder, addition, Directions.values()))
					continue;
				
				//otherwise remove the block
				addition.removeBorder(ownBorder);
			}
		}
	}
	
	private void removeExitsInsideClip(Clip clip, ClipAction changes) {
	
		for(MazePoint exit : exits) {
			if(clip.contains(exit)) {
				changes.removeExit(exit);
			}
		}
	}
	
	public ClipAction getDeletion(Clip clip) {
		
		ClipAction deletion = new ClipAction();
		
		if(!getWorld().equals(clip.getWorld()))
			return deletion;
		
		removeIntrudingShapeParts(clip, deletion);
		
		if(deletion.getAddedBorder().isEmpty() && deletion.getRemovedFill().isEmpty())
			return deletion;
		
		removeExcludedBorder(clip, deletion);
		removeExitsInsideClip(clip, deletion);
		return deletion;
	}
	
	private void removeIntrudingShapeParts(Clip clip, ClipAction deletion) {
		//remove all fill from the shape
		for(Chunk chunk : clip.getChunks()) {
			
			if(!getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint point : clip.getFill(chunk))
				if(getClip().contains(point) && !clip.borderContains(point))
					deletion.removeFill(point);
		}
		//get new border where shape is cutting into maze
		for(Chunk chunk : clip.getBorderChunks()) {
			
			if(!getClip().getChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint point : clip.getBorder(chunk))
				if(getClip().contains(point) && !getClip().borderContains(point))
					deletion.addBorder(point);
		}
	}
	
	private void removeExcludedBorder(Clip clip, ClipAction deletion) {
		
		for(Chunk chunk : clip.getBorderChunks()) {
			
			if(!getClip().getBorderChunks().contains(chunk)) {
				continue;
			}
			
			for(MazePoint ownBorder : getClip().getBorder(chunk)) {
				
				//continue if the point isn't even in the shape
				if(!clip.contains(ownBorder))
					continue;
				
				//if the point is inside the shapes border look up if is connected to blocks outside of the maze
				if(clip.borderContains(ownBorder) && sealsMaze(ownBorder, deletion, Directions.values()))
					continue;
				
				//otherwise remove the block
				deletion.removeBorder(ownBorder);
				deletion.removeFill(ownBorder);
			}
		}
	}

	public ClipAction getExpansion(Block block) {
		
		if(!isHighlighted(block))
			return null;
		
		MazePoint point = new MazePoint(block.getLocation());
		ClipAction expansion = new ClipAction();
		
		expandBorder(point, expansion);
		removeIntrusiveBorder(point, expansion);
		
		return expansion;
	}
	
	private void expandBorder(MazePoint point, ClipAction expansion) {
		
		expansion.removeBorder(point);
		
		for(Directions dir : Directions.values()) {
			MazePoint neighbor = Utils.nearestSurface(point.clone().add(dir.toVec3()));
			
			if(!getClip().contains(neighbor)) {
				expansion.addFill(neighbor);
				expansion.addBorder(neighbor);
				
			}else if(exitsContain(neighbor) && !sealsMaze(neighbor, expansion, Directions.cardinalValues()))
				expansion.removeExit(neighbor);
		}
	}
	
	private void removeIntrusiveBorder(MazePoint point, ClipAction expansion) {
		//look for neighbors, that are now intruding the border unnecessarily
		for(Directions dir : Directions.values()) {
			MazePoint neighbor = Utils.nearestSurface(point.clone().add(dir.toVec3()));
			
			if(!getClip().borderContains(neighbor) && !expansion.getAddedBorder().contains(neighbor))
				continue;
			
			if(!sealsMaze(neighbor, expansion, Directions.values()))
				expansion.removeBorder(neighbor);
		}
	}
	
	public ClipAction getErasure(Block block) {
		
		if(!isHighlighted(block))
			return null;
		
		MazePoint point = new MazePoint(block.getLocation());
		ClipAction action = new ClipAction();
		
		action.removeBorder(point);
		
		reduceBorder(point, action);
		removeProtrusiveBorder(point, action);
		
		return action;
	}
	
	
	private void reduceBorder(MazePoint point, ClipAction action) {
		
		if(exitsContain(point))
			action.removeExit(point);
		
		action.removeBorder(point);
		action.removeFill(point);
		
		if(!sealsMaze(point, action, Directions.values()))
			return;
		
		for(Directions dir : Directions.values()) {
			MazePoint neighbor = point.clone().add(dir.toVec3());
			
			if(getClip().contains(neighbor) && !getClip().borderContains(neighbor))
				action.addBorder(neighbor);
			
			if(exitsContain(neighbor) && !sealsMaze(neighbor, action, Directions.cardinalValues()))
				action.removeExit(neighbor);
		}
	}
	
	private void removeProtrusiveBorder(MazePoint point, ClipAction changes) {
		//detect outstanding neighbor borders of the block (in cardinal directions)
		for(Directions dir : Directions.values()) {
			MazePoint neighbor = Utils.nearestSurface(point.clone().add(dir.toVec3()));
			
			if(!getClip().borderContains(neighbor))
				continue;
			
			//remove the neighbor if it still stands out
			if(!sealsMaze(neighbor, changes, Directions.values())) {
				changes.removeBorder(neighbor);
				changes.removeFill(neighbor);
			}
		}
	}
	
	public boolean sealsMaze(MazePoint point, Directions[] directions) {
		return sealsMaze(point, new ClipAction(), directions);
	}
	
	public boolean sealsMaze(MazePoint point, ClipAction changes, Directions[] directions) {
		
		boolean
			touchesFill = false,
			touchesExternal = false;
		
		for(Directions dir : directions) {
			MazePoint neighbor = point.clone().add(dir.toVec3());
			
			if(!changes.clipWillContain(clip, neighbor)) {
				touchesExternal = true;

			}else if(!changes.clipBorderWillContain(clip, neighbor)) {
				touchesFill = true;
			}
			
			if(touchesFill && touchesExternal) {
				return true;
			}
		}
		return false;
	}
}