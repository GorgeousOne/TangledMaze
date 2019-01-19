package me.gorgeousone.tangledmaze.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class MazePoint extends Location implements Comparable<Location> {

	public MazePoint(Location loc) {
		super(
			loc.getWorld(),
			loc.getBlockX(),
			loc.getBlockY(), 
			loc.getBlockZ());
	}

	public MazePoint(World w, double x, double y, double z) {
		super(w, (int) x, (int) y, (int) z);
	}
	
	@Override
	public void setX(double x) {
		super.setX((int) x);
	}
	
	@Override
	public void setY(double y) {
		super.setY((int) y);
	}
	
	@Override
	public void setZ(double z) {
		super.setZ((int) z);
	}
	
	@Override
	public int compareTo(Location loc) {
		
		int deltaX = Double.compare(getX(), loc.getX());

		return deltaX != 0 ? deltaX : Double.compare(getZ(), loc.getZ());
	}
	
	@Override
	public MazePoint add(Vector vec) {
		super.add(vec);
		return this;
	}
	
	@Override
	public MazePoint subtract(Vector vec) {
		super.subtract(vec);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		
		if(obj == null || !obj.getClass().isAssignableFrom(super.getClass())) {
			return false;
		}
		
		Location other = (Location) obj;
		
		if(getWorld() != other.getWorld()) {
			return false;
		}
		
		return
			getBlockX() == other.getBlockX() &&
			getBlockZ() == other.getBlockZ();
	}
	
	@Override
	public String toString() {
		return "[" + getWorld().getName() + ",x:" + getBlockX() + ",y:" + getBlockY() + ",z:" + getBlockZ() + "]";
	}
	
	@Override
	public MazePoint clone() {
		return new MazePoint(this);
	}
	
	@Override
	public int hashCode() {
		
		int hash = 3;
		
	    hash = 19 * hash + (getWorld() != null ? getWorld().hashCode() : 0);
	    hash = 19 * hash + (getBlockX() ^ getBlockX() >>> 32);
	    hash = 19 * hash + (getBlockZ() ^ getBlockZ() >>> 32);
	    
	    return hash;
	}
}