package me.tangledmaze.gorgeousone.selections;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Utils;

public class RectSelection {
	
	private Player p;
	private World world;
	private Shape shape;
	
	private ArrayList<Location> vertices;
	private boolean isComplete;
	
	public RectSelection(Block firstVertex, Player p) {
		this.p = p;
		world = firstVertex.getWorld();
		vertices = new ArrayList<>();
		isComplete = false;
		
		vertices.add(firstVertex.getLocation());
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public ArrayList<Location> getVertices() {
		return vertices;
	}

	public Shape getShape() {
		return shape;
	}
	
	public int getWidth() {
		if(!isComplete)
			return 0;
		return vertices.get(2).getBlockX() - vertices.get(0).getBlockX() + 1;
	}
	
	public int getDepth() {
		if(!isComplete)
			return 0;
		return vertices.get(2).getBlockZ() - vertices.get(0).getBlockZ() + 1;
	}
	
	public void setShape(Shape s) {
		this.shape = s;
		this.vertices = s.getVertices();
		isComplete = true;
	}
	
	public boolean frames(Location point) {
		if(isComplete())
			return point.getBlockX() >= vertices.get(0).getX() && point.getBlockX() <= vertices.get(2).getX() &&
				   point.getBlockZ() >= vertices.get(0).getZ() && point.getBlockZ() <= vertices.get(2).getZ();
		else
			return point.getBlockX() == vertices.get(0).getX() && point.getBlockZ() == vertices.get(0).getZ(); 
	}
	
	public boolean isHighlighted(Block b) {
		Chunk c = b.getChunk();

		if(isComplete) {
			if(!shape.getBorder().containsKey(c))
				return false;
			
			for(Location point : shape.getBorder().get(c))
				if(point.getBlock().equals(b))
					return true;
		}
		
		for(Location vertex : vertices)
			if(vertex.getBlock().equals(b))
				return true;
		
		return false;
	}
	
	public boolean isVertex(Block b) {
		if(!isComplete)
			return false;

		for(Location vertex : vertices)
			if(vertex.equals(b.getLocation()))
				return true;
		return false;
	}
	
	public int indexOfVertex(Block b) {
		if(!isComplete || !b.getWorld().equals(world))
			return -1;
		
		for(Location vertex : vertices)
			if(b.getX() == vertex.getX() &&
			   b.getZ() == vertex.getZ())
				return vertices.indexOf(vertex);
		return -1;
	}
	
	public void recalc(Location point) {
		if(!point.getWorld().equals(world))
			return;

		if(isVertex(point.getBlock()))
			vertices.set(indexOfVertex(point.getBlock()), Utils.nearestSurface(point));
			
		if(!isComplete || !shape.contains(point))
			return;
			
		ArrayList<Location>	fill = shape.getFill().get(point.getChunk());
		
		Location newPoint = Utils.nearestSurface(point);
		fill.set(fill.indexOf(point), newPoint);
		
		if(shape.borderContains(point)) {
			ArrayList<Location>	border = shape.getBorder().get(point.getChunk());
			border.set(border.indexOf(point), newPoint);
		}
	}
}