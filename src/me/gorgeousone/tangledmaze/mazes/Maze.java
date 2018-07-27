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
	private int size, borderSize;
	
	private ArrayList<Location> exits;
		
	public Maze(Player builder) {

		this.player = builder.getUniqueId();
		history = new ActionHistory();

		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		exits = new ArrayList<>();
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
		
		Renderer.showMaze(this);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		// TODO Auto-generated method stub
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
	

	public void setPathWidth(int pathWidth) {
		// TODO Auto-generated method stub
		
	}

	public void setWallHeight(int wallHeight) {
		// TODO Auto-generated method stub
		
	}

	public void setWallWidth(int wallWidth) {
		// TODO Auto-generated method stub
		
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
	
	private void addFill(Location point) {
		Chunk c = point.getChunk();
		
		if(fillChunks.containsKey(c))
			fillChunks.get(c).add(point);
		else
			fillChunks.put(c, new ArrayList<>(Arrays.asList(point)));

		size++;
	}

	private void removeFill(Location point) {
		Chunk c = point.getChunk();
		
		for(Location point2 : fillChunks.get(c))
			
			if(point2.getBlockX() == point.getBlockX() &&
			point2.getBlockZ() == point.getBlockZ()) {
				
				fillChunks.get(c).remove(point2);
				size--;
				break;
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
		Chunk c = point.getChunk();
		
		for(Location point2 : borderChunks.get(c))

			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				borderChunks.get(c).remove(point2);
				borderSize--;
				break;
			}
	}
	
	public void addExit(Location loc) {
		
	}
	
	public void removeExit(Location loc) {
		
	}
	
	public void processAction(MazeAction action, boolean saveToHistory) {
		if(saveToHistory)
			history.pushAction(action);

		for(Location point : action.getRemovedFill())
			removeFill(point);
	
		for(Location point : action.getRemovedBorder())
			removeBorder(point);
	
		for(Location point : action.getAddedFill())
			addFill(point);

		for(Location point : action.getAddedBorder())
			addBorder(point);		
	}
	
	
	public MazeAction getAddition(ShapeSelection shape) {
		
		MazeAction addition = new MazeAction();
		
		if(!world.equals(shape.getWorld()))
			return null;
		
		addFillAndBorderFromShape(addition, shape);
		
		//return if the shapes is totally covered by the maze
		if(addition.getAddedFill().isEmpty())
			return null;
		
		removeBorderInsideShape(shape, addition);
		
		//remove all exists inside the shape (thats the easy way)
		for(Location exit : exits)
			if(shape.contains(exit)) 
				addition.removeExit(exit);
		
		return addition;
	}
	
	private void addFillAndBorderFromShape(MazeAction addition, ShapeSelection shape) {
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
	
	private void removeBorderInsideShape(ShapeSelection shape, MazeAction addition) {
		
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
				addition.removeBorder(borderPoint);
			}
		}
	}
	
	private boolean touchesOutside(Location point, ShapeSelection shape) {
		
		for(Vector dir : Utils.DIRECTIONS) {
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
		
		//get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : shape.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !borderContains(point))
					deletion.addBorder(point);
		
		//remove all remaining maze fill inside the shape
		for(Chunk c : shape.getFill().keySet())
			for(Location point : shape.getFill().get(c))
				if(contains(point) && !shape.borderContains(point))
					deletion.addFill(point);
				
		if(deletion.getAddedBorder().isEmpty() && deletion.getAddedFill().isEmpty())
			return deletion;
	
		//remove all maze border inside the shape
		for(Chunk c : shape.getFill().keySet()) {
			if(!borderChunks.containsKey(c))
				continue;
	
			ArrayList<Location> currentChunk = borderChunks.get(c);
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//continue if the border point isn't even in the cutting shape
				if(!shape.contains(point))
					continue;
				
				//if the point is inside the shapes border, look up if it touches actual fill blocks (otherwise corners get removed)
				if(shape.borderContains(point)) {
					for(Vector dir : Utils.DIRECTIONS) {
						Location point2 = point.clone().add(dir);
						
						if(contains(point2) && !shape.contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				deletion.removeBorder(point);
				deletion.removeFill(point);
			}
		}
		
		//remove all exits inside the shape 
		for(Location exit : exits)
			if(shape.contains(exit))
				deletion.removeExit(exit);
			
		return deletion;
	}
	
	public MazeAction reduce(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public MazeAction enlarge(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canBeExit(Location loc) {
		// TODO Auto-generated method stub
		return false;
	}
}