package me.tangledmaze.gorgeousone.shapes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class Rectangle implements Shape {

	private World world;
	private ArrayList<Location> vertices, border, fill;
	
	public Rectangle(RectSelection selection) {
		if(!selection.isComplete())
			throw new IllegalArgumentException("The given selection is incomplete and cannot be used");
		
		world    = selection.getWorld();
		vertices = selection.getVertices();
		border = new ArrayList<>();
		fill   = new ArrayList<>();
		
		calcFillAndBorder();
	}
	
	@Override
	public ArrayList<Location> getBorder() {
		return border;
	}

	@Override
	public ArrayList<Location> getFill() {
		return fill;
	}
	
	@Override
	public boolean contains(Location point) {
		if(!point.getWorld().equals(world))
			return false;
		
		return point.getX() >= vertices.get(0).getX() && point.getZ() <= vertices.get(2).getX() &&
			   point.getZ() >= vertices.get(0).getZ() && point.getZ() <= vertices.get(2).getZ();
	}
	
	@Override
	public boolean borderContains(Location point) {
		return false;	//TODO implement
	}
	
	private void calcFillAndBorder() {
		Vector v0 = vertices.get(0).toVector(),
				   v2 = vertices.get(2).toVector();
		
		for(int x = v0.getBlockX(); x <= v2.getX(); x++)
			for(int z = v0.getBlockZ(); z <= v2.getZ(); z++) {
		
				Location loc = TangledMain_go.getNearestSurface(new Location(world, x, v0.getY(), z));
				fill.add(loc);
				
				if(x == v0.getX() || x == v2.getX() ||
				   z == v0.getZ() || z == v2.getZ())
					border.add(loc.clone());
			}
	}
}