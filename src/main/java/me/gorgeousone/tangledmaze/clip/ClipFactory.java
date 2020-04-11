package me.gorgeousone.tangledmaze.clip;

import me.gorgeousone.tangledmaze.utils.BlockUtils;
import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ClipFactory {
	
	private static final double circleSmoothing = -0.25f;
	
	private ClipFactory() {}
	
	public static List<Location> createCompleteVertexList(List<Location> definingVertices, ClipShape shape) {
		
		if (definingVertices.size() < shape.getRequiredVertexCount())
			throw new IllegalArgumentException("Not enough vertices given to create a clip.");
		
		switch (shape) {
			
			case RECTANGLE:
			case ELLIPSE:
				
				int highestY = Math.max(definingVertices.get(0).getBlockY(), definingVertices.get(1).getBlockY());
				definingVertices.get(0).setY(highestY);
				definingVertices.get(1).setY(highestY);
				
				Location point1 = BlockUtils.nearestSurface(definingVertices.get(0)).getLocation();
				Location point3 = BlockUtils.nearestSurface(definingVertices.get(1)).getLocation();
				Location point2 = BlockUtils.nearestSurface(new Location(point1.getWorld(), point1.getX(), highestY, point3.getZ())).getLocation();
				Location point4 = BlockUtils.nearestSurface(new Location(point1.getWorld(), point3.getX(), highestY, point1.getZ())).getLocation();
				
				return new ArrayList<>(Arrays.asList(
						point1,
						point2,
						point3,
						point4));
			
			default:
				return definingVertices;
		}
	}
	
	public static Clip createClip(ClipShape shape, List<Location> definingVertices) {
		
		if (definingVertices.size() < shape.getRequiredVertexCount())
			throw new IllegalArgumentException("Not enough vertices given to create a clip with shape " + shape.name());
		
		World clipWorld = definingVertices.get(0).getWorld();
		Map.Entry<Vec2, Vec2> rectangularBounds = getRectangularBounds(definingVertices.get(0), definingVertices.get(1));
		int highestY = Math.max(definingVertices.get(0).getBlockY(), definingVertices.get(1).getBlockY());
		
		switch (shape) {
			
			case RECTANGLE:
				return createRectangleClip(clipWorld, rectangularBounds.getKey(), rectangularBounds.getValue(), highestY);
			
			case ELLIPSE:
				return createEllipseClip(clipWorld, rectangularBounds.getKey(), rectangularBounds.getValue(), highestY);
			
			default:
				return null;
		}
	}
	
	private static Clip createRectangleClip(World world, Vec2 minVertex, Vec2 maxVertex, int highestY) {
		
		Clip clip = new Clip(world);
		
		for (int x = minVertex.getX(); x <= maxVertex.getX(); x++) {
			for (int z = minVertex.getZ(); z <= maxVertex.getZ(); z++) {
				
				Vec2 point = new Vec2(x, z);
				int height = BlockUtils.nearestSurfaceY(point, highestY, clip.getWorld());
				
				clip.addFill(point, height);
				
				if (rectangleBorderContains(x, z, minVertex, maxVertex))
					clip.addBorder(point);
			}
		}
		
		return clip;
	}
	
	private static boolean rectangleBorderContains(int x, int z, Vec2 rectMin, Vec2 rectMax) {
		return x == rectMin.getX() || x == rectMax.getX() ||
		       z == rectMin.getZ() || z == rectMax.getZ();
	}
	
	private static Clip createEllipseClip(World world, Vec2 minVertex, Vec2 maxVertex, int highestY) {
		
		Clip clip = new Clip(world);
		
		double radiusX = (maxVertex.getX() - minVertex.getX() + 1) / 2d;
		double radiusZ = (maxVertex.getZ() - minVertex.getZ() + 1) / 2d;
		double distortionZ = radiusX / radiusZ;
		
		for (double relX = -radiusX; relX <= radiusX; relX++) {
			for (double relZ = -radiusZ; relZ <= radiusZ; relZ++) {
				
				if (!ellipseContains(relX + 0.5f, relZ + 0.5f, distortionZ, radiusX + circleSmoothing))
					continue;
				
				Vec2 point = minVertex.clone().add((int) (radiusX + relX), (int) (radiusZ + relZ));
				int height = BlockUtils.nearestSurfaceY(point, highestY, clip.getWorld());
				
				clip.addFill(point, height);
				
				if (ellipseBorderContains(relX + 0.5f, relZ + 0.5f, distortionZ, radiusX + circleSmoothing))
					clip.addBorder(point);
			}
		}
		
		return clip;
	}
	
	private static boolean ellipseContains(double x, double z, double distortionZ, double radius) {
		
		double circleZ = z * distortionZ;
		return x * x + circleZ * circleZ <= radius * radius;
	}
	
	private static boolean ellipseBorderContains(double x, double z, double distortionZ, double radius) {
		
		for (Direction dir : Direction.values()) {
			
			Vec2 dirVec = dir.getVec2();
			
			if (!ellipseContains(x + dirVec.getX(), z + dirVec.getZ(), distortionZ, radius))
				return true;
		}
		
		return false;
	}
	
	private static Map.Entry<Vec2, Vec2> getRectangularBounds(Location vertex1, Location vertex2) {
		
		int x1 = vertex1.getBlockX();
		int x2 = vertex2.getBlockX();
		int z1 = vertex1.getBlockZ();
		int z2 = vertex2.getBlockZ();
		
		Vec2 min = new Vec2(Math.min(x1, x2), Math.min(z1, z2));
		Vec2 max = new Vec2(Math.max(x1, x2), Math.max(z1, z2));
		
		return new AbstractMap.SimpleEntry<>(min, max);
	}
}
