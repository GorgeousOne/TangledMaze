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
	private ArrayList<Location> exits;
	
	private int size;
	
	private boolean isVisible;
	
	public Maze(Shape baseShape, Player editor) {
		p = editor;
		
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();	
		exits        = new ArrayList<>();
		
		for(Chunk c : baseShape.getFill().keySet())
			fillChunks.put(c, baseShape.getFill().get(c));
		for(Chunk c : baseShape.getBorder().keySet())
			borderChunks.put(c, baseShape.getBorder().get(c));
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int size() {
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
	
	public ArrayList<Location> getExits() {
		return exits;
	}
	
	public int getY(int x, int z) {
		int chunkX = x >> 4,
			chunkZ = z >> 4;
		
		for(Chunk c : fillChunks.keySet()) {
			
			if(c.getX() == chunkX &&
			   c.getZ() == chunkZ) {
				
				for(Location point : fillChunks.get(c))
					if(point.getX() == x && point.getZ() == z)
						return point.getBlockY();
				break;
			}
		}
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	public void process(MazeAction action) {
		for(Location point : action.getRemovedFill())
			removeFill(point);
		
		for(Location point : action.getRemovedBorder())
			removeBorder(point);
		
		for(Location point : action.getAddedFill())
			addFill(point);
		
		for(Location point : action.getAddedBorder())
			addBorder(point);
		
		for(Location point : action.getRemovedExits())
			exits.remove(point);
		
		if(isVisible) {
			for(Location point : action.getRemovedBorder())
				p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
			
			for(Location point : action.getAddedBorder())
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		}
	}

	public MazeAction getAddition(Shape s) {
		
		ArrayList<Location>
			fillAddition   = new ArrayList<>(),
			fillDeletion   = new ArrayList<>(),
			borderAddition = new ArrayList<>(),
			borderDeletion = new ArrayList<>(),
			exitDeletions  = new ArrayList<>();
		
		MazeAction addition = new MazeAction(fillAddition, fillDeletion, borderAddition, borderDeletion, exitDeletions);
		
		//check for new border blocks
		for(Chunk c : s.getBorder().keySet())
			for(Location point : s.getBorder().get(c))
				if(!contains(point))
					borderAddition.add(point);

		//add new fill blocks
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(!contains(point))
					fillAddition.add(point);

		//return if the shapes is totally covered by the maze
		if(borderAddition.isEmpty() && fillAddition.isEmpty())
			return addition;
		
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
						
						if(!contains(point2) && !s.contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				borderDeletion.add(point);
			}
		}
		
		//remove all exists inside the shape (thats the easy way)
		for(Location exit : exits)
			if(s.contains(exit)) 
				exitDeletions.add(exit);
		
		return addition;
	}
	
	public MazeAction getSubtraction(Shape s) {
		
		ArrayList<Location>
			fillAddition   = new ArrayList<>(),
			fillDeletion   = new ArrayList<>(),
			borderAddition = new ArrayList<>(),
			borderDeletion = new ArrayList<>(),
			exitDeletions  = new ArrayList<>();
		
		MazeAction deletion = new MazeAction(fillAddition, fillDeletion, borderAddition, borderDeletion, exitDeletions);

		//get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : s.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !borderContains(point))
					borderAddition.add(point);
		
		//remove all remaining maze fill inside the shape
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(contains(point) && !s.borderContains(point))
					fillDeletion.add(point);
				
		if(borderAddition.isEmpty() && fillDeletion.isEmpty())
			return deletion;

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
				borderDeletion.add(point);
			}
		}
		
		//remove all exits inside the shape 
		for(Location exit : exits)
			if(s.contains(exit))
				exitDeletions.add(exit);
		
		return deletion;
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

		if(exits.contains(point)) {
			if(exits.indexOf(point) == 0 && exits.size() > 1 && isVisible)
				p.sendBlockChange(exits.get(1), Constants.MAZE_MAIN_EXIT, (byte) 0);
			exits.remove(point);
		}
		
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
	
	@SuppressWarnings("deprecation")
	public void addExit(Block b) {
		Location newExit = b.getLocation();
		
		if(!borderContains(newExit) || !isHighlighted(b))
			return;
		
		boolean isSealing = false;
		
		for(Vector dir : Utils.shuffledCardinalDirs()) {
			Location point2 = newExit.clone().add(dir);
			
			if(contains(point2) && !borderContains(point2))
				isSealing = true;
		}
		
		if(!isSealing) {
			if(isVisible)
				Utils.sendBlockLater(p, newExit, Constants.MAZE_BORDER);
			return;
		}
		
		for(int i = 0; i < exits.size(); i++)
			if(exits.get(i).equals(newExit)) {
				
				if(isVisible) {
					Utils.sendBlockLater(p, newExit, Constants.MAZE_BORDER);
				
					if(i == 0 && exits.size() > 1)
						p.sendBlockChange(exits.get(1), Constants.MAZE_MAIN_EXIT, (byte) 0);
				}
				
				exits.remove(i);
				return;
			}
		
		if(!exits.isEmpty() && isVisible)
			p.sendBlockChange(exits.get(0), Constants.MAZE_EXIT, (byte) 0);
		exits.add(0, newExit);
		
		if(isVisible)
			Utils.sendBlockLater(p, newExit, Constants.MAZE_MAIN_EXIT);
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
		
		for(ArrayList<Location> chunk : borderChunks.values())
			for(Location point : chunk)
				p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
		
		for(Location exit : exits)
			p.sendBlockChange(exit, Constants.MAZE_EXIT, (byte) 0);
		
		if(!exits.isEmpty())
			p.sendBlockChange(exits.get(0), Constants.MAZE_MAIN_EXIT, (byte) 0);
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