package me.gorgeousone.tangledmaze.shapes;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.tools.Clip;
import me.gorgeousone.tangledmaze.utils.Directions;
import me.gorgeousone.tangledmaze.utils.MazePoint;
import me.gorgeousone.tangledmaze.utils.Utils;

public class Ellipse implements Shape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}
	
	@Override
	public Clip createClip(ArrayList<MazePoint> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("2 vertices needed for this calculation.");
		
		MazePoint v0 = vertices.get(0),
				 v2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(v0, v2));
		
		double
			radiusX = (vertices.get(1).getX() - vertices.get(0).getX() + 1) / 2,
			radiusZ = (vertices.get(3).getZ() - vertices.get(0).getZ() + 1) / 2,
			proportion = 1d * radiusZ / radiusX;
		
		int posX = vertices.get(0).getBlockX(),
			posZ = vertices.get(0).getBlockZ();
		
		//calculate maximum Y to start surface iteration from there?
		int maxY = Utils.getMaxY(vertices);
		
		Vector midPoint = new Vector(0, 0, 0);
		Vector iter;
		
		Clip clip = new Clip(vertices.get(0).getWorld());
		
		//iterate over the rectangle of the vertices equally to rectangle shape
		for(double x = -radiusX; x < radiusX; x++) {
			for(double z = -radiusZ; z < radiusZ; z++) {
				
				//calculate the iterator compensating the deformation of the ellipses, so the radius behaves like in a circle
				iter = new Vector(proportion * (x + 0.5), 0, z + 0.5);
				MazePoint point = new MazePoint(vertices.get(0).getWorld(), posX + radiusX + x, maxY, posZ + radiusZ + z);
				
				//add all blocks that are inside the circle/ellipse, if their iterator is inside the radius

				/* using radius: the circle looks edged,
				 * using radius-1/2: single blocks stick out at most smooth parts,
				 * so radius - 0.25 is the perfect compromise that makes the circle look smooth */
				if(midPoint.distance(iter) <= radiusZ - 0.25)
					clip.addFill(Utils.nearestSurface(point));
				else
					continue;
				
				//check for border by looking for neighbors blocks that aren't in radius distance
				for(Directions dir : Directions.values()) {
					Vector neighbour = iter.clone().add(dir.facing3d().setX(proportion * dir.toVec2().getIntX()));
					
					if(midPoint.distance(neighbour) > radiusZ - 0.25) {
						clip.addBorder(new MazePoint(Utils.nearestSurface(point)));
						break;
					}
				}
			}
		}
		
		return clip;
	}
}