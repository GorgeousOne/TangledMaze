package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Entry;
import me.tangledmaze.gorgeousone.utils.Utils;

public class Maze {
	
	private World world;
	private Player owner;
	
	private ActionHistory history;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> exits;
	private ArrayList<Entry<Material, Byte>> wallComposition;
	
	private int size, borderSize, wallHeight;
	
	public Maze(Shape baseShape, Player owner) {
		world = baseShape.getWorld();

		this.owner = owner;
		
		history = new ActionHistory();
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();	
		exits        = new ArrayList<>();
		
		size = 0;
		borderSize = 0;
		
		wallHeight = 3;
		
		for(Chunk c : baseShape.getFill().keySet()) {
			fillChunks.put(c, baseShape.getFill().get(c));
			size += baseShape.getFill().get(c).size();
		}
		
		for(Chunk c : baseShape.getBorder().keySet()) {
			borderChunks.put(c, baseShape.getBorder().get(c));
			borderSize += baseShape.getBorder().get(c).size();
		}
	}
	
	//getters
	public World getWorld() {
		return world;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public int size() {
		return size;
	}
	
	public int borderSize() {
		return borderSize;
	}
	
	public int getWallHeight() {
		return wallHeight;
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
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getExits() {
		return (ArrayList<Location>) exits.clone();
	}
	
	public ArrayList<Entry<Material, Byte>> getWallComposition() {
		return wallComposition;
	}

	//setters
	public void addExit(Location point) {
		if(!canBeExit(point))
			return;
		if(exits.contains(point))
			return;
		
		exits.add(0, point);
	}
	
	public void removeExit(Location point) {
		exits.remove(point);
	}
	
	public void setWallHeight(int height) {
		wallHeight = height;
	}
	
	public void setWallComposition(ArrayList<Entry<Material, Byte>> composition) {
		wallComposition = composition;
	}
	
	public void process(MazeAction action) {
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
	
	public boolean undoLast() {
		if(history.isEmpty())
			return false;
		
		MazeAction action = history.popLastAction();
		
		for(Location point : action.getRemovedFill())
			addFill(point);
	
		for(Location point : action.getRemovedBorder())
			addBorder(point);
	
		for(Location point : action.getAddedFill())
			removeFill(point);

		for(Location point : action.getAddedBorder())
			removeBorder(point);
		
		return true;
	}

	public MazeAction getAddition(Shape s) {
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
		
		if(!world.equals(s.getWorld()))
			return addition;
		
		//check for new border blocks
		for(Chunk c : s.getBorder().keySet())
			for(Location point : s.getBorder().get(c))
				if(!contains(point))
					addedBorder.add(point);

		//add new fill blocks
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(!contains(point))
					addedFill.add(point);

		//return if the shapes is totally covered by the maze
		if(addedBorder.isEmpty() && addedFill.isEmpty())
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
				removedBorder.add(point);
			}
		}
		
		//remove all exists inside the shape (thats the easy way)
		for(Location exit : exits)
			if(s.contains(exit)) 
				removedExits.add(exit);
		
		return addition;
	}
	
	public MazeAction getDeletion(Shape s) {
		
		ArrayList<Location>
			addedFill   = new ArrayList<>(),
			addedBorder = new ArrayList<>(),
			removedBorder = new ArrayList<>(),
			removedExits  = new ArrayList<>();
		
		MazeAction deletion = new MazeAction(
				new ArrayList<>(),
				addedFill,
				addedBorder,
				removedBorder,
				removedExits);

		if(!world.equals(s.getWorld()))
			return deletion;
		
		//get new border points where shape is cutting into maze
		for(ArrayList<Location> chunk : s.getBorder().values())
			for(Location point : chunk)
				if(contains(point) && !borderContains(point))
					addedBorder.add(point);
		
		//remove all remaining maze fill inside the shape
		for(Chunk c : s.getFill().keySet())
			for(Location point : s.getFill().get(c))
				if(contains(point) && !s.borderContains(point))
					addedFill.add(point);
				
		if(addedBorder.isEmpty() && addedFill.isEmpty())
			return deletion;

		//remove all maze border inside the shape
		for(Chunk c : s.getFill().keySet()) {
			if(!borderChunks.containsKey(c))
				continue;

			ArrayList<Location> currentChunk = borderChunks.get(c);
			
			borderloop:
			for(int i = currentChunk.size()-1; i >= 0; i--) {
				Location point = currentChunk.get(i);
				
				//continue if the border point isn't even in the cutting shape
				if(!s.contains(point))
					continue;
				
				//if the point is inside the shapes border, look up if it touches actual fill blocks (otherwise corners get removed)
				if(s.borderContains(point)) {
					for(Vector dir : Utils.directions()) {
						Location point2 = point.clone().add(dir);
						
						if(contains(point2) && !s.contains(point2))
							continue borderloop;
					}
				}
				
				//otherwise remove the block
				removedBorder.add(point);
				addedFill.add(point);
			}
		}
		
		//remove all exits inside the shape 
		for(Location exit : exits)
			if(s.contains(exit))
				removedExits.add(exit);
		
		return deletion;
	}
	
	public MazeAction reduce(Block b) {
		Location point = b.getLocation();
		
		//can't remove what isn't part of the border
		if(!isHighlighted(b))
			return null;
		
		ArrayList<Location>
			removedFill   = new ArrayList<>(),
			removedBorder = new ArrayList<>(),
			addedBorder   = new ArrayList<>(),
			removedExits  = new ArrayList<>();
		
		MazeAction action = new MazeAction(
				new ArrayList<>(),
				removedFill,
				addedBorder,
				removedBorder,
				removedExits);
		
		//remove clicked block from maze
		removedFill.add(point);
		removedBorder.add(point);

		//remove any exit on this block
		if(exits.contains(point))
			removedExits.add(point);

		ArrayList<Location> neighbors = new ArrayList<>();
		
		boolean	isExternalBorder = false,
				isSealingBorder = false;
		
		ArrayList<Vector> directions = Utils.directions();
		Location point2;
		
		//check if this block closes the shape somehow (also diagonally)
		for(Vector dir : directions) {
			point2 = Utils.nearestSurface(point.clone().add(dir));
			
			//that is true if it touches a block outside the maze 
			if(!contains(point2)) {
				isExternalBorder = true;
				continue;
			
			//but if the other block is a fill block, it will have to replace the block in the border
			}else if(!borderContains(point2)) {
				isSealingBorder = true;
				neighbors.add(point2);
				continue;
			}
		}
		
		//if it seals the shape the neighbor blocks have to replace it
		if(isExternalBorder && isSealingBorder)
			for(Location point3 : neighbors)
				addedBorder.add(Utils.nearestSurface(point3));
		
		//remove exits that can't exist any further bc the lack of contact
		for(Vector dir : directions) {
			point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(exitsContain(point2)) {
				Location point3;

				boolean touchesFill = false,
						touchesOutside = false;
				
				//check if the point is touching fill and the outside of the maze
				for(Vector dir2 : Utils.cardinalDirs()) {
					point3 = Utils.nearestSurface(point.clone().add(dir2));
					
					if(contains(point3) && !removedFill.contains(point3))
						if(!borderContains(point2) || removedBorder.contains(point3))
							touchesFill = true;
					else
						touchesOutside = true;
				}
				
				if(!touchesFill || !touchesOutside)
					removedExits.add(point2);
			}
		}
		
		boolean standsOut;
		
		//detect outstanding neighbor borders of the block (in cardinal directions)
		for(Vector dir : Utils.cardinalDirs()) {
			point2 = Utils.nearestSurface(point.clone().add(dir));

			if(!borderContains(point2))
				continue;

			Location point3;
			standsOut = true;

			//check if the neighbor is touching fill-only blocks of the maze
			for(Vector dir2 : directions) {
				point3 = Utils.nearestSurface(point2.clone().add(dir2));
				
				//fill only is also dependent on border that will be added
				if(contains(point3) && !borderContains(point3) && !addedBorder.contains(point3)) {
					standsOut = false;
					break;
				}
			}

			//remove the neighbor if it still stands out
			if(standsOut) {
				removedFill.add(point2);
				removedBorder.add(point2);
			}
		}

		return action;
	}
	
	public MazeAction enlarge(Block b) {
		Location point = b.getLocation();
		
		//can't remove what is not part of the border
		if(!isHighlighted(b))
			return null;
		
		ArrayList<Location>
			addedFill       = new ArrayList<>(),
			removedBorder = new ArrayList<>(),
			addedBorder   = new ArrayList<>(),
			removedExits  = new ArrayList<>();
		
		MazeAction action = new MazeAction(
				addedFill,
				new ArrayList<>(),
				addedBorder,
				removedBorder,
				removedExits);
		
		removedBorder.add(point);
		
		//if there is an exit on this point remove it
		if(exits.contains(point))
			removedExits.add(point);

		ArrayList<Vector>
			directions = Utils.directions(),
			cardinalDirs = Utils.cardinalDirs();
		
		Location point2;
		
		//look for neighbors that can replace this border block
		for(Vector dir : directions) {
			point2 = Utils.nearestSurface(point.clone().add(dir));
			
			//add all non-maze neighbors as border (and fill)
			if(!contains(point2)) {
				addedFill.add(Utils.nearestSurface(point2));
				addedBorder.add(Utils.nearestSurface(point2));
				continue;
			}
			
			//remove neighbor exits that are now no usable anymore
			if(exits.contains(point2) && !canBeExit(point2))
				removedExits.add(point2);
		}
		
		//remove exits that can't exist any further bc the lack of contact
		for(Vector dir : directions) {
			point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(exitsContain(point2)) {
				Location point3;

				boolean touchesFill = false,
						touchesOutside = false;
				
				//check if the point is touching fill and the outside of the maze
				for(Vector dir2 : Utils.cardinalDirs()) {
					point3 = Utils.nearestSurface(point.clone().add(dir2));
					
					if(contains(point3) || !addedFill.contains(point3))
						if(!borderContains(point2) && !addedBorder.contains(point3) || removedBorder.contains(point3))
							touchesFill = true;
					else
						touchesOutside = true;
				}
				
				if(!touchesFill || !touchesOutside)
					removedExits.add(point2);
			}
		}
		
		boolean standsOut;
		
		//look for neighbors, that are now standing out (inside the maze)
		for(Vector dir : cardinalDirs) {
			point2 = Utils.nearestSurface(point.clone().add(dir));
			
			if(!borderContains(point2))
				continue;
			
			Location point3;
			standsOut = true;
			
			//check if the neighbors are connected to other border parts (in cardinal directions)
			for(Vector dir2 : directions) {
				point3 = Utils.nearestSurface(point2.clone().add(dir2));
				
				if(!contains(point3) && !addedFill.contains(point3)) {
					standsOut = false;
					break;
				}
			}

			//if they are totally without connection 
			if(standsOut)
				removedBorder.add(point2);
		}
		
		return action;
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
			borderChunks.get(c).add(Utils.nearestSurface(point));
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(Utils.nearestSurface(point))));
		
		borderSize++;
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
				borderSize--;
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
	
	public boolean exitsContain(Location point) {
		for(Location point2 : exits)
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		
		return false;
	}
	
	public boolean canBeExit(Location point) {
		Location point2;

		boolean touchesFill = false,
				touchesOutside = false;
		
		//check if the point is touching fill and the outside of the maze
		for(Vector dir : Utils.cardinalDirs()) {
			point2 = point.clone().add(dir);
			
			if(contains(point2))
				if(!borderContains(point2))
				touchesFill = true;
				
			else
				touchesOutside = true;
		}
		
		return touchesFill && touchesOutside;
	}

	public void recalc(Location point) {
		if(!borderContains(point))
			return;
		
		removeBorder(point);
		addBorder(Utils.nearestSurface(point));
	}
}