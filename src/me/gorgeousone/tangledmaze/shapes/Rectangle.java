package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.tools.Clip;
import me.gorgeousone.tangledmaze.utils.MazePoint;
import me.gorgeousone.tangledmaze.utils.Utils;

public class Rectangle implements Shape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}
	
	@Override
	public boolean contains(ArrayList<Location> vertices, Location point) {
		
		if(vertices.size() < 4)
			throw new IllegalArgumentException("4 vertices needed for this calculation.");
		
		return point.getBlockX() >= vertices.get(0).getX() && point.getBlockX() <= vertices.get(2).getX() &&
			   point.getBlockZ() >= vertices.get(0).getZ() && point.getBlockZ() <= vertices.get(2).getZ();
	}
	
	@Override
	public boolean borderContains(ArrayList<Location> vertices, Location point) {
		Vector v0 = vertices.get(0).toVector(),
			   v2 = vertices.get(2).toVector();
		
		return (point.getBlockX() == v0.getX() || point.getBlockX() == v2.getX()) && (point.getBlockZ() >= v0.getZ() && point.getBlockZ() <= v2.getZ()) ||
			   (point.getBlockZ() == v0.getZ() || point.getBlockZ() == v2.getZ()) && (point.getBlockX() >= v0.getX() && point.getBlockX() <= v2.getX());
	}
	
	@Override
	public Clip createClip(ArrayList<Location> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("A rectangle neeeds 2 vertices to be determined.");
		
		Location v0 = vertices.get(0),
				 v2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Utils.createRectangularVertices(v0, v2));
		
		v0 = vertices.get(0);
		v2 = vertices.get(2);
		
		int maxY = Utils.getMaxY(vertices);
		
		Clip clip = new Clip(v0.getWorld());
		
		for(int x = v0.getBlockX(); x <= v2.getX(); x++) {
			for(int z = v0.getBlockZ(); z <= v2.getZ(); z++) {
				
				Location point = new Location(vertices.get(0).getWorld(), x, maxY, z);
				
				clip.addFill(new MazePoint(Utils.nearestSurface(point)));

				if(x == v0.getX() || x == v2.getX() ||
				   z == v0.getZ() || z == v2.getZ()) {
					clip.addBorder(new MazePoint(Utils.nearestSurface(point)));
				}
			}
		}
		
		return clip;
	}
}