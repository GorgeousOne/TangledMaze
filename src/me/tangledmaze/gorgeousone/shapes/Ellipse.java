package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.utils.Utils;

public class Ellipse implements Shape {
	
	private World world;
	private ArrayList<Location> vertices;
	private HashMap<Chunk, ArrayList<Location>> borderChunks, fillChunks;
	private Vector mid;
	private double radiusX, radiusZ, aspect;
	
	public Ellipse(ArrayList<Location> vertices) {
		if(vertices.size() < 4)
			throw new IllegalArgumentException("A rectangle neeeds 4 vertices to be determined.");
		
		this.vertices = vertices;
		world = vertices.get(0).getWorld();

		borderChunks = new HashMap<>();
		fillChunks   = new HashMap<>();
		
		radiusX = (vertices.get(1).getX() - vertices.get(0).getX() + 1) / 2;
		radiusZ = (vertices.get(3).getZ() - vertices.get(0).getZ() + 1) / 2;
		aspect = 1d * radiusZ / radiusX;
		
		mid = new Vector(
				vertices.get(0).getX() + radiusX, 0,
				vertices.get(0).getZ() + radiusZ);

		calcFillAndBorder();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Location> getVertices() {
		return (ArrayList<Location>) vertices.clone();
	}
	
	@Override
	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	@Override
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}
	
	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Vector point2 = point.toVector();
		point2.setX((point2.getX() - mid.getX()) * aspect + mid.getX());
		point2.setY(0);
		
		return mid.distance(point2) <= radiusZ - 0.25;
	}
	
	public boolean borderContains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		Vector point2 = point.toVector();
		point2.setX((point2.getX() - mid.getX()) * aspect + mid.getX());
		point2.setY(0);
		
		if(mid.distance(point2) > radiusZ - 0.25)
			return false;
		
		for(Vector dir : Utils.directions()) {
			Vector neighbour = point2.clone().add(dir.clone().setX(aspect * dir.getX()));
			
			if(mid.distance(neighbour) > radiusZ - 0.25)
				return true;
		}
		
		return false;
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
		int posX = vertices.get(0).getBlockX(),
			posZ = vertices.get(0).getBlockZ();
		
		ArrayList<Integer> verticesYs = new ArrayList<>();
		
		for(Location point : vertices)
			verticesYs.add(point.getBlockY());
		
		int maxY = Utils.getMax(verticesYs);
		
		Vector midPoint = new Vector(0, 0, 0);
		Vector iter;
		
		for(double x = -radiusX; x < radiusX; x++)
			for(double z = -radiusZ; z < radiusZ; z++) {
				
				iter = new Vector(aspect * (x+0.5), 0, z+0.5);
				Location point = new Location(world, posX + radiusX + x, maxY, posZ + radiusZ + z);
				
				//using radius-0: the circle looks edged
				//using radius-1/2: only one block sticks out at the edges
				// -> radius - 0.25 is the perfect compromise that makes the circle look smooth
				if(midPoint.distance(iter) <= radiusZ - 0.25)
					addFill(Utils.nearestSurface(point));
				else
					continue;
				
				for(Vector dir : Utils.directions()) {
					Vector neighbour = iter.clone().add(dir.clone().setX(aspect * dir.getX()));
					
					if(midPoint.distance(neighbour) > radiusZ - 0.25) {
						addBorder(Utils.nearestSurface(point));
						break;
					}
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