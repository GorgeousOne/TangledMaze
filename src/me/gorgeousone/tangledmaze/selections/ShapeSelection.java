package me.gorgeousone.tangledmaze.selections;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.shapes.Shape;
import me.gorgeousone.tangledmaze.utils.Utils;

public class ShapeSelection extends Selection {
	
	private HashMap<Chunk, ArrayList<Location>> fillChunks, borderChunks;
	private ArrayList<Location> vertices;
	
	private boolean isComplete;
	
	private Shape shape;
	
	public ShapeSelection(Player p, Shape s) {
		super(p);
		
		shape = s;
		
		vertices = new ArrayList<>();
		fillChunks   = new HashMap<>();
		borderChunks = new HashMap<>();	
		isComplete = false;
	}

	@Override
	public void interact(Block b, Action a) {
		
		int vertexCount = vertices.size();
		
		if(vertexCount == 0 || vertexCount < shape.requieredVertices()-1) {
			
			vertices.add(Utils.nearestSurface(b.getLocation()));
		
		}else if(vertexCount == shape.requieredVertices()-1) {
			
			p.sendMessage("hello!");
			vertices.add(Utils.nearestSurface(b.getLocation()));
			shape.calcFillAndBorderAndPerhapsVertices(vertices, fillChunks, borderChunks);
			isComplete = true;
			
			for(Location l : vertices)
				Bukkit.broadcastMessage(l.toVector().toString());
			
			Renderer.showSelection(this);
			
		}else if (vertexCount == shape.requieredVertices()){
			
			fillChunks.clear();
			borderChunks.clear();
			vertices.clear();
			isComplete = false;
			
			shape.calcFillAndBorderAndPerhapsVertices(vertices, fillChunks, borderChunks);
		}
	}
	
	public boolean isComplete() {
		return isComplete;
	}
	
	public HashMap<Chunk, ArrayList<Location>> getFill() {
		return fillChunks;
	}

	public HashMap<Chunk, ArrayList<Location>> getBorder() {
		return borderChunks;
	}
	
	public Shape getShape() {
		return shape;
	}

	public int size() {
		return 0;
	}

	public ArrayList<Location> getVertices() {
		return vertices;
	}
}