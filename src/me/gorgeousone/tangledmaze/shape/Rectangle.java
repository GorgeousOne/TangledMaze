package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Utils;

public class Rectangle implements Shape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}
	
	@Override
	public Clip createClip(ArrayList<MazePoint> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("A rectangle neeeds 2 vertices to be determined.");
		
		MazePoint v0 = vertices.get(0),
				 v2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(v0, v2));
		
		v0 = vertices.get(0);
		v2 = vertices.get(2);
		
		int maxY = Utils.getMaxY(vertices);
		
		Clip clip = new Clip(v0.getWorld());
		
		for(int x = v0.getBlockX(); x <= v2.getX(); x++) {
			for(int z = v0.getBlockZ(); z <= v2.getZ(); z++) {
				
				MazePoint point = new MazePoint(vertices.get(0).getWorld(), x, maxY, z);
				
				clip.addFill(Utils.nearestSurface(point));

				if(x == v0.getX() || x == v2.getX() ||
				   z == v0.getZ() || z == v2.getZ()) {
					clip.addBorder(Utils.nearestSurface(point));
				}
			}
		}
		
		return clip;
	}
}