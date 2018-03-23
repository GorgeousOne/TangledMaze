package me.tangledmazes.gorgeousone.model;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Rectangle implements Shape {

	private ArrayList<Location> vertices;
	
	public Rectangle(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");
		
		vertices = selection.getVertices();
		//vertices.addAll(selection.getVertices());
	}

	@Override
	public ArrayList<Vector> getBorder() {
		ArrayList<Vector> points = new ArrayList<>();

		Location v0 = vertices.get(0).clone(),
				 v2 = vertices.get(3).clone();
		 
		for(int x = v0.getBlockX();  x <= v2.getX(); x++) {
			points.add(new Vector(x, 0, v0.getZ()));
			points.add(new Vector(x, 0, v2.getZ()));
		}
		for(int z = v0.getBlockZ()+1; z < v2.getZ(); z++) {
			points.add(new Vector(v0.getX(), 0, z));
			points.add(new Vector(v2.getX(), 0, z));
		}
		
		return points;
	}
}
