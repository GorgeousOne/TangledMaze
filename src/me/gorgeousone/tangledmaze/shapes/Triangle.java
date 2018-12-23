package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.tools.Clip;

public class Triangle implements Shape {
	
	@Override
	public int getVertexCount() {
		return 3;
	}
	
	@Override
	public boolean contains(ArrayList<Location> vertices, Location point) {
		return false;
	}
	
	@Override
	public boolean borderContains(ArrayList<Location> vertices, Location point) {
		return false;
	}
	
	@Override
	public Clip createClip(ArrayList<Location> vertices) {
		return null;
	}
}