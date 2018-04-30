package me.tangledmaze.gorgeousone.shapes;


import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;

public interface Shape {
	
	public ArrayList<Location> getVertices();
	public HashMap<Chunk, ArrayList<Location>> getBorder();
	public HashMap<Chunk, ArrayList<Location>> getFill();
	
	public boolean contains(Location point);
	public boolean borderContains(Location point);
	
	public void recalc(Location point);
}