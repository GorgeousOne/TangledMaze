package me.gorgeousone.tangledmaze.shape;

import java.util.HashSet;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.util.MazePoint;

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
	
	//TODO normal - evaluate the effort to calculate chunks new every time
	public HashSet<Chunk> getFillChunks() {
		
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