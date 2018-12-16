package me.gorgeousone.tangledmaze.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class Clip {

	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private int size, borderSize;
	
	public Clip() {
		fillChunks = new HashMap<>();
		borderChunks = new HashMap<>();
	}
	
	public int size() {
		return size;
	}
	
	public int borderSize() {
		return borderSize;
	}
	
	public Set<Chunk> getChunks() {
		return fillChunks.keySet();
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}

	public void addFill(Location point) {
		Chunk chunk = point.getChunk();
		
		if(fillChunks.containsKey(chunk))
			fillChunks.get(chunk).add(point);
		else
			fillChunks.put(chunk, new ArrayList<>(Arrays.asList(point)));

		size++;
	}

	public void addBorder(Location point) {
		this.borderChunks = borderChunks;
	}
	
	
}