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
import me.gorgeousone.tangledmaze.utils.Utils;

public class Maze {
	
	private UUID player;
	private World world;
	
	private ActionHistory history;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> exits;
	
	private int size, borderSize;
	private Vector dimensions;
	
	private boolean isStarted;
		
	public Maze(Player builder) {

		this.player = builder.getUniqueId();
		history = new ActionHistory();

		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		exits = new ArrayList<>();
		
		dimensions = new Vector(1, 3, 1);
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
		
		size = 0;
		borderSize = 0;
		
		history.clear();
		isStarted = false; 
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
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
	
	public boolean isBorder(Block b) {
		if(!b.getWorld().equals(world))
			return false;
		
		Chunk chunk = b.getChunk();
		Location point = b.getLocation();
		
		if(!borderChunks.containsKey(chunk));
		
		for(Location point2 : borderChunks.get(chunk))
			if(point.equals(point2))
				return true;
		
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
		
		for(Location point2 : borderChunks.get(chunk))

			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				borderChunks.get(chunk).remove(point2);
				borderSize--;
				break;
			}
	}
	
	public void addExit(Location loc) {
		
	}
	
	public void removeExit(Location loc) {
		
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
		
		addShapesStickOuts(addition, shape);
		
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
	
	private void addShapesStickOuts(MazeAction addition, ShapeSelection shape) {
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
				if(shape.borderContains(borderPoint) && touchesOutside(borderPoint, shape))
					continue;
				
				//otherwise remove the block
				action.removeBorder(borderPoint);
			}
		}
	}
	
	private boolean touchesOutside(Location point, ShapeSelection shape) {
		
		for(Vector dir : Utils.ALL_DIRECTIONS) {
			Location point2 = point.clone().add(dir);
			
			if(!contains(point2) && !shape.contains(point2))
				return true;
		}
		
		return false;
	}
	
	public MazeAction getDeletion(ShapeSelection shape) {
		
		MazeAction deletion = new MazeAction();
	
		if(!world.equals(shape.getWorld()))
			return deletion;
		
		removeShapesOverlap(deletion, shape);
				
		if(deletion.getAddedBorder().isEmpty() && deletion.getAddedFill().isEmpty())
			return deletion;
	
		removeIntersectingBorder(shape, deletion);
		
		//remove all exits inside the shape 
		for(Location exit : exits)
			if(shape.contains(exit))
				deletion.removeExit(exit);
		
		return deletion;
	}
	
	private void removeShapesOverlap(MazeAction deletion, ShapeSelection shape) {
		////get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : shape.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !borderContains(point))
					deletion.addBorder(point);
		
		//remove all remaining maze fill inside the shape
		for(ArrayList<Location> chunk : shape.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !shape.borderContains(point))
					deletion.removeFill(point);
	}
	
	public MazeAction getEnlargment(Block b) {
		
		Location point = b.getLocation();
		MazeAction action = new MazeAction();
		
		if(!isBorder(b))
			return action;
		
		enlargeBorder(point, action);
		removeStickingInBorder(point, action);
		
		return action;
	}
	
	public MazeAction getReduction(Block b) {

		Location point = b.getLocation();
		MazeAction action = new MazeAction();
		
		//can't remove what isn't part of the border
		if(!isBorder(b))
			return action;
		
		reduceBorder(point, action);
		removeSitckingOutBorder(point, action);
		
		return action;
	}
	
	private void enlargeBorder(Location point, MazeAction changes) {
		changes.removeBorder(point);
		
		if(!sealsMaze(point, changes, Utils.ALL_DIRECTIONS))
			return;
		
		for(Vector dir : Utils.ALL_DIRECTIONS) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(!contains(point2))
				changes.addBorder(point2);
			
			if(exitsContain(point2) && !sealsMaze(point2, changes, Utils.CARDINAL_DIRS))
				changes.removeExit(point2);
		}
	}
	
	private boolean sealsMaze(Location point, MazeAction changes, ArrayList<Vector> directions) {
		
		boolean
			touchesFill = false,
			touchesOutside = false;
		
		for(Vector dir : directions) {
			Location point2 = point.clone().add(dir);
			
			if((contains(point2) || changes.getAddedFill().contains(point2)) && !changes.getRemovedFill().contains(point2)) {
				if(!borderContains(point2) && !changes.getAddedBorder().contains(point2))
					touchesFill = true;
			}else
				touchesOutside = true;
		}
		
		Bukkit.broadcastMessage("fill " + touchesFill + ", out " + touchesOutside);
		return touchesFill && touchesOutside;
	}
	
	private void removeStickingInBorder(Location point, MazeAction action) {
		//look for neighbors, that are now standing out (inside the maze)
		for(Vector dir : Utils.CARDINAL_DIRS) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(!borderContains(point2))
				continue;
			
			//if they are totally without connection 
			if(!sealsMaze(point, action, Utils.ALL_DIRECTIONS))
				action.removeBorder(point2);
		}
	}
	
//	private boolean sticksIn(Location borderPoint, MazeAction action) {
//		//check if the neighbors are connected to other border parts (in cardinal directions)
//		for(Vector dir : Utils.ALL_DIRECTIONS) {
//			Location point = borderPoint.clone().add(dir);
//			
//			if((contains(point) || action.getAddedFill().contains(point)) &&
//			   (!borderContains(point) && !action)
//				return false;
//		}
//		return true;
//	}
	
	private void reduceBorder(Location point, MazeAction action) {
		
		action.removeFill(point);
		action.removeBorder(point);
		
		if(exits.contains(point))
			action.removeExit(point);
		
		if(!sealsMaze(point, action, Utils.ALL_DIRECTIONS))
			return;
		
		for(Vector dir : Utils.ALL_DIRECTIONS) {
			Location point2 = point.clone().add(dir);
			
			if(contains(point2) && !borderContains(point2))
				action.addBorder(point2);
			
			if(exitsContain(point2) && !sealsMaze(point2, action, Utils.CARDINAL_DIRS))
				action.removeExit(point2);
		}
	}
	
	private void removeSitckingOutBorder(Location point, MazeAction changes) {
		//detect outstanding neighbor borders of the block (in cardinal directions)
		for(Vector dir : Utils.CARDINAL_DIRS) {
			Location point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(!borderContains(point2))
				continue;
			
			//remove the neighbor if it still stands out
			if(sticksOut(point2, changes)) {
				changes.removeBorder(point2);
				changes.removeFill(point2);
			}
		}
	}
	
	private boolean sticksOut(Location borderPoint, MazeAction changes) {
		
		for(Vector dir2 : Utils.ALL_DIRECTIONS) {
			Location point3 = borderPoint.clone().add(dir2);
			
			//fill only is also dependent on border that will be added
			if(contains(point3) && !borderContains(point3) && !changes.getAddedBorder().contains(point3))
				return false;
		}
		return true;
	}
	
	public boolean canBeExit(Location point) {
		if(!borderContains(point))
			return false;
		
		return sealsMaze(point, new MazeAction(), Utils.CARDINAL_DIRS);
	}
}