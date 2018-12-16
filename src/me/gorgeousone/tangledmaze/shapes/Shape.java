package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;

public interface Shape {
	
	public static final Rectangle RECT = new Rectangle();
	public static final Ellipse CIRCLE = new Ellipse();
	
	public int getVertexCount();
	public boolean contains(ArrayList<Location> vertices, Location point);
	public boolean borderContains(ArrayList<Location> vetices,Location point);
	
	public void createFillAndBorder(
			ArrayList<Location> vertices,
			HashMap<Chunk, ArrayList<Location>> fill,
			HashMap<Chunk, ArrayList<Location>> border);
}