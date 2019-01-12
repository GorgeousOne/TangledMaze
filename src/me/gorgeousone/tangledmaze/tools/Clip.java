package me.gorgeousone.tangledmaze.tools;

import java.util.HashSet;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.utils.MazePoint;

public class Clip {
	
	private World world;
	private TreeSet<MazePoint> fill, border;
	
	private int size, borderSize;
	
	public Clip(World world) {
		
		if(world == null)
			throw new IllegalArgumentException("World cannot be null");
		
		this.world = world;
		
		fill = new TreeSet<>();
		border = new TreeSet<>();
	}
	
	public World getWorld() {
		return world;
	}
	
	public int size() {
		return size;
	}
	
	public int borderSize() {
		return borderSize;
	}
	
	public HashSet<Chunk> getChunks() {
		
		HashSet<Chunk> chunks = new HashSet<>();
		
		//TODO low - evaluate the effort of getting chunks
		for(MazePoint point : fill) {
			chunks.add(point.getChunk());
		}
		
		return chunks;
	}
	
	public TreeSet<MazePoint> getFill() {
		return fill;
	}

	public TreeSet<MazePoint> getBorder() {
		return border;
	}
	
	public boolean addFill(MazePoint point) {
		
		if (getWorld() != point.getWorld()) {
			return false;
		}
		
		if(fill.add(point)) {
			size++;
			return true;
		}
		
		return false;
	}
	
	public boolean removeFill(MazePoint point) {
		
		if(fill.remove(point)) {
			size--;
			return true;
		}
		
		return false;
	}
	
	public boolean addBorder(MazePoint point) {

		if (getWorld() != point.getWorld()) {
			return false;
		}
		
		if(border.add(point)) {
			borderSize++;
			return true;
		}
		
		return false;
	}
	
	public boolean removeBorder(MazePoint point) {
		
		if(border.remove(point)) {
			borderSize--;
			return true;
		}
		
		return false;
	}
	
	public boolean contains(MazePoint point) {
		return fill.contains(point);
	}
	
	public boolean borderContains(MazePoint point) {
		return border.contains(point);
	}
}