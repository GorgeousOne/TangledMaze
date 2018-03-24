package me.tangledmazes.gorgeousone.shapestuff;

import java.util.ArrayList;

import org.bukkit.Location;

public interface Shape {
	
	public ArrayList<Location> getBorder();
	public ArrayList<Location> getFill();
	
	public boolean contains(Location point);
}