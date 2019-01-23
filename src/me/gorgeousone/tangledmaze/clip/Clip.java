package me.gorgeousone.tangledmaze.clip;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.util.MazePoint;

public class Clip {
	
	private World world;
	private TreeSet<MazePoint> fill, border;
	private HashSet<Chunk> fillChunks, borderChunks;
	
	private int size, borderSize;
	
	public Clip(World world) {

		this.world = world;
		
		fill = new TreeSet<>();
		border = new TreeSet<>();
		
		fillChunks = new HashSet<>();
		borderChunks = new HashSet<>();
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
	
	//TODO normal - evaluate the effort to calculate chunks new every time
	public HashSet<Chunk> getChunks() {
		
		HashSet<Chunk> chunks = new HashSet<>();
		
		for(MazePoint point : fill) {
			chunks.add(point.getChunk());
		}
		
		return chunks;
	}
	
	public HashSet<Chunk> getBorderChunks() {
		
		HashSet<Chunk> chunks = new HashSet<>();
		
		for(MazePoint point : border) {
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
	
	public TreeSet<MazePoint> getFill(Chunk chunk) {
		return getPointsInChunk(fill, chunk);
	}
	
	public TreeSet<MazePoint> getBorder(Chunk chunk) {
		return getPointsInChunk(border, chunk);
	}
	
	public boolean addFill(MazePoint point) {
		
		if (getWorld() != point.getWorld()) {
			return false;
		}
		
		if(fill.add(point)) {
			
			fillChunks.add(point.getChunk());
			size++;
			return true;
		}
		
		return false;
	}
	
	public boolean removeFill(MazePoint point) {
		
		if(fill.remove(point)) {
			
			if(getFill(point.getChunk()).isEmpty()) {
				fillChunks.remove(point.getChunk());
			}
			
			size--;
			return true;
		}
		
		return false;
	}
	
	public void removeFill(Collection<MazePoint> points) {
		
		HashSet<Chunk> chunks = new HashSet<>();
		
		for(MazePoint point : points) {	

			if(fill.remove(point)) {
				chunks.add(point.getChunk());
				size--;
			}
		}
		
		for(Chunk chunk : chunks) {
			
			if(getFill(chunk).isEmpty()) {
				fillChunks.remove(chunk);
			}
		}
	}
	
	public boolean addBorder(MazePoint point) {

		if (getWorld() != point.getWorld()) {
			return false;
		}
		
		if(border.add(point)) {
			borderChunks.add(point.getChunk());
			borderSize++;
			return true;
		}
		
		return false;
	}
	
	public boolean removeBorder(MazePoint point) {
		
		if(border.remove(point)) {
			
			if(getBorder(point.getChunk()).isEmpty()) {
				borderChunks.remove(point.getChunk());
			}

			borderSize--;
			return true;
		}
		
		return false;
	}
	
	public void removeBorder(Collection<MazePoint> points) {
		
		HashSet<Chunk> chunks = new HashSet<>();
		
		for(MazePoint point : points) {	

			if(border.remove(point)) {
				chunks.add(point.getChunk());
				size--;
			}
		}
		
		for(Chunk chunk : chunks) {
			
			if(getBorder(chunk).isEmpty()) {
				fillChunks.remove(chunk);
			}
		}
	}

	public boolean contains(MazePoint point) {
		return fill.contains(point);
	}
	
	public boolean borderContains(MazePoint point) {
		return border.contains(point);
	}
	
	private TreeSet<MazePoint> getPointsInChunk(TreeSet<MazePoint> set, Chunk chunk) {
		
		int chunkX = chunk.getX() * 16,
			chunkZ = chunk.getZ() * 16,
			chunkX2 = chunkX + 15,
			chunkZ2 = chunkZ + 15;
		
		MazePoint
			chunkStart = new MazePoint(null, chunkX, 0, chunkZ),
			chunkEnd   = new MazePoint(null, chunkX2, 0, chunkZ2);
					
		TreeSet<MazePoint>
			subSet = (TreeSet<MazePoint>) set.subSet(chunkStart, chunkEnd),
			chunkSet = new TreeSet<>();
		
		for(int iterZ = chunkZ; iterZ <= chunkZ2; iterZ++) {

			chunkStart.setZ(iterZ);
			chunkEnd.setZ(iterZ);
			chunkSet.addAll(subSet.subSet(chunkStart, chunkEnd));
		}
		
		return chunkSet;
	}
}