package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.tools.Clip;
import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.MazePoint;
import me.gorgeousone.tangledmaze.utils.Utils;

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
	
	//TODO low - check if exit is highlighted in a way (only for hooks)
	public boolean canBeExit(Location point) {
		
		MazePoint newExit = new MazePoint(point);
		
		if(!isHighlighted(newExit.getBlock())) {
			return false;
		}
		
		return sealsMaze(point, new MazeAction(), Directions.cardinalValues());
	}
	
	public boolean isHighlighted(Block b) {
		
		MazePoint point = new MazePoint(b.getLocation());
		
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
	
	public void addExit(Location point) {
		
		if(!canBeExit(point)) {
			Utils.sendBlockDelayed(getPlayer(), point, Constants.MAZE_BORDER);
			return;
		}
		
		MazePoint exit = new MazePoint(point);
		
		if(exits.contains(exit)) {
			
			exits.remove(exit);
			Utils.sendBlockDelayed(getPlayer(), exit, Constants.MAZE_BORDER);
			
			if(!exits.isEmpty()) {
				Utils.sendBlockDelayed(getPlayer(), exits.get(exits.size()-1), Constants.MAZE_MAIN_EXIT);
			}
			
			return;
		}
		
		if(!exits.isEmpty()) {
			Utils.sendBlockDelayed(getPlayer(), exits.get(exits.size()-1), Constants.MAZE_EXIT);
		}
		
		exits.add(exit);
		Utils.sendBlockDelayed(getPlayer(), exit, Constants.MAZE_MAIN_EXIT);
	}
	
	@SuppressWarnings("deprecation")
	public void removeExit(Location point) {
		
		if(!point.getWorld().equals(getWorld()))
			return;
		
		for(int i = exits.size()-1; i >= 0; i--) {
			
			Location exit = exits.get(i);
			
			if(exit.getBlockX() == point.getBlockX() &&
			   exit.getBlockZ() == point.getBlockZ()) {
				
				if(exits.size() > 1 && i == exits.size()-1)
					getPlayer().sendBlockChange(exits.get(i-1), Constants.MAZE_MAIN_EXIT, (byte) 0);
				
				exits.remove(exit);
				Utils.sendBlockDelayed(getPlayer(), exit, Constants.MAZE_BORDER);
				return;
			}
		}
		
	}
	
	public void updateHeight(Location point) {
		
		MazePoint point2 = Utils.nearestSurface(point);
		
		if(getClip().removeFill(point2)) {
			getClip().addFill(point2);
		
		}else
			return;
		
		if(getClip().removeBorder(point2)) {
			getClip().addBorder(point2);
		}
	}
	
	//TODO normal - overthink MazeActions's storing method
	public void processAction(MazeAction action, boolean saveToHistory) {
		
		for(MazePoint point : action.getRemovedFill())
			getClip().removeFill(point);
		
		for(MazePoint point : action.getRemovedBorder())
			getClip().removeBorder(point);
		
		for(MazePoint point : action.getAddedFill())
			getClip().addFill(point);
		
		for(MazePoint point : action.getAddedBorder())
			getClip().addBorder(point);
		
		if(saveToHistory)
			history.pushAction(action);

		Renderer.showMazeAction(this, action);
	}
	
	
	public MazeAction getAddition(Clip clip) {
		
		MazeAction addition = new MazeAction();
		
		if(!getWorld().equals(clip.getWorld()))
			return addition;
		
		addProtrudingClipParts(addition, clip);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return addition;
		
		removeIntersectingBorder(clip, addition);
		
		//remove all exists inside the shape
		for(MazePoint exit : exits)
			if(clip.contains(new MazePoint(exit))) 
				addition.removeExit(exit);
		
		return addition;
	}
	
	private void addProtrudingClipParts(MazeAction addition, Clip other) {
		//check for new border blocks
			for(MazePoint borderPoint : other.getBorder())
				if(!getClip().contains(borderPoint))
					addition.addBorder(borderPoint);
		
		//add new fill blocks
			for(MazePoint fillPoint : other.getFill())
				if(!getClip().contains(fillPoint))
					addition.addFill(fillPoint);
	}
	
	private void removeIntersectingBorder(Clip other, MazeAction action) {
		
		for(MazePoint borderPoint : getClip().getBorder()) {

			//continue if the point isn't even in the shape
			if(!other.contains(borderPoint))
				continue;
			
			//if the point is inside the shapes border look up if is connected to blocks outside of the maze
			if(other.borderContains(borderPoint) && touchesExternalArea(borderPoint, other))
				continue;
			
			//otherwise remove the block
			action.removeBorder(borderPoint);
		}
	}
	
	private boolean touchesExternalArea(MazePoint point, Clip other) {
		
		for(Directions dir : Directions.values()) {
			
			MazePoint point2 = new MazePoint(point);
			point2.add(dir.facing3d());
			
			if(!getClip().contains(point2) && !other.contains(point2))
				return true;
		}
		return false;
	}
	
	public MazeAction getDeletion(Clip other) {
		
		MazeAction deletion = new MazeAction();
		
		if(!getWorld().equals(other.getWorld()))
			return deletion;
		
		removeIntrudingClipParts(deletion, other);
		
		if(deletion.getAddedBorder().isEmpty() && deletion.getRemovedFill().isEmpty())
			return deletion;
	
		removeIntersectingBorder(other, deletion);
		
		//remove all exits inside the shape 
		for(MazePoint exit : exits)
			if(other.contains(exit))
				deletion.removeExit(exit);
		
		return deletion;
	}
	
	private void removeIntrudingClipParts(MazeAction deletion, Clip other) {
		//get new border points where shape is cutting into maze
		for(MazePoint borderPoint : other.getBorder())
			if(getClip().contains(borderPoint) && !getClip().borderContains(borderPoint))
				deletion.addBorder(borderPoint);
		
		//remove all remaining maze fill inside the shape
		for(MazePoint fillPoint : other.getFill())
				if(getClip().contains(fillPoint) && !other.borderContains(fillPoint))
					deletion.removeFill(fillPoint);
	}
	
	public MazeAction getExpansion(Block b) {
		
		if(!isHighlighted(b))
			return null;

		MazeAction action = new MazeAction();
		MazePoint point = new MazePoint(b.getLocation());

		expandBorder(point, action);
		removeIntrusiveBorder(point, action);
		
		return action;
	}
	
	public MazeAction getReduction(Block b) {
		
		//can't remove what isn't part of the border
		if(!isHighlighted(b))
			return null;
		
		MazeAction action = new MazeAction();
		MazePoint point = new MazePoint(b.getLocation());

		reduceBorder(point, action);
		removeProtrusiveBorder(point, action);
		
		return action;
	}
	
	private void expandBorder(MazePoint point, MazeAction changes) {
		
		changes.removeBorder(point);
		
		for(Directions dir : Directions.values()) {
			
			MazePoint point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!getClip().contains(point2)) {
				changes.addFill(point2);
				changes.addBorder(point2);
				
			}else if(exitsContain(point2) && !sealsMaze(point2, changes, Directions.cardinalValues()))
				changes.removeExit(point2);
		}
	}
	
	private void removeIntrusiveBorder(MazePoint point, MazeAction changes) {
		//go through all neighbor-border around given border-point
		for(Directions dir : Directions.values()) {
			
			MazePoint point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!getClip().borderContains(point2) && !changes.getAddedBorder().contains(point2)) {
				continue;
			}
			
			//remove if they will stick inside of mazes's shape unnecessarily when border-point is removed 
			if(!sealsMaze(point2, changes, Directions.values())) {
				changes.removeBorder(point2);
			}
		}
	}
	
	private void reduceBorder(MazePoint point, MazeAction action) {
		
		if(exitsContain(point))
			action.removeExit(point);
		
		action.removeBorder(point);
		action.removeFill(point);
		
		if(!sealsMaze(point, action, Directions.values())) {
			return;
		}
		
		for(Directions dir : Directions.values()) {
			
			MazePoint point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(getClip().contains(point2) && !getClip().borderContains(point2)) {
				action.addBorder(point2);
			}
			
			if(exitsContain(point2) && !sealsMaze(point2, action, Directions.cardinalValues())) {
				action.removeExit(point2);
			}
		}
	}
	
	private void removeProtrusiveBorder(MazePoint point, MazeAction changes) {
		//go through all neighbor-border around given border-point 
		for(Directions dir : Directions.values()) {
			MazePoint point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!getClip().borderContains(point2)) {
				continue;
			}
			
			//remove if they will stick out of maze's shape unnecessarily when border-point is removed 
			if(!sealsMaze(point2, changes, Directions.values())) {
				changes.removeBorder(point2);
				changes.removeFill(point2);
			}
		}
	}
	
	public boolean sealsMaze(Location loc, MazeAction changes, Directions[] directions) {
		
		boolean
			touchesFill = false,
			touchesExternal = false;
		
		for(Directions dir : directions) {
			
			MazePoint point2 = new MazePoint(loc.clone().add(dir.facing3d()));
			
			if(!getClip().contains(point2) &&
			   !changes.getAddedFill().contains(point2) ||
			    changes.getRemovedFill().contains(point2)) {
				
				touchesExternal = true;

			}else if(!getClip().borderContains(point2) &&
					 !changes.getAddedBorder().contains(point2) ||
					  changes.getRemovedBorder().contains(point2)) {
				
				touchesFill = true;
			}
		}
		
		return touchesFill && touchesExternal;
	}
}