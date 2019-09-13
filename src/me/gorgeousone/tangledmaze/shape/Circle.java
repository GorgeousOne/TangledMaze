package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import org.bukkit.Location;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Circle implements Shape {
	
	private static float circleSmoothing = -0.25f;
	
	@Override
	public int getVertexCount() {
		return 2;
	}

	@Override
	public Clip createClip(ArrayList<Location> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("An ellipse neeeds 2 vertices to be determined.");

		Location vertex0 = vertices.get(0);
		Location vertex1 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(vertex0, vertex1));
		
		Vec2 minVertex = new Vec2(vertices.get(0));
		Vec2 maxVertex = new Vec2(vertices.get(2)).add(1, 1);
		
		Clip clip = new Clip(vertices.get(0).getWorld());
		
		float radiusX = (float) (maxVertex.getX() - minVertex.getX()) / 2;
		float radiusZ = (float) (maxVertex.getZ() - minVertex.getZ()) / 2;
		float distortionZ = 1 / (radiusZ / radiusX);
		
		int maxY = Utils.getMaxHeight(vertices);
		
		for(float x = (float) -radiusX; x <= radiusX; x++) {
			for(float z = (float) -radiusZ; z <= radiusZ; z++) {
				
				if(!isInEllipse(x+0.5f, z+0.5f, distortionZ, radiusX + circleSmoothing))
					continue;
				
				Vec2 loc = minVertex.clone().add((int) (radiusX + x), (int) (radiusZ + z));
				int height = Utils.nearestSurfaceY(loc, maxY, clip.getWorld());
				
				clip.addFill(loc, height);
				
				if(isEllipseBorder(x + 0.5f, z + 0.5f, distortionZ, radiusX + circleSmoothing))
					clip.addBorder(loc);
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
			Vec2 dirVec = dir.getVec2();
			
			if(!isInEllipse(x + dirVec.getX(), z + dirVec.getZ(), distortionZ, radius)) {
				return true;
			}
		}
		
		return false;
	}
}