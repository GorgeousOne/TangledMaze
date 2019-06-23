package me.gorgeousone.tangledmaze.clip;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import me.gorgeousone.tangledmaze.util.Vec2;

public class Clip {
	
	private World world;
	private Map<Vec2, Integer> fill;
	private Set<Vec2> border;
	
	public Clip(World world) {

		this.world = world;
		
		fill = new TreeMap<>();
		border = new TreeSet<>();
	}
	
	public World getWorld() {
		return world;
	}
	
	public Set<Entry<Vec2, Integer>> getFillSet() {
		return fill.entrySet();
	}
	
	public Set<Vec2> getFill() {
		return fill.keySet();
	}
	
	public Set<Vec2> getFill(Chunk chunk) {
		return getLocsInChunk((TreeSet<Vec2>) fill.keySet(), chunk);
	}

	public void addFill(Vec2 loc, int height) {
		fill.put(loc, height);
	}
	
	public void addAllFill(Map<Vec2, Integer> locs) {
		fill.putAll(locs);
	}
	
	public void removeFill(Vec2 loc) {

		if(fill.remove(loc) != null)
			removeBorder(loc);
	}
		
	public void removeFill(Location loc) {
		removeFill(new Vec2(loc));
	}
	
	public Set<Vec2> getBorder() {
		return border;
	}

	public Set<Vec2> getBorder(Chunk chunk) {
		return getLocsInChunk((TreeSet<Vec2>) getBorder(), chunk);
	}

	public void addBorder(Vec2 loc) {
		
		if(fill.containsKey(loc))
			border.add(loc);
	}
	
	public void removeBorder(Vec2 loc) {
		border.remove(loc);
	}
	
	public void removeBorder(Location loc) {
		border.remove(new Vec2(loc));
	}
	
	public int size() {
		return fill.size();
	}

	public int borderSize() {
		return border.size();
	}
	
	public int getHeight(Vec2 loc) {
		return fill.get(loc);
	}
	
	public Location getLocation(Vec2 loc) {
		return new Location(getWorld(), loc.getX(), getHeight(loc), loc.getZ());
	}
	
	public Set<Location> getBorderBlocks() {
		
		Set<Location> blocks = new HashSet<>();
		
		for(Vec2 border : getBorder())
			blocks.add(new Location(getWorld(), border.getX(), getHeight(border), border.getZ()));
		
		return blocks;
	}

	public Set<Location> getBorderBlocks(Chunk chunk) {
		
		Set<Location> blocks = new HashSet<>();
		
		for(Vec2 border : getBorder(chunk))
			blocks.add(new Location(getWorld(), border.getX(), getHeight(border), border.getZ()));
		
		return blocks;
	}

	public boolean isBorderBlock(Block block) {
		
		if(block.getWorld() != getWorld())
			return false;
		
		Vec2 blockVec = new Vec2(block);
		
		return borderContains(blockVec) && getHeight(blockVec) == block.getY();
	}
	
	public boolean contains(Location loc) {

		if(loc.getWorld() != getWorld())
			return false;

		return contains(new Vec2(loc));
	}
	
	public boolean contains(Vec2 loc) {
		return fill.containsKey(loc);
	}
	
	public boolean borderContains(Location loc) {
		return borderContains(new Vec2(loc));
	}
	
	public boolean borderContains(Vec2 loc) {
		return border.contains(loc);
	}
	
	private Set<Vec2> getLocsInChunk(TreeSet<Vec2> set, Chunk chunk) {
	
		int chunkMinX = chunk.getX() * 16,
			chunkMinZ = chunk.getZ() * 16,
			chunkMaxX = chunkMinX + 15,
			chunkMaxZ = chunkMinZ + 15;
		
		Vec2 chunkStart = new Vec2(chunkMinX, chunkMinZ);
		Vec2 chunkEnd   = new Vec2(chunkMaxX, chunkMaxZ);
					
		TreeSet<Vec2> subSet = (TreeSet<Vec2>) set.subSet(chunkStart, chunkEnd);
		TreeSet<Vec2> chunkSet = new TreeSet<>();
		
		for(int iterZ = chunkMinZ; iterZ <= chunkMaxZ; iterZ++) {
	
			chunkStart.setZ(iterZ);
			chunkEnd.setZ(iterZ);
			chunkSet.addAll(subSet.subSet(chunkStart, chunkEnd));
		}
		
		return chunkSet;
	}
}