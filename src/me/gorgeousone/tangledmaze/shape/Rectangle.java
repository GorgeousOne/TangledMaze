package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.clip.Clip;
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
			throw new IllegalArgumentException("A rectangle needs 2 vertices to be determined.");
		
		MazePoint
			vertex0 = vertices.get(0),
			vertex2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(vertex0, vertex2));
		
		MazePoint
			minVertex = vertices.get(0).clone(),
			maxVertex = vertices.get(2).clone().add(1, 0, 1);
		
		Clip clip = new Clip(vertex0.getWorld());
		
		int maxY = Utils.getMaxHeight(vertices);
		
		for(int x = minVertex.getBlockX(); x < maxVertex.getX(); x++) {
			for(int z = minVertex.getBlockZ(); z < maxVertex.getZ(); z++) {
				
				MazePoint point = new MazePoint(minVertex.getWorld(), x, maxY, z);
				point = Utils.nearestSurface(point);
				
				clip.addFilling(point);
				
				if(isBorder(x, z, minVertex, maxVertex)) {
					clip.addBorder(point);
				}
			}
		}

		return clip;
	}
	
	private boolean isBorder(int x, int z, MazePoint minVertex, MazePoint maxVertex) {
		return
			x == minVertex.getX() || x == maxVertex.getX() - 1 ||
			z == minVertex.getZ() || z == maxVertex.getZ() - 1;
	}
}