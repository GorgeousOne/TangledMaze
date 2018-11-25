package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;
import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.Utils;

public class Maze {
	
	private UUID player;
	private World world;
	
	private ActionHistory history;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> exits;
	private ArrayList<MaterialData> wallComposition;
	
	private int size, borderSize;
	private Vector dimensions;
	
	private boolean isStarted;
		
	public Maze(Player builder) {
		
		player = builder.getUniqueId();
		world = builder.getWorld();
		
		history = new ActionHistory();
		
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		exits = new ArrayList<>();
		
		dimensions = new Vector(1, 2, 1);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	public ArrayList<Chunk> getChunks() {
		return new ArrayList<>(fillChunks.keySet());
	}
	
	public int size() {
		return size;
	}
	
	public int borderSize() {
		return borderSize;
	}
	
	public ArrayList<Location> getExits() {
		return exits;
	}
	
	public Location getMainExit() {
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
	
	@SuppressWarnings("unchecked")
	public void setShape(ShapeSelection shape) {
		
		if(size != 0)
			Renderer.hideMaze(this);
		
		world = shape.getWorld();
		
		fillChunks = (HashMap<Chunk, ArrayList<Location>>) shape.getFill().clone();
		borderChunks = (HashMap<Chunk, ArrayList<Location>>) shape.getBorder().clone();
		
		size = shape.size();
		borderSize = shape.borderSize();
		isStarted = true;
		
		Renderer.showMaze(this);
	}
	
	public void reset() {
		Renderer.hideMaze(this);
		
		fillChunks.clear();
		borderChunks.clear();
		exits.clear();
		
		size = 0;
		borderSize = 0;
		
		history.clear();
		isStarted = false; 
	}
	
	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Chunk chunk = point.getChunk();
		
		if(!fillChunks.containsKey(chunk))
			return false;
		
		for(Location point2 : fillChunks.get(chunk)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public boolean borderContains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Chunk chunk = point.getChunk();
		
		if(!borderChunks.containsKey(chunk))
			return false;
		
		for(Location point2 : borderChunks.get(chunk)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public boolean exitsContain(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		for(Location point2 : exits) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		
		return false;
	}
	
	public boolean canBeExit(Location point) {
		if(!borderContains(point))
			return false;
		
		return sealsMaze(point, new MazeAction(), Directions.cardinalValues());
	}
	
	public boolean isHighlighted(Block b) {
		if(!b.getWorld().equals(world))
			return false;
		
		Chunk chunk = b.getChunk();
		Location point = b.getLocation();
		
		if(!borderChunks.containsKey(chunk))
			return false;
		
		for(Location point2 : borderChunks.get(chunk))
			if(point.equals(point2))
				return true;
		
		return false;
	}
	
	private void addFill(Location point) {
		Chunk chunk = point.getChunk();
		
		if(fillChunks.containsKey(chunk))
			fillChunks.get(chunk).add(point);
		else
			fillChunks.put(chunk, new ArrayList<>(Arrays.asList(point)));

		size++;
	}
	
	private void removeFill(Location point) {
		Chunk chunk = point.getChunk();
		
		if(!fillChunks.containsKey(chunk))
			return;
		
		for(Location point2 : fillChunks.get(chunk)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				fillChunks.get(chunk).remove(point2);
				size--;
				break;
			}
		}
	}
	
	private void addBorder(Location point) {
		Chunk c = point.getChunk();
		
		if(borderChunks.containsKey(c))
			borderChunks.get(c).add(Utils.nearestSurface(point));
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(Utils.nearestSurface(point))));
		
		borderSize++;
	}
	
	private void removeBorder(Location point) {
		Chunk chunk = point.getChunk();
		
		for(Location point2 : borderChunks.get(chunk)) {

			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				borderChunks.get(chunk).remove(point2);
				borderSize--;
				break;
			}
		}
	}
	
	public void addExit(Location point) {
		
		if(!canBeExit(point)) {
			
			if(isHighlighted(point.getBlock()))
				Utils.sendBlockDelayed(getPlayer(), point, Constants.MAZE_BORDER);
			
			return;
		}
		
		Location exit = Utils.nearestSurface(point);
		
		if(!exits.isEmpty())
			Utils.sendBlockDelayed(getPlayer(), exits.get(exits.size()-1), Constants.MAZE_EXIT);
		
		exits.add(exit);
		Utils.sendBlockDelayed(getPlayer(), exit, Constants.MAZE_MAIN_EXIT);
	}
	
	@SuppressWarnings("deprecation")
	public void removeExit(Location point) {
		
		if(!point.getWorld().equals(world))
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
		if(!point.getWorld().equals(world))
			return;
		
		if(!fillChunks.containsKey(point.getChunk()))
			return;
			
		ArrayList<Location>	fill = fillChunks.get(point.getChunk());
		
		if(fill.contains(point)) {
			
			Location newPoint = Utils.nearestSurface(point);
			fill.set(fill.indexOf(point), newPoint);

			ArrayList<Location>	border = borderChunks.get(point.getChunk());

			if(border.contains(point))
				border.set(border.indexOf(point), newPoint);
		}
	}
	
	public void processAction(MazeAction action, boolean saveToHistory) {
		
		for(Location point : action.getRemovedFill())
			removeFill(point);
	
		for(Location point : action.getRemovedBorder())
			removeBorder(point);
	
		for(Location point : action.getAddedFill())
			addFill(point);

		for(Location point : action.getAddedBorder())
			addBorder(point);
		
		if(saveToHistory)
			history.pushAction(action);

		Renderer.showMazeAction(this, action);
	}
	
	
	public MazeAction getAddition(ShapeSelection shape) {
		
		MazeAction addition = new MazeAction();
		
		if(!world.equals(shape.getWorld()))
			return addition;
		
		addProtrudingShapeParts(addition, shape);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return addition;
		
		removeIntersectingBorder(shape, addition);
		
		//remove all exists inside the shape (thats the easy way)
		for(Location exit : exits)
			if(shape.contains(exit)) 
				addition.removeExit(exit);
		
		return addition;
	}
	
	private void addProtrudingShapeParts(MazeAction addition, ShapeSelection shape) {
		//check for new border blocks
		for(Chunk chunk : shape.getBorder().keySet())
			for(Location borderPoint : shape.getBorder().get(chunk))
				if(!contains(borderPoint))
					addition.addBorder(borderPoint);
		
		//add new fill blocks
		for(Chunk chunk : shape.getFill().keySet())
			for(Location fillPoint : shape.getFill().get(chunk))
				if(!contains(fillPoint))
					addition.addFill(fillPoint);
	}
	
	private void removeIntersectingBorder(ShapeSelection shape, MazeAction action) {
		
		for(Chunk chunk : shape.getFill().keySet()) {
			if(!borderChunks.containsKey(chunk))
				continue;
			
			ArrayList<Location> currentChunk = borderChunks.get(chunk);
			
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location borderPoint = currentChunk.get(i);
				
				//continue if the point isn't even in the shape
				if(!shape.contains(borderPoint))
					continue;
				
				//if the point is inside the shapes border look up if is connected to blocks outside of the maze
				if(shape.borderContains(borderPoint) && touchesExternalArea(borderPoint, shape))
					continue;
				
				//otherwise remove the block
				action.removeBorder(borderPoint);
			}
		}
	}
	
	private boolean touchesExternalArea(Location point, ShapeSelection shape) {
		
		for(Directions dir : Directions.values()) {
			Location point2 = point.clone().add(dir.facing3d());
			
			if(!contains(point2) && !shape.contains(point2))
				return true;
		}
		return false;
	}
	
	public MazeAction getDeletion(ShapeSelection shape) {
		
		MazeAction deletion = new MazeAction();
		
		if(!world.equals(shape.getWorld()))
			return deletion;
		
		removeIntrudingShapeParts(deletion, shape);
		
		if(deletion.getAddedBorder().isEmpty() && deletion.getRemovedFill().isEmpty())
			return deletion;
	
		removeIntersectingBorder(shape, deletion);
		
		//remove all exits inside the shape 
		for(Location exit : exits)
			if(shape.contains(exit))
				deletion.removeExit(exit);
		
		return deletion;
	}
	
	private void removeIntrudingShapeParts(MazeAction deletion, ShapeSelection shape) {
		//get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : shape.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !borderContains(point))
					deletion.addBorder(point);
		
		//remove all remaining maze fill inside the shape
		for(ArrayList<Location> chunk : shape.getFill().values())
			for(Location point : chunk)
				if(contains(point) && !shape.borderContains(point))
					deletion.removeFill(point);
	}
	
	public MazeAction getEnlargment(Block b) {
		
		Location point = b.getLocation();
		MazeAction action = new MazeAction();
		
		if(!isHighlighted(b))
			return action;
		
		enlargeBorder(point, action);
		removeIntrusiveBorder(point, action);
		
		return action;
	}
	
	public MazeAction getReduction(Block b) {
		
		Location point = b.getLocation();
		MazeAction action = new MazeAction();
		
		//can't remove what isn't part of the border
		if(!isHighlighted(b))
			return action;
		
		action.removeBorder(point);
		reduceBorder(point, action);
		removeProtrusiveBorder(point, action);
		
		return action;
	}
	
	private void enlargeBorder(Location point, MazeAction changes) {
		
		changes.removeBorder(point);
		
		for(Directions dir : Directions.values()) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!contains(point2)) {
				changes.addFill(point2);
				changes.addBorder(point2);
			
			}else if(exitsContain(point2) && !sealsMaze(point2, changes, Directions.cardinalValues()))
				changes.removeExit(point2);
		}
	}
	
	private void removeIntrusiveBorder(Location point, MazeAction changes) {
		//look for neighbors, that are now intruding the border unnecessarily
		for(Directions dir : Directions.values()) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!borderContains(point2) && !Utils.listContains(changes.getAddedBorder(), point2))
				continue;
			
			if(!sealsMaze(point2, changes, Directions.values()))
				changes.removeBorder(point2);
		}
	}
	
	private void reduceBorder(Location point, MazeAction action) {
		
		if(exitsContain(point))
			action.removeExit(point);
		
		action.removeBorder(point);
		action.removeFill(point);
		
		if(!sealsMaze(point, action, Directions.values()))
			return;
		
		for(Directions dir : Directions.values()) {
			Location point2 = point.clone().add(dir.facing3d());
			
			if(contains(point2) && !borderContains(point2))
				action.addBorder(point2);
			
			if(exitsContain(point2) && !sealsMaze(point2, action, Directions.cardinalValues()))
				action.removeExit(point2);
		}
	}
	
	private void removeProtrusiveBorder(Location point, MazeAction changes) {
		//detect outstanding neighbor borders of the block (in cardinal directions)
		for(Directions dir : Directions.values()) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir.facing3d()));
			
			if(!borderContains(point2))
				continue;
			
			//remove the neighbor if it still stands out
			if(!sealsMaze(point2, changes, Directions.values())) {
				changes.removeBorder(point2);
				changes.removeFill(point2);
			}
		}
	}
	
	public boolean sealsMaze(Location point, MazeAction changes, Directions[] directions) {
		
		boolean
			touchesFill = false,
			touchesExternal = false;
		
		for(Directions dir : directions) {
			Location point2 = point.clone().add(dir.facing3d());
			
			if(!contains(point2) && !Utils.listContains(changes.getAddedFill(), point2) ||
									 Utils.listContains(changes.getRemovedFill(), point2))
				touchesExternal = true;

			else if(!borderContains(point2) && !Utils.listContains(changes.getAddedBorder(), point2) ||
												Utils.listContains(changes.getRemovedBorder(), point2))
				touchesFill = true;
		}
		
		return touchesFill && touchesExternal;
	}
}