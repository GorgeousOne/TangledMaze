package me.gorgeousone.tangledmaze.mazes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	
	private Player builder;
	private World world;
	
	private ActionHistory history;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private int size, borderSize;
	
	private ArrayList<Location> exits;
		
	public Maze(Player builder) {

		this.builder = builder;
		
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		exits = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	public void setShape(ShapeSelection shape) {
		world = shape.getWorld();
		
		fillChunks = (HashMap<Chunk, ArrayList<Location>>) shape.getFill().clone();
		borderChunks = (HashMap<Chunk, ArrayList<Location>>) shape.getBorder().clone();
		
		size = shape.size();
		borderSize = shape.borderSize();
		
		Renderer.showMaze(this);
	}
	
	public Player getPlayer() {
		return builder;
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
	
	
	public MazeAction getDeletion(ShapeSelection shape) {
		return null;
	}

	public MazeAction getAddition(ShapeSelection shape) {
		ArrayList<Location>
			addedFill     = new ArrayList<>(),
			addedBorder   = new ArrayList<>(),
			removedBorder = new ArrayList<>(),
			removedExits  = new ArrayList<>();
		
		MazeAction addition = new MazeAction(
				addedFill,
				new ArrayList<>(),
				addedBorder,
				removedBorder,
				removedExits);
		
		if(!world.equals(shape.getWorld()))
			return addition;
		
		//check for new border blocks
		for(Chunk chunk : shape.getBorder().keySet())
			for(Location point : shape.getBorder().get(chunk))
				if(!contains(point))
					addedBorder.add(point);

		//add new fill blocks
		for(Chunk chunk : shape.getFill().keySet())
			for(Location point : shape.getFill().get(chunk))
				if(!contains(point))
					addedFill.add(point);

		//return if the shapes is totally covered by the maze
		if(addedBorder.isEmpty() && addedFill.isEmpty())
			return addition;
		
		//remove border blocks inside of the new shape
		for(Chunk chunk : shape.getFill().keySet()) {
			if(!borderChunks.containsKey(chunk))
				continue;
			
			ArrayList<Location> currentChunk = borderChunks.get(chunk);
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//continue if the point isn't even in the shape
				if(!shape.contains(point))
					continue;
				
				//if the point is inside the shapes border look up if is connected to blocks outside of the maze
				if(shape.borderContains(point)) {
					for(Vector dir : Utils.directions()) {
						Location point2 = point.clone().add(dir);
						
						if(!contains(point2) && !shape.contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				removedBorder.add(point);
			}
		}
		
		//remove all exists inside the shape (thats the easy way)
		for(Location exit : exits)
			if(shape.contains(exit)) 
				removedExits.add(exit);
		
		return addition;
	}
	
	public MazeAction reduce(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public MazeAction enlarge(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBorder(Block b) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canBeExit(Location loc) {
		// TODO Auto-generated method stub
		return false;
	}
}