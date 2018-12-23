package me.gorgeousone.tangledmaze.tools;

import java.util.ArrayList;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.utils.MazePoint;

public class Clip {
	
	private World world;
	private TreeSet<MazePoint> fill, border;
	private ArrayList<Chunk> chunks;
	
	private int size, borderSize;
	
	public Clip(World world) {
		
		this.world = world;
		
		fill = new TreeSet<>();
		border = new TreeSet<>();
		chunks = new ArrayList<>();
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
	
	@SuppressWarnings("unchecked")
	public ArrayList<Chunk> getChunks() {
		return (ArrayList<Chunk>) chunks.clone();
	}
	
	public TreeSet<MazePoint> getFill() {
		return fill;
	}

	public TreeSet<MazePoint> getBorder() {
		return border;
	}
	
	public void addFill(MazePoint point) {
		
		if (getWorld() != point.getWorld() &&
			((getWorld() == null) || (!getWorld().equals(point.getWorld())))) {
			return;
		}
		
		//TODO find a way to remove empty chunks
		if(fill.add(point)) {
			size++;
			chunks.add(point.getChunk());
		}
	}
	
	public boolean removeFill(MazePoint point) {
		
		if(fill.remove(point)) {
			size--;
			return true;
		}
		
		return false;
	}
	
	//TODO make this booleans
	public void addBorder(MazePoint point) {

		if (getWorld() != point.getWorld() &&
			((getWorld() == null) || (!getWorld().equals(point.getWorld())))) {
			return;
		}
		
		if(border.add(point)) {
			borderSize++;
			chunks.add(point.getChunk());
		}
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