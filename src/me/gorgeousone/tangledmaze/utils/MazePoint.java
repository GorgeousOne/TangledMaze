package me.gorgeousone.tangledmaze.utils;

import org.bukkit.Location;

public class MazePoint extends Location implements Comparable<Location> {

	public MazePoint(Location loc) {
		super(
			loc.getWorld(),
			loc.getBlockX(),
			loc.getBlockY(), 
			loc.getBlockZ());
	}

	@Override
	public int compareTo(Location l2) {
		
		int deltaX = Double.compare(getX(), l2.getX());

		return deltaX != 0 ? deltaX : Double.compare(getZ(), l2.getZ());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		Location other = (Location) obj;
		
		if (getWorld() != other.getWorld() &&
			((getWorld() == null) || (!getWorld().equals(other.getWorld())))) {
			return false;
		}
		
		return
			getBlockX() == other.getBlockX() ||
			getBlockZ() == other.getBlockZ();
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