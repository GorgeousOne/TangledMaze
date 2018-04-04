package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	private ArrayList<Shape> shapes;
	private boolean isVisible;
	
	public Maze(Shape borderShape, Player editor) {
		p = editor;
		
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		
		shapes = new ArrayList<>();
		shapes.add(borderShape);
		
		add(borderShape);
	}
	
	public ArrayList<Location> getFill() {
		ArrayList<Location> fill = new ArrayList<>();
		
		for(ArrayList<Location> chunk : fillChunks.values())
			fill.addAll(chunk);
		
		return fill;
	}
	
	public ArrayList<Location> getMainBorder() {
		ArrayList<Location> border = new ArrayList<>();
		
		for(ArrayList<Location> chunk : borderChunks.values())
			border.addAll(chunk);
		
		return border;
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
			update(overlappingBorderChunks);
			return;
		}

		//check for new fill blocks
		for(Chunk c : s.getFill().keySet())
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

		//show the new border and re-show overlapping border parts (they may be hidden with the shape)
		update(newShapeChunks);
		update(overlappingBorderChunks);
	}
	
	@SuppressWarnings("deprecation")
	public void subtract(Shape s) {
		ArrayList<Chunk> newVoidChunks = new ArrayList<>();
		ArrayList<Location> newBorder  = new ArrayList<>();
		
		//get new border points where shape is cutting into maze
		for(Chunk c : s.getBorder().keySet()) {
			for(Location point : s.getBorder().get(c))

				if(contains(point) && !borderContains(point)) {
					newBorder.add(point);

					if(!newVoidChunks.contains(c))
						newVoidChunks.add(c);
				}
		}
		
		if(newVoidChunks.isEmpty())
			return;

		//remove all overlapping fill
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				
				if(contains(point)) {
					removeFill(point);
					
					if(!newVoidChunks.contains(c))
						newVoidChunks.add(c);
				}

		//remove all maze border inside the shape
		for(Chunk c : newVoidChunks) {
			if(!borderChunks.containsKey(c))
				continue;

			ArrayList<Location> current = borderChunks.get(c);
			
			for(int i = current.size()-1; i >= 0; i--) {
				Location point = current.get(i);
				
				if(s.contains(point) && !s.borderContains(point)) {
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
					current.remove(point);
				}
			}
		}
		
		for(Location point : newBorder)
			addBorder(point);

		update(newVoidChunks);
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
	
	private void removeFill(Location point) {
		Chunk c = point.getChunk();
		
		for(Location point2 : fillChunks.get(c))
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				fillChunks.get(c).remove(point2);
				break;
			}
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
	
	public boolean borderContains(Location point) {
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
		if(isVisible || p == null)
			return;
		isVisible = true;

		for(Chunk c : fillChunks.keySet())
			if(borderChunks.containsKey(c))
				for(Location point : borderChunks.get(c))
					Utils.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		if(!isVisible || p == null)
			return;
		
		isVisible = false;
		
		for(Chunk c : borderChunks.keySet())
			for(Location point : borderChunks.get(c))
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	public void update(ArrayList<Chunk> changedChunks) {
		if(p == null)
			return;
		
		for(Chunk c : changedChunks)
			if(borderChunks.containsKey(c))
				for(Location point : borderChunks.get(c))
					Utils.sendBlockLater(p, point, Constants.MAZE_BORDER);
	}
}