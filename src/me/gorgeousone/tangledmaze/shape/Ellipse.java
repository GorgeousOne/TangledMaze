package me.gorgeousone.tangledmaze.shape;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.MazePoint;
import me.gorgeousone.tangledmaze.util.Utils;
import me.gorgeousone.tangledmaze.util.Vec2;

public class Ellipse implements Shape {
	
	@Override
	public int getVertexCount() {
		return 2;
	}
	
	//TODO low - structurize, i cant read that shit
	@Override
	public Clip createClip(ArrayList<MazePoint> vertices) {
		
		if(vertices.size() < 2)
			throw new IllegalArgumentException("2 vertices needed for this calculation.");
		
		MazePoint
			vertex0 = vertices.get(0),
			vertex2 = vertices.get(1);

		vertices.clear();
		vertices.addAll(Shape.createRectangularVertices(vertex0, vertex2));
		
		float
			radiusX = (vertices.get(1).getBlockX() - vertices.get(0).getBlockX() + 1) / 2f,
			radiusZ = (vertices.get(3).getBlockZ() - vertices.get(0).getBlockZ() + 1) / 2f,
			distortion = radiusZ / radiusX;
		
		int minX = vertices.get(0).getBlockX(),
			minZ = vertices.get(0).getBlockZ();
		
		Clip clip = new Clip(vertices.get(0).getWorld());
		//calculate maximum Y to start surface iteration from there? no
		int minY = Utils.getMinHeight(vertices);

		//iterate over the rectangle of the vertices equally to rectangle shape
		for(float x = -radiusX; x < radiusX; x++) {
			for(float z = -radiusZ; z < radiusZ; z++) {
				
				//calculate the iterator compensating the deformation of the ellipses, so the radius behaves like in a circle
				Vec2 iter = new Vec2(distortion * (x + 0.5f), z + 0.5f);
				
				MazePoint point = new MazePoint(
						vertices.get(0).getWorld(),
						minX + radiusX + x,
						minY,
						minZ + radiusZ + z);
				
				//add all blocks that are inside the circle/ellipse, if their iterator is inside the radius
				
				/* using radius: the circle looks edged,
				 * using radius-1/2: single blocks stick out at most smooth parts,
				 * so radius - 0.25 is the perfect compromise that makes the circle look smooth
				 */
				if(pointIsInEllipse(iter, radiusZ - 0.25f)) {
					clip.addFill(Utils.nearestSurface(point));
					
				}else {
					continue;
				}
				
				//check for border by looking for neighbors blocks that aren't in radius distance
				for(Directions dir : Directions.values()) {
					
					Vec2 neighbour = iter.clone().add(dir.toVec2().setX(distortion * dir.toVec2().getIntX()));
					
					if(!pointIsInEllipse(neighbour, radiusZ - 0.25f)) {
						
						clip.addBorder(new MazePoint(Utils.nearestSurface(point)));
						break;
					}
				}
			}
		}
		
		return clip;
	}
	
	private boolean pointIsEllipseBorder(Vec2 point, float radius, float distortion) {
		
		for(Directions dir : Directions.values()) {
			
			Vec2 neighbour = point.clone();
			
			neighbour.add(dir.toVec2());
			neighbour.setX(neighbour.getX()*distortion);
			
			if(!pointIsInEllipse(neighbour, radius - 0.25f)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean pointIsInEllipse(Vec2 point, float radius) {
		return Math.sqrt(point.getX() * point.getX() + point.getZ() * point.getZ()) <= radius;
	}
}