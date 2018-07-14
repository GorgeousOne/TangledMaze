package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.utils.Utils;

public class Line {
	
	private Location start, end;
	int maxY;
	
	@SuppressWarnings("unused")
	private Vector normal, facing;
	private ArrayList<Location> border;
	
	public Line(Location p1, Location p2) {
		
		start = p1.clone().add(0.5, 0, 0.5);
		end   = p2.clone().add(0.5, 0, 0.5);
		
		if(start.getY() > end.getY())
			end.setY(start.getY());
		else
			start.setY(end.getY());
		
		Vector delta = end.toVector().subtract(start.toVector());
		
		//exception if |delta| = 0 ?
		
		double normalCoefficient = Math.max(delta.getX(), delta.getZ());
		normal = delta.multiply(1 / normalCoefficient);
		
		if(Math.abs(delta.getX()) > Math.abs(delta.getZ()))
			facing = new Vector(Math.signum(delta.getX()), 0, 0);
		else
			facing = new Vector(0, 0, Math.signum(delta.getZ()));

		
		border = new ArrayList<>();
		
		for(int i = 0; i < normalCoefficient; i++) {
			Location iter = start.clone().add(normal.clone().multiply(i));
			
			Location point = iter.getBlock().getLocation();
			
			if(!contains(point))
				border.add(Utils.nearestSurface(point));
		}
	}
	
	public boolean contains(Location point) {
		
		for(Location point2 : border) {
			if(point2.getBlockX() == point.getBlockX() &&
			   point2.getBlockZ() == point.getBlockZ())
				return true;
		}
		return false;
	}
	
	public ArrayList<Location> getBorder() {
		return border;
	}
}