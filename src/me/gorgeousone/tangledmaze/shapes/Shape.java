package me.gorgeousone.tangledmaze.shapes;


import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;

public interface Shape {
	
	public int requieredVertices();

	public boolean contains(ArrayList<Location> vertices, Location point);
	public boolean borderContains(ArrayList<Location> vetices,Location point);
	
	public void calcFillAndBorderAndPerhapsVertices(
			ArrayList<Location> vertices,
			HashMap<Chunk, ArrayList<Location>> fill,
			HashMap<Chunk, ArrayList<Location>> border);
}