package me.gorgeousone.tangledmaze.clip.shape;

import java.util.ArrayList;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Rectangle extends ClipShape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}
	
	@Override
	public Clip createClip(ArrayList<Location> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("A rectangle needs 2 vertices to be determined.");
		
		Location vertex0 = vertices.get(0);
		Location vertex2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(ClipShape.createRectangularVertices(vertex0, vertex2));
		
		Vec2 minVertex = new Vec2(vertices.get(0));
		Vec2 maxVertex = new Vec2(vertices.get(2)).add(1, 1);
		Clip clip = new Clip(vertex0.getWorld());
		
		int maxY = Utils.getMaxHeight(vertices);
		
		for(int x = minVertex.getX(); x < maxVertex.getX(); x++) {
			for(int z = minVertex.getZ(); z < maxVertex.getZ(); z++) {
				
				Vec2 loc = new Vec2(x, z);
				int height = Utils.nearestSurfaceY(loc, maxY, clip.getWorld());
				
				clip.addFill(loc, height);

				if(isBorder(x, z, minVertex, maxVertex))
					clip.addBorder(loc);
			}
		}
		
		return clip;
	}
	
	private boolean isBorder(int x, int z, Vec2 minVertex, Vec2 maxVertex) {
		return
			x == minVertex.getX() || x == maxVertex.getX() - 1 ||
			z == minVertex.getZ() || z == maxVertex.getZ() - 1;
	}
}