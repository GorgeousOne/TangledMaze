package me.tangledmazes.gorgeousone.model;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Rectangle implements Shape {

	private ArrayList<Location> vertices, border;
	
	public Rectangle(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");
		
		vertices = selection.getVertices();
		border = new ArrayList<>();
		
		Vector v0 = vertices.get(0).toVector(),
			   v2 = vertices.get(3).toVector();
		World w = selection.getWorld();
		
		for(int x = v0.getBlockX();   x <= v2.getX(); x++) {
			border.add(new Location(w, x, 0, v0.getZ()));
			border.add(new Location(w, x, 0, v2.getZ()));
		}
		for(int z = v0.getBlockZ()+1; z <  v2.getZ(); z++) {
			border.add(new Location(w, v0.getX(), 0, z));
			border.add(new Location(w, v2.getX(), 0, z));
		}
	}

	@Override
	public ArrayList<Location> getBorder() {
		return border;
	}
}