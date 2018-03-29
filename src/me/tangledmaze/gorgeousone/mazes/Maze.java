package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.gorgeousone.shapes.Shape;

/**
 * A class that stores the information about a maze, e.g. where the are of the maze is and where preset walls of the maze should stand
 * @author GorgeousOne
 */
public class Maze {
	
	private Player p;
	private HashMap<Chunk, ArrayList<Location>> filledChunks, borderedChunks;
	//private ArrayList<Location> fill, border;
	private ArrayList<Shape> shapes;
	
	public Maze(Player creator, Shape borderShape) {
		p = creator;
		
		filledChunks = new HashMap<>();
		borderedChunks = new HashMap<>();
		
		shapes = new ArrayList<>();
		shapes.add(borderShape);
		
		add(borderShape);
	}
	
	private void addFill(Location point) {
		Chunk c = point.getChunk();
		
		if(filledChunks.containsKey(c))
			filledChunks.get(c).add(point);
		else
			filledChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}

	private void addBorder(Location point) {
		Chunk c = point.getChunk();
		
		if(borderedChunks.containsKey(c))
			borderedChunks.get(c).add(point);
		else
			borderedChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}
	
	@SuppressWarnings("deprecation")
	public void add(Shape s) {
		ArrayList<Chunk> changedChunks = new ArrayList<>();

		ArrayList<Location> newBorder  = new ArrayList<>(),
							newFill    = new ArrayList<>();
		
		//check for new border blocks
		for(Location point : s.getBorder()) {
			if(!contains(point)) {
				newBorder.add(point);
				if(!changedChunks.contains(point.getChunk()))
					changedChunks.add(point.getChunk());
			}
		}
		
		//return if the shapes is totally covered by the maze
		if(changedChunks.isEmpty())
			return;
		
		//check for new fill blocks
		for(Location point : s.getFill()) {
			if(!contains(point)) {
				newFill.add(point);
				if(!changedChunks.contains(point.getChunk()))
					changedChunks.add(point.getChunk());
			}
		}
		
		//check for existing removable border blocks in the changed chunks
		for(Chunk c : changedChunks) {
			if(!borderedChunks.containsKey(c))
				continue;
			ArrayList<Location> currentChunk = borderedChunks.get(c);
			
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				if(s.contains(point) && !s.borderContains(point)) {
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
					currentChunk.remove(point);
				}
			}
		}
		
		//add all new blocks
		for(Location point : newFill)
			addFill(point);
		for(Location point : newBorder)
			addBorder(point);
		
		//show all new blocks
		update(changedChunks);
	}
	
	public void subtract(Shape s) {
	}
	
	/**
	 * @param point 
	 * @return if the point is inside the area of the maze.
	 */
	public boolean contains(Location point) {
		Chunk c = point.getChunk();
		
		if(!filledChunks.containsKey(c))
			return false;
		
		for(Location point2 : filledChunks.get(c)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public boolean borderContains(Location point) {	//TODO think about for usefulness
		Chunk c = point.getChunk();
		
		if(!borderedChunks.containsKey(c))
			return false;
		
		for(Location point2 : borderedChunks.get(c)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public void show() {
		for(Chunk c : filledChunks.keySet())
			if(borderedChunks.containsKey(c))
				for(Location point : borderedChunks.get(c))
					TangledMain_go.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		for(Chunk c : filledChunks.keySet())
			for(Location point : borderedChunks.get(c))
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	public void update(ArrayList<Chunk> changedChunks) {
		for(Chunk c : changedChunks)
			if(borderedChunks.containsKey(c))
				for(Location point : borderedChunks.get(c))
					TangledMain_go.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
}