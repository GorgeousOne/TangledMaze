package me.tangledmazes.gorgeousone.shapestuff;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmazes.gorgeousone.main.TangledMain_go;
import me.tangledmazes.gorgeousone.selectionstuff.RectSelection;

public class Ellipse implements Shape {
	
	private World world;
	private ArrayList<Location> vertices, border, fill;
	private double aspect;
	
	public Ellipse(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");

		world    = selection.getWorld();
		vertices = selection.getVertices();
		border = new ArrayList<>();
		fill   = new ArrayList<>();
		
		int width = selection.getWidth(),
			depth = selection.getDepth();
		
		aspect = 1d * depth / width;
		
		int minX = vertices.get(0).getBlockX(),
			minZ = vertices.get(0).getBlockZ();
		
		Vector relMid = new Vector(width/2d, 0, depth/2d);
		Vector relPoint;
		double adjacent, angle, borderZ;
		
		for(int x = 0; x < width; x++) {
		
			adjacent = Math.abs(x+0.5 - relMid.getX());
			angle    = Math.acos(adjacent / (width/2d));
			borderZ  = Math.sin(angle) * depth/2d;
			
			System.out.println("border: " + borderZ);
			
			for(int z = 0; z < depth; z++) {
				relPoint = new Vector(aspect * (x+0.5), 0, z+0.5);
				
				if(relMid.distance(relPoint) <= depth/2d) {
					Location point = TangledMain_go.getNearestSurface(new Location(world, minX+x, vertices.get(0).getY(), minZ+z));
					
					fill.add(point.clone());
					System.out.println((int) Math.abs(relPoint.getZ() - width/2d));

					if(Math.abs(borderZ - Math.abs(relPoint.getZ() - width/2d)) < 3) {
						border.add(point.clone());
						System.out.println("added");
					}
				}
			}
		}
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
	
	
}
