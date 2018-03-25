package me.tangledmazes.gorgeousone.shapestuff;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmazes.gorgeousone.main.TangledMain_go;
import me.tangledmazes.gorgeousone.selectionstuff.RectSelection;

public class Ellipse implements Shape {
	
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

		calcFillAndBorder();
	}
	
	@Override
	public ArrayList<Location> getBorder() {
		// TODO Auto-generated method stub
		return border;
	}
	
	@Override
	public ArrayList<Location> getFill() {
		return fill;
	}
	
	@Override
	public boolean contains(Location point) {
		return false;
	}
	
	private void calcFillAndBorder() {
			int minX = vertices.get(0).getBlockX(),
				minZ = vertices.get(0).getBlockZ();
			
			Vector relMid = new Vector(0, 0, 0);
			Vector point;
			
			for(double x = -radiusX; x < radiusX; x++)
				for(double z = -radiusZ; z < radiusZ; z++) {
					
					point = new Vector(aspect * (x+0.5), 0, z+0.5);
					Location loc = TangledMain_go.getNearestSurface(new Location(
							world,
							minX + radiusX + x,
							vertices.get(0).getY(),
							minZ + radiusZ + z));
					
					//using radius-0: the circle looks edged
					//using radius-1/2: only one block sticks out at the edges
					// -> radius - 0.25 is the perfect compromise that makes the circle look smooth
					if(relMid.distance(point) <= radiusZ - 0.25)
						fill.add(loc);
					else
						continue;
					
					for(Vector dir : dirs) {
						Vector neighbour = point.clone().add(dir.clone().setX(aspect * dir.getX()));
						
						if(relMid.distance(neighbour) > radiusZ - 0.25) {
							border.add(loc);
							break;
						}
					}
				}
	}
}
