package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.Utils;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class Rectangle implements Shape {

	private World world;
	private ArrayList<Location> vertices;
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	
	public Rectangle(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");
		
		world    = selection.getWorld();
		vertices = selection.getVertices();
		
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();
		
		calcFillAndBorder();
	}
	
	@Override
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	@Override
	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	@Override
	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		return point.getX() >= vertices.get(0).getX() && point.getX() <= vertices.get(2).getX() &&
			   point.getZ() >= vertices.get(0).getZ() && point.getZ() <= vertices.get(2).getZ();
	}
	
	@Override
	public boolean borderContains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Vector v0 = vertices.get(0).toVector(),
			   v2 = vertices.get(2).toVector();
		
		return (point.getBlockX() == v0.getX() || point.getBlockX() == v2.getX()) && (point.getBlockZ() >= v0.getZ() && point.getBlockZ() <= v2.getZ()) ||
			   (point.getBlockZ() == v0.getZ() || point.getBlockZ() == v2.getZ()) && (point.getBlockX() >= v0.getX() && point.getBlockX() <= v2.getX());
	}
	
	private void addFill(Location point) {
		Chunk c = point.getChunk();
		
		if(fillChunks.containsKey(c))
			fillChunks.get(c).add(point);
		else
			fillChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}
	
	private void addBorder(Location point) {
		Chunk c = point.getChunk();

		if(borderChunks.containsKey(c))
			borderChunks.get(c).add(point);
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}
	
	private void calcFillAndBorder() {
		Vector v0 = vertices.get(0).toVector(),
			   v2 = vertices.get(2).toVector();
		
		int maxY = Utils.maxBlockY(vertices);
		Bukkit.broadcastMessage("" + maxY);
		
		for(int x = v0.getBlockX(); x <= v2.getX(); x++)
			for(int z = v0.getBlockZ(); z <= v2.getZ(); z++) {
				
				Location point = new Location(world, x, maxY, z);
				addFill(Utils.getNearestSurface(point));
				
				if(x == v0.getX() || x == v2.getX() ||
				   z == v0.getZ() || z == v2.getZ())
					addBorder(Utils.getNearestSurface(point));
			}
	}
	
	public void recalc(Location point) {
		Chunk c = point.getChunk();
		
		if(!borderChunks.containsKey(c))
			return;
		
		for(Location point2 : borderChunks.get(c))
			if(point2.getX() == point.getX() &&
			   point2.getZ() == point.getZ()) {
				point2.setY(Utils.getNearestSurface(point).getY());
				break;
			}
	}
}