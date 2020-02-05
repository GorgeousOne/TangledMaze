package me.gorgeousone.tangledmaze.clip;

import me.gorgeousone.tangledmaze.util.BlockUtils;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ClipFactory {

	private ClipFactory() {}

	public static List<Location> createCompleteControlPointsList(List<Location> definingVertices, ClipShape shape)  {

		if(definingVertices.size() < shape.getRequiredControlPointCount())
			throw new IllegalArgumentException("Not enough vertices given to create a clip.");

		switch (shape) {

			case RECTANGLE:
			case ELLIPSE:

				int highestY = Math.max(definingVertices.get(0).getBlockY(), definingVertices.get(1).getBlockY());

				Location point1 = BlockUtils.nearestSurface(definingVertices.get(0));
				Location point3 = BlockUtils.nearestSurface(definingVertices.get(1));
				Location point2 = BlockUtils.nearestSurface(new Location(point1.getWorld(), point1.getX(), highestY, point3.getZ()));
				Location point4 = BlockUtils.nearestSurface(new Location(point1.getWorld(), point3.getX(), highestY, point1.getZ()));

				return new ArrayList<>(Arrays.asList(
						point1,
						point2,
						point3,
						point4));

			default:
				return definingVertices;
		}
	}

	public static Clip createClip(ClipShape shape, List<Location> definingControlPoints) {

		if(definingControlPoints.size() < shape.getRequiredControlPointCount())
			throw new IllegalArgumentException("Not enough vertices given to create a clip.");

		World clipWorld = definingControlPoints.get(0).getWorld();
		Map.Entry<Vec2, Vec2> rectangularBounds = getRectangularBounds(definingControlPoints.get(0), definingControlPoints.get(1));
		int highestY = Math.max(definingControlPoints.get(0).getBlockY(), definingControlPoints.get(1).getBlockY());

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

		for(int x = minVertex.getX(); x <= maxVertex.getX(); x++) {
			for(int z = minVertex.getZ(); z <= maxVertex.getZ(); z++) {

				Vec2 point = new Vec2(x, z);
				int height = BlockUtils.nearestSurfaceY(point, highestY, clip.getWorld());

				clip.addFill(point, height);

				if(isRectangleBorder(x, z, minVertex, maxVertex))
					clip.addBorder(point);
			}
		}

		return clip;
	}

	private static boolean isRectangleBorder(int x, int z, Vec2 rectMin, Vec2 rectMax) {
		return x == rectMin.getX() || x == rectMax.getX() ||
				z == rectMin.getZ() || z == rectMax.getZ();
	}

	private static Clip createEllipseClip(World world, Vec2 minVertex, Vec2 maxVertex, int highestY) {

		Clip clip = new Clip(world);

		double radiusX = (maxVertex.getX() - minVertex.getX() + 1) / 2d;
		double radiusZ = (maxVertex.getZ() - minVertex.getZ() + 1) / 2d;
		double distortionZ = radiusX / radiusZ;

		for(double x = -radiusX; x <= radiusX; x++) {
			for(double z = -radiusZ; z <= radiusZ; z++) {

				double circleSmoothing = -0.25f;
				if(!isInEllipse(x+0.5f, z+0.5f, distortionZ, radiusX + circleSmoothing))
					continue;

				Vec2 loc = minVertex.clone().add((int) (radiusX + x), (int) (radiusZ + z));
				int height = BlockUtils.nearestSurfaceY(loc, highestY, clip.getWorld());

				clip.addFill(loc, height);

				if(isEllipseBorder(x + 0.5f, z + 0.5f, distortionZ, radiusX + circleSmoothing))
					clip.addBorder(loc);
			}
		}

		return clip;
	}

	private static boolean isInEllipse(double x, double z, double distortionZ, double radius) {

		double circleZ = z * distortionZ;
		return Math.sqrt(x*x + circleZ*circleZ) <= radius;
	}

	private static boolean isEllipseBorder(double x, double z, double distortionZ, double radius) {

		for(Directions dir : Directions.values()) {
			Vec2 dirVec = dir.getVec2();

			if(!isInEllipse(x + dirVec.getX(), z + dirVec.getZ(), distortionZ, radius)) {
				return true;
			}
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
