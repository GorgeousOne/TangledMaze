package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.tools.Clip;

public interface Shape {
	
	public static final Rectangle RECT = new Rectangle();
	public static final Ellipse CIRCLE = new Ellipse();
	
	public int getVertexCount();
	public boolean contains(ArrayList<Location> vertices, Location point);
	public boolean borderContains(ArrayList<Location> vetices,Location point);
	
	public Clip createClip(ArrayList<Location> vertices);
}