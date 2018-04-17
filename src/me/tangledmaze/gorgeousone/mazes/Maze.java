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

		boolean somethingWasAdded = false;
		
		//check for new border blocks
		for(Chunk c : s.getBorder().keySet())
			for(Location point : s.getBorder().get(c))
				if(!contains(point)) {
					addBorder(point);
					somethingWasAdded = true;
					
					if(isVisible)
						p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				}

		//return if the shapes is totally covered by the maze
		if(!somethingWasAdded)
			return;
		
		//add new fill blocks
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(!contains(point))
					addFill(point);

		//remove border blocks inside of the new shape
		for(Chunk c : s.getFill().keySet()) {
			if(!borderChunks.containsKey(c))
				continue;
			
			ArrayList<Location> currentChunk = borderChunks.get(c);
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//continue if the point isn't even in the shape
				if(!s.contains(point))
					continue;
				
				//if the point is inside the shapes border look up if is connected to blocks outside of the maze
				if(s.borderContains(point)) {
					for(Vector dir : Utils.directions()) {
						Location point2 = point.clone().add(dir);
						
						if(!contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				currentChunk.remove(point);
				if(isVisible)
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void cut(Shape s) {
		
		boolean somethingWasCutOut = false;
		
		//get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : s.getBorder().values()) {
			for(Location point : chunk) {

				if(contains(point) && !borderContains(point)) {
					addBorder(point);
					somethingWasCutOut = true;
					
					if(isVisible)
						p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				}
			}
		}
		
		if(!somethingWasCutOut)
			return;

		//remove all maze border inside the shape
		for(Chunk c : s.getFill().keySet()) {
			if(!borderChunks.containsKey(c))
				continue;

			ArrayList<Location> currentChunk = borderChunks.get(c);
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//continue if the point isn't even in the shape
				if(!s.contains(point))
					continue;
				
				//if the point is inside the shapes border, look up if it is connected to actual fill blocks
				if(s.borderContains(point)) {
					for(Vector dir : Utils.directions()) {
						Location point2 = point.clone().add(dir);
						
						if(contains(point2) && !s.contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				currentChunk.remove(point);
				removeFill(point);
				
				if(isVisible)
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			}
		}
		
		//remove all remaining maze fill inside the shape
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(contains(point) && !s.borderContains(point))
					removeFill(point);
	}
	
	@SuppressWarnings("deprecation")
	public void brush(Block b) {
		Location point = b.getLocation();
		
		if(!borderContains(point) || !isHighlighted(b))
			return;
		
		ArrayList<Location> neighbors = new ArrayList<>();
		boolean	isExternalBorder = false,
				isSealingBorder = false;

		//check if this block closes the shape somehow
		for(Vector dir : Utils.directions()) {
			Location point2 = point.clone().add(dir);
			
			if(!contains(point2))
				isExternalBorder = true;
			
			//if yes these neighbor blocks have to be stored
			else if(!borderContains(point2)) {
				isSealingBorder = true;
				neighbors.add(point2);
			}
		}

		fillChunks.get(b.getChunk()).remove(point);
		borderChunks.get(b.getChunk()).remove(point);
		
		if(!isExternalBorder || !isSealingBorder)
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
		Utils.sendBlockLater(p, newStart, Constants.MAZE_ENTRANCE);
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
	
	@SuppressWarnings("deprecation")
	private void removeFill(Location point) {
		Chunk c = point.getChunk();
		
		for(Location point2 : fillChunks.get(c))

			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ()) {
				
				fillChunks.get(c).remove(point2);
				if(isVisible)
					p.sendBlockChange(point2, point2.getBlock().getType(), point2.getBlock().getData());

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
		
		for(ArrayList<Location> chunk : fillChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
		for(ArrayList<Location> chunk : borderChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		for(Location start : entrances)
			p.sendBlockChange(start, Constants.MAZE_ENTRANCE, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public void hide() {
		if(!isVisible || p == null)
			return;
		
		isVisible = false;
		
		for(ArrayList<Location> chunk : fillChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
	}

	@SuppressWarnings("deprecation")
	public void update(ArrayList<Location> points) {
		if(!isVisible)
			return;
		
		for(Location point : points)
			if(borderContains(point))
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