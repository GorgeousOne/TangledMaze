package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.Utils;
import me.tangledmaze.gorgeousone.shapes.Shape;

public class Maze {
	
	private Player p;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> entrances;
	
	private int size;
	
	private boolean isVisible;
	
	public Maze(Shape borderShape, Player editor) {
		p = editor;
		
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
		
		entrances = new ArrayList<>();
		add(borderShape);
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int getSize() {
		return size;
	}
	
	public ArrayList<Chunk> getChunks() {
		return new ArrayList<Chunk>(fillChunks.keySet());
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	public ArrayList<Location> getEntrances() {
		return entrances;
	}
	
	@SuppressWarnings("deprecation")
	public void add(Shape s) {
		ArrayList<Chunk> newShapeChunks = new ArrayList<>(),
						 overlappingBorderChunks = new ArrayList<>();

		ArrayList<Location> newBorder  = new ArrayList<>(),
							newFill    = new ArrayList<>();
		
		//check for new border blocks
		//save chunks with new or overlapping border to show/refresh them later
		for(Chunk c : s.getBorder().keySet())
			for(Location point : s.getBorder().get(c)) {
				
				if(!contains(point)) {
					newBorder.add(point);
					
					if(!newShapeChunks.contains(c))
						newShapeChunks.add(c);
				}
				
				if(borderContains(point) && !overlappingBorderChunks.contains(c))
					overlappingBorderChunks.add(c);
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
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//thats the case if the point is covered by the new shape...
				if(!s.contains(point))
					continue;
				
				//...or it wont seal the new maze anymore
				for(Vector dir : Utils.directions()) {
					Location point2 = point.clone().add(dir);
					
					if(!contains(point2) && !s.contains(point2)) {
						p.sendMessage("nah " + point.toVector().toString());
						continue borderloop;
					}
				}

				currentChunk.remove(point);
				if(isVisible)
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
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
		for(ArrayList<Location> chunk : s.getBorder().values()) {
			Chunk c = chunk.get(0).getChunk();
			
			for(Location point : chunk) {

				if(contains(point) && !borderContains(point)) {
					newBorder.add(point);

					if(!newVoidChunks.contains(c))
						newVoidChunks.add(c);
				}
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
					current.remove(point);
					
					if(isVisible)
						p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
				}
			}
		}
		
		//add all new border blocks
		for(Location point : newBorder)
			addBorder(point);

		//refresh changed chunks
		update(newVoidChunks);
	}
	
	@SuppressWarnings("deprecation")
	public void brush(Block b) {
		Location point = b.getLocation();
		
		if(!borderContains(point) || !isHighlighted(b))
			return;
		
		ArrayList<Location> neighbors = new ArrayList<>();
		boolean	isSealing = false;

		//check if this block closes the shape somehow
		for(Vector dir : Utils.directions()) {
			Location point2 = point.clone().add(dir);
			
			//if yes these neighbor blocks have to be stored
			if(contains(point2) && !borderContains(point2)) {
				isSealing = true;
				neighbors.add(point2);
			}
		}

		removeFill(point);
		borderChunks.get(b.getChunk()).remove(point);
		
		if(!isSealing)
			return;
		
		//if it seals the shape neighbor blocks have to replace it
		for(Location point2 : neighbors) {
			Location surface = Utils.getNearestSurface(point2);
			addBorder(surface);
			
			if(isVisible)
				p.sendBlockChange(surface, Constants.MAZE_BORDER, (byte) 0);
		}
	}
	
	public void addEntrance(Block b) {
		Location newStart = b.getLocation();
		
		if(!borderContains(newStart) || !isHighlighted(b))
			return;
		
		for(Location start : entrances)
			if(start.distance(newStart) <= 1) {
				Utils.sendBlockLater(p, newStart, Constants.MAZE_BORDER);
				return;
			}
		
		entrances.add(newStart);
		Utils.sendBlockLater(p, newStart, Constants.MAZE_START);
	}
	
	private void addFill(Location point) {
		Chunk c = point.getChunk();
		
		if(fillChunks.containsKey(c))
			fillChunks.get(c).add(point);
		else
			fillChunks.put(c, new ArrayList<>(Arrays.asList(point)));

		size++;
	}

	private void addBorder(Location point) {
		Chunk c = point.getChunk();
		
		if(borderChunks.containsKey(c))
			borderChunks.get(c).add(Utils.getNearestSurface(point));
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(Utils.getNearestSurface(point))));
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
	
	private void removeBorder(Location point) {
		Chunk c = point.getChunk();
		
		for(Location point2 : borderChunks.get(c))

			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				borderChunks.get(c).remove(point2);
				break;
			}
	}

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
	
	@SuppressWarnings("deprecation")
	public void show() {
		if(isVisible || p == null)
			return;
		
		isVisible = true;
		
		for(ArrayList<Location> chunk : borderChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		for(Location start : entrances)
			p.sendBlockChange(start, Constants.MAZE_START, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		if(!isVisible || p == null)
			return;
		
		isVisible = false;
		
		for(ArrayList<Location> chunk : borderChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}
	
	@SuppressWarnings("deprecation")
	public void update(ArrayList<Chunk> changedChunks) {
		if(p == null)
			return;
		
		for(Chunk c : changedChunks)
			if(borderChunks.containsKey(c))
				for(Location point : borderChunks.get(c))
					p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
	}
	
	public void recalc(Location point) {
		if(!borderContains(point))
			return;
		
		hide();
		removeBorder(point);
		addBorder(Utils.getNearestSurface(point));
	}
}