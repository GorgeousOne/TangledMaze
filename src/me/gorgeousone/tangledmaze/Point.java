package me.gorgeousone.tangledmaze;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Point {
	
	private int x, y, z;
	
	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(double x, double y, double z) {
		this((int) x, (int) y, (int) z);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void add(Vector v) {
		x += v.getBlockX();
		y += v.getBlockY();
		z += v.getBlockZ();
	}
	
	public Vector toVector() {
		return new Vector(x, y, z);
	}
	public Location toLocation(World w) {
		return new Location(w, x, y, z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() != Point.class)
			return false;
		
		return ((Point) obj).getX() == x &&
			   ((Point) obj).getZ() == z;
	}
}