package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.utils.Utils;

//TODO cant focuson everything. the class is not finished yet, so i cant adapt it to the new system
public class Triangle {

	private World world;
	private ArrayList<Location> vertices;
//	private Vector edgeA, edgeB;
	
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private int size;
	
	public Triangle(ArrayList<Location> vertices) {
		
		if(vertices.size() < 3)
			throw new IllegalArgumentException("A triangle neeeds 3 vertices to be determined.");
		
		this.vertices = vertices;
		world = vertices.get(0).getWorld();
		
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();
		
//		edgeA = vertices.get(1).toVector().subtract(vertices.get(0).toVector());
//		edgeB = vertices.get(2).toVector().subtract(vertices.get(0).toVector());
		
 		calcFillAndBorder();
	}
	
	public World getWorld() {
		return world;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Location> getVertices() {
		return (ArrayList<Location>) vertices.clone();
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	public int size() {
		return size;
	}
	
	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		return point.getBlockX() >= vertices.get(0).getX() && point.getBlockX() <= vertices.get(2).getX() &&
			   point.getBlockZ() >= vertices.get(0).getZ() && point.getBlockZ() <= vertices.get(2).getZ();
	}
	
	public boolean borderContains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Vector v0 = vertices.get(0).toVector(),
			   v2 = vertices.get(2).toVector();
		
		return (point.getBlockX() == v0.getX() || point.getBlockX() == v2.getX()) && (point.getBlockZ() >= v0.getZ() && point.getBlockZ() <= v2.getZ()) ||
			   (point.getBlockZ() == v0.getZ() || point.getBlockZ() == v2.getZ()) && (point.getBlockX() >= v0.getX() && point.getBlockX() <= v2.getX());
	}
	
//	private void addFill(Location point) {
//		Chunk c = point.getChunk();
//		
//		if(fillChunks.containsKey(c))
//			fillChunks.get(c).add(point);
//		else
//			fillChunks.put(c, new ArrayList<>(Arrays.asList(point)));
//	}
	
	private void addBorder(Location point) {
		Chunk c = point.getChunk();

		if(borderChunks.containsKey(c))
			borderChunks.get(c).add(point);
		else
			borderChunks.put(c, new ArrayList<>(Arrays.asList(point)));
	}
	
	private void calcFillAndBorder() {
		
		ArrayList<Line> borders = new ArrayList<>(Arrays.asList(
				new Line(vertices.get(0), vertices.get(1)),
				new Line(vertices.get(1), vertices.get(2)),
				new Line(vertices.get(2), vertices.get(0))));
		
		for(Line line : borders) {
			for(Location point : line.getBorder()) {
				if(!contains(point))
					addBorder(point);
			}
		}
	}
	
	public void recalc(Location point) {
		Chunk c = point.getChunk();
		
		if(!borderChunks.containsKey(c))
			return;
		
		for(Location point2 : borderChunks.get(c))
			if(point2.getX() == point.getX() &&
			   point2.getZ() == point.getZ()) {
				point2.setY(Utils.nearestSurface(point).getY());
				break;
			}
	}
}