package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.Utils;
import me.tangledmaze.gorgeousone.shapes.Shape;

/**
 * A class that stores the information about a maze, e.g. where the are of the maze is and where preset walls of the maze should stand
 * @author GorgeousOne
 */
public class Maze {
	
	private Player p;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	//private ArrayList<Location> fill, border;
	private ArrayList<Shape> shapes;
	
	public Maze(Player creator, Shape borderShape) {
		p = creator;
		
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		
		shapes = new ArrayList<>();
		shapes.add(borderShape);
		
		add(borderShape);
	}
	
	private void addFill(Location point) {
		Chunk c = point.getChunk();
		
		if(fillChunks.containsKey(c))
			fillChunks.get(c).add(point);
		else
			fillChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}

	private void addBorder(Location point) {
		Chunk c = point.getChunk();
		
		if(borderChunks.containsKey(c))
			borderChunks.get(c).add(point);
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}
	
	@SuppressWarnings("deprecation")
	public void add(Shape s) {
		ArrayList<Chunk> newShapeChunks = new ArrayList<>(),
						 overlappingBorderChunks = new ArrayList<>();

		ArrayList<Location> newBorder  = new ArrayList<>(),
							newFill    = new ArrayList<>();
		
		//check for new border blocks
		//save chunks with new or overlapping border to show/refresh them later
		for(Chunk c : s.getBorder().keySet()) {
			for(Location point : s.getBorder().get(c)) {

				if(!contains(point)) {
					newBorder.add(point);
					
					if(!newShapeChunks.contains(c))
						newShapeChunks.add(c);
				}
				if(borderContains(point) && !overlappingBorderChunks.contains(c))
					overlappingBorderChunks.add(c);
			}
		}

		//return if the shapes is totally covered by the maze
		if(newShapeChunks.isEmpty()) {
			Bukkit.broadcastMessage("meh nothing new");
			update(overlappingBorderChunks);
			return;
		}

		//check for new fill blocks
		for(Chunk c : s.getBorder().keySet())
			for(Location point : s.getFill().get(c)) {
				if(!contains(point)) {
					newFill.add(point);
					
					if(!newShapeChunks.contains(c))
						newShapeChunks.add(c);
				}
			}
	
		//check for existing removable border blocks in the chunks of new shape
		for(Chunk c : newShapeChunks) {
			if(!borderChunks.containsKey(c))
				continue;
			ArrayList<Location> currentChunk = borderChunks.get(c);
			
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
		update(newShapeChunks);
		update(overlappingBorderChunks);
	}
	
	public void subtract(Shape s) {
	}
	
	/**
	 * @param point 
	 * @return if the point is inside the area of the maze.
	 */
	public boolean contains(Location point) {
		Chunk c = point.getChunk();
		
		if(!fillChunks.containsKey(c))
			return false;
		
		for(Location point2 : fillChunks.get(c)) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public boolean borderContains(Location point) {	//TODO think about for usefulness
		Chunk c = point.getChunk();
		
		if(!borderChunks.containsKey(c))
			return false;
		
		for(Location point2 : borderChunks.get(c))
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		return false;
	}
	
	public boolean isHighlighted(Block b) {
		Chunk c = b.getChunk();
		
		if(!borderChunks.containsKey(c))
			return false;
		
		for(Location point : borderChunks.get(c))
			if(point.getBlock().equals(b))
				return true;
		return false;
	}
	
	public void show() {
		for(Chunk c : fillChunks.keySet())
			if(borderChunks.containsKey(c))
				for(Location point : borderChunks.get(c))
					Utils.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		for(Chunk c : borderChunks.keySet())
			for(Location point : borderChunks.get(c))
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	public void update(ArrayList<Chunk> changedChunks) {
		for(Chunk c : changedChunks)
			if(borderChunks.containsKey(c))
				for(Location point : borderChunks.get(c))
					Utils.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
}