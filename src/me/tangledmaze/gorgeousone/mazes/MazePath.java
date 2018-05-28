package me.tangledmaze.gorgeousone.mazes;

import java.util.ArrayList;

import org.bukkit.util.Vector;

public class MazePath {
	
	Vector corner;
	ArrayList<Vector> fill;
	
	public MazePath(int x0, int z0, int lenght, int width) {
		corner = new Vector(x0, 0, z0);
		
		fill = new ArrayList<>();
		
		for(int x = x0; x < x0 + lenght; x++)
			for(int z = z0; z < z0 + width; z++)
				fill.add(new Vector(x, 0, z));
	}

	public Vector getCorner() {
		return corner;
	}
	
	public ArrayList<Vector> getFill() {
		return fill;
	}
}