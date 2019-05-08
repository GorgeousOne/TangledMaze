package me.gorgeousone.tangledmaze.clip;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.util.MazePoint;

public class Clip {
	
	private World world;
	private Set<MazePoint> fill, border;
	private Set<Chunk> fillChunks, borderChunks;
	
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
	
	@SuppressWarnings("unchecked")
	public Set<Chunk> getChunks() {
		return (Set<Chunk>) ((HashSet<Chunk>) fillChunks).clone();
	}
	
	@SuppressWarnings("unchecked")
	public Set<Chunk> getBorderChunks() {
		return (Set<Chunk>) ((HashSet<Chunk>) borderChunks).clone();
	}
	
	public Set<MazePoint> getFilling() {
		return fill;
	}

	public Set<MazePoint> getBorder() {
		return border;
	}
	
	public Set<MazePoint> getFilling(Chunk chunk) {
		return getPointsInChunk((TreeSet<MazePoint>) fill, chunk);
	}
	
	public Set<MazePoint> getBorder(Chunk chunk) {
		return getPointsInChunk((TreeSet<MazePoint>) border, chunk);
	}
	
	public boolean addFilling(MazePoint point) {
		
		if(getWorld() != point.getWorld())
			return false;
		
		if(fill.add(point)) {
			
			fillChunks.add(point.getChunk());
			size++;
			return true;
		}

		return false;
	}
	
	public boolean removeFilling(MazePoint point) {
		
		if(fill.remove(point)) {
			
			if(getFilling(point.getChunk()).isEmpty())
				fillChunks.remove(point.getChunk());
			
			size--;
			return true;
		}
		
		return false;
	}
	
	public void removeFilling(Collection<MazePoint> points) {
		
		HashSet<Chunk> changedChunks = new HashSet<>();
		
		for(MazePoint point : points) {	

			if(fill.remove(point)) {
				changedChunks.add(point.getChunk());
				size--;
			}
		}
		
		for(Chunk chunk : changedChunks) {
			
			if(getFilling(chunk).isEmpty())
				fillChunks.remove(chunk);
		}
	}
	
	public boolean addBorder(MazePoint point) {

		if(getWorld() != point.getWorld())
			return false;
		
		if(border.add(point)) {

			borderChunks.add(point.getChunk());
			borderSize++;
			return true;
		}
		
		return false;
	}
	
	public boolean removeBorder(MazePoint point) {
		
		if(!border.remove(point))
			return false;
			
		if(getBorder(point.getChunk()).isEmpty())
			borderChunks.remove(point.getChunk());

		borderSize--;
		return true;
	}
	
	public void removeBorder(Collection<MazePoint> points) {
		
		HashSet<Chunk> changedChunks = new HashSet<>();
		
		for(MazePoint point : points) {	

			if(border.remove(point)) {
				changedChunks.add(point.getChunk());
				size--;
			}
		}
		
		for(Chunk chunk : changedChunks) {
			
			if(getBorder(chunk).isEmpty())
				fillChunks.remove(chunk);
		}
	}

	public boolean contains(MazePoint point) {

		if(point.getWorld() != getWorld())
			return false;

		return fill.contains(point);
	}
	
	public boolean borderContains(MazePoint point) {

		if(point.getWorld() != getWorld())
			return false;

		return border.contains(point);
	}
	
	private Set<MazePoint> getPointsInChunk(TreeSet<MazePoint> set, Chunk chunk) {
		
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