package me.gorgeousone.tangledmaze.clip.shape;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Utils;

public abstract class ClipShape {
	
	public static final Rectangle RECT = new Rectangle();
	public static final Circle CIRCLE = new Circle();
	
	public abstract int getVertexCount();
	
	public abstract Clip createClip(ArrayList<Location> vertices);
	
	public static ArrayList<Location> createRectangularVertices(Location vertex0, Location vertex2) {
		
		ArrayList<Location> vertices = new ArrayList<>();
		World world = vertex0.getWorld();
		
		int maxY = Math.max(vertex0.getBlockY(), vertex2.getBlockY());
		
		int minX = Math.min(vertex0.getBlockX(), vertex2.getBlockX()),
			minZ = Math.min(vertex0.getBlockZ(), vertex2.getBlockZ()),
			maxX = Math.max(vertex0.getBlockX(), vertex2.getBlockX()),
			maxZ = Math.max(vertex0.getBlockZ(), vertex2.getBlockZ());
		
		vertices = new ArrayList<>(Arrays.asList(
				Utils.nearestSurface(new Location(world, minX, maxY, minZ)),
				Utils.nearestSurface(new Location(world, maxX, maxY, minZ)),
				Utils.nearestSurface(new Location(world, maxX, maxY, maxZ)),
				Utils.nearestSurface(new Location(world, minX, maxY, maxZ))));
		
		return vertices;
	}
}