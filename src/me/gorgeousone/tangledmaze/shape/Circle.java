package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Circle implements Shape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}

	@Override
	public Clip createClip(ArrayList<MazePoint> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("An ellipse neeeds 2 vertices to be determined.");

		MazePoint
			vertex0 = vertices.get(0),
			vertex1 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(vertex0, vertex1));
		
		MazePoint
			minVertex = vertices.get(0).clone(),
			maxVertex = vertices.get(2).clone().add(1, 0, 1);
		
		Clip clip = new Clip(minVertex.getWorld());
		
		float
			radiusX = (float) (maxVertex.getX() - minVertex.getX()) / 2,
			radiusZ = (float) (maxVertex.getZ() - minVertex.getZ()) / 2,
			distortionZ = 1 / (radiusZ / radiusX);
		
		int maxY = Utils.getMaxHeight(vertices);
		
		for(float x = (float) -radiusX; x <= radiusX; x++) {
			for(float z = (float) -radiusZ; z <= radiusZ; z++) {
				
				if(!isInEllipse(x+0.5f, z+0.5f, distortionZ, radiusX - 0.25f)) {
					continue;
				}
				
				MazePoint point = minVertex.clone().add(radiusX + x, 0, radiusZ + z);
				point.setY(maxY);
				point = Utils.nearestSurface(point);
				
				clip.addFilling(point);
				
				if(isEllipseBorder(x+0.5f, z+0.5f, distortionZ, radiusX - 0.25f)) {
					clip.addBorder(point);
				}
			}
		}
		
		return clip;
	}
	
	private static boolean isInEllipse(float x, float z, float distortionZ, float radius) {
		
		float circleZ = z * distortionZ;
		return Math.sqrt(x*x + circleZ*circleZ) <= radius;
	}
	
	private static boolean isEllipseBorder(float x, float z, float distortionZ, float radius) {
		
		for(Directions dir : Directions.values()) {
			Vec2 dirVec = dir.toVec2();
			
			if(!isInEllipse(x + dirVec.getX(), z + dirVec.getZ(), distortionZ, radius)) {
				return true;
			}
		}
		
		return false;
	}
}