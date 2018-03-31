package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.Utils;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class Ellipse {
	
	private ArrayList<Vector> dirs = new ArrayList<>(Arrays.asList(
			new Vector( 1, 0,  0),
			new Vector( 1, 0,  1),
			new Vector( 0, 0,  1),
			new Vector(-1, 0,  1),
			new Vector(-1, 0,  0),
			new Vector(-1, 0, -1),
			new Vector( 0, 0, -1),
			new Vector( 1, 0, -1)));
	
	private World world;
	private ArrayList<Location> vertices, border, fill;
	private Vector mid;
	private double radiusX, radiusZ, aspect;
	
	public Ellipse(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");

		world    = selection.getWorld();
		vertices = selection.getVertices();
		border = new ArrayList<>();
		fill   = new ArrayList<>();
		
		radiusX = selection.getWidth() / 2d;
		radiusZ = selection.getDepth() / 2d;
		aspect = 1d * radiusZ / radiusX;
		
		mid = new Vector((vertices.get(0).getX() + vertices.get(2).getX()) / 2, 0,
						 (vertices.get(0).getZ() + vertices.get(2).getZ()) / 2);

		calcFillAndBorder();
	}
	
	public ArrayList<Location> getBorder() {
		return border;
	}
	
	public ArrayList<Location> getFill() {
		return fill;
	}
	
	public boolean contains(Location point) {
		Vector point2 = point.toVector();
		point2.setX((point2.getX() - mid.getX()) * aspect + mid.getX());
		point2.setY(0);
		
		return mid.distance(point2) <= radiusZ - 0.25;
	}
	
	public boolean borderContains(Location point) {
		Vector point2 = point.toVector();
		point2.setX((point2.getX() - mid.getX()) * aspect + mid.getX());
		point2.setY(0);
		
		if(mid.distance(point2) > radiusZ - 0.25)
			return false;
		
		for(Vector dir : dirs) {
			Vector neighbour = point2.clone().add(dir.clone().setX(aspect * dir.getX()));
			
			if(mid.distance(neighbour) > radiusZ - 0.25)
				return true;
		}
		
		return false;
	}
	
	
	private void calcFillAndBorder() {
		int posX = vertices.get(0).getBlockX(),
			posZ = vertices.get(0).getBlockZ();
		
		Vector midPoint = new Vector(0, 0, 0);
		Vector point;
		
		for(double x = -radiusX; x < radiusX; x++)
			for(double z = -radiusZ; z < radiusZ; z++) {
				
				point = new Vector(aspect * (x+0.5), 0, z+0.5);
				Location loc = Utils.getNearestSurface(new Location(
						world,
						posX + radiusX + x,
						vertices.get(0).getY(),
						posZ + radiusZ + z));
				
				//using radius-0: the circle looks edged
				//using radius-1/2: only one block sticks out at the edges
				// -> radius - 0.25 is the perfect compromise that makes the circle look smooth
				if(midPoint.distance(point) <= radiusZ - 0.25)
					fill.add(loc);
				else
					continue;
				
				for(Vector dir : dirs) {
					Vector neighbour = point.clone().add(dir.clone().setX(aspect * dir.getX()));
					
					if(midPoint.distance(neighbour) > radiusZ - 0.25) {
						border.add(loc);
						break;
					}
				}
			}
	}
}