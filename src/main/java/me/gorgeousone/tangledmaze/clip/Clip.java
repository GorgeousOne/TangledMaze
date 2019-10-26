package me.gorgeousone.tangledmaze.clip;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import me.gorgeousone.tangledmaze.util.Vec2;

/**A class for storing a 2D area. The area consists of a map with (unique) {@link Vec2}s for the x- and z-coordinate of each location
 * mapped to an int as a y-coordinate. Additionally there is a list of Vec2s for the border for the area.
 * Clips are typically created with ClipShape classes, e.g. {@link me.gorgeousone.tangledmaze.clip.shape.Rectangle}.
 * See {@link me.gorgeousone.tangledmaze.core.Maze} and {@link me.gorgeousone.tangledmaze.tool.ClippingTool} for uses of this class.
 */
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
		
	public Set<Vec2> getBorder() {
		return border;
	}

	public void addBorder(Vec2 loc) {
		
		if(fill.containsKey(loc))
			border.add(loc);
	}
	
	public void removeBorder(Vec2 loc) {
		border.remove(loc);
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
	
	public boolean borderContains(Vec2 loc) {
		return border.contains(loc);
	}
}