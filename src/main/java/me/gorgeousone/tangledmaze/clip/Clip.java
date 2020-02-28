package me.gorgeousone.tangledmaze.clip;

import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	
	public int size() {
		return fill.size();
	}
	
	public int borderSize() {
		return border.size();
	}
	
	public Set<Vec2> getFill() {
		return fill.keySet();
	}
	
	public Set<Entry<Vec2, Integer>> getFillEntries() {
		return fill.entrySet();
	}
	
	public void addFill(Vec2 point, int height) {
		fill.put(point, height);
	}
	
	public void addAllFill(Map<Vec2, Integer> points) {
		fill.putAll(points);
	}
	
	public void removeFill(Vec2 point) {
		if (fill.remove(point) != null)
			removeBorder(point);
	}
	
	public Set<Vec2> getBorder() {
		return border;
	}
	
	public void addBorder(Vec2 point) {
		
		if (fill.containsKey(point))
			border.add(point);
	}
	
	
	public void removeBorder(Vec2 point) {
		border.remove(point);
	}
	
	public boolean isBorderBlock(Block block) {
		
		if (block.getWorld() != getWorld())
			return false;
		
		Vec2 blockVec = new Vec2(block);
		return borderContains(blockVec) && getHeight(blockVec) == block.getY();
	}
	
	public Set<Location> getBorderBlocks() {
		
		Set<Location> blocks = new HashSet<>();
		
		for (Vec2 border : border) {
			blocks.add(getBlockLoc(border));
		}
		
		return blocks;
	}
	
	public Location getBlockLoc(Vec2 point) {
		return new Location(getWorld(), point.getX(), getHeight(point), point.getZ());
	}
	
	public int getHeight(Vec2 point) {
		return fill.get(point);
	}
	
	public boolean contains(Vec2 point) {
		return fill.containsKey(point);
	}
	
	public boolean borderContains(Vec2 point) {
		return border.contains(point);
	}
	
	public boolean sealsBorder(Vec2 point, Direction[] directions) {
		
		boolean touchesFill = false;
		boolean touchesExternal = false;
		
		for (Direction dir : directions) {
			Vec2 neighbor = point.clone().add(dir.getVec2());
			
			if (!contains(neighbor))
				touchesExternal = true;
			
			else if (!borderContains(neighbor))
				touchesFill = true;
			
			if (touchesFill && touchesExternal)
				return true;
		}
		
		return false;
	}
}