package me.tangledmaze.gorgeousone.shapes;


import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public interface Shape {
	
	public World getWorld();
	public ArrayList<Location> getVertices();
	public HashMap<Chunk, ArrayList<Location>> getBorder();
	public HashMap<Chunk, ArrayList<Location>> getFill();
	
	public int size();
	
	public boolean contains(Location point);
	public boolean borderContains(Location point);
	
	public void recalc(Location point);
}