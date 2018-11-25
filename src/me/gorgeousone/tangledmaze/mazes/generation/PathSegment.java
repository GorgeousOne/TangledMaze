package me.gorgeousone.tangledmaze.mazes.generation;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.utils.Vec2;

public class PathSegment {
	
	public static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		EXIT = 4;
	
	private Vec2 start, end, facing, relativeMin, dimension;
	
	public PathSegment(Vec2 start, Vec2 facing, int length, int width, boolean isExit) {

		//the segment can either face aligned to x or z axis. in positive or negative direction
		this.start = start;
		this.end = start.clone();
		this.facing = facing;
		
		//a vector relative to start, pointing to minimum corner of segment
		relativeMin = new Vec2();
		dimension = new Vec2();
		
		calculateDimensions(length, width, isExit);
	}
	
	public Vec2 getStart() {
		return start.clone();
	}
	
	public Vec2 getEnd() {
		return end.clone();
	}
	
	public ArrayList<Vec2> getFill() {
		
		ArrayList<Vec2> fill = new ArrayList<>();
		
		Vec2 min = start.clone().add(relativeMin);
		Vec2 max = min.clone().add(dimension);

		for(int x = min.getX(); x < max.getX(); x++)
			for(int z = min.getZ(); z < max.getZ(); z++)
				fill.add(new Vec2(x, z));
		
		return fill;
	}
	
	private void move(int dx, int dz) {
		start.add(dx, dz);
		end.add(dx, dz);
	}
	
	public void expand(int dLength) {
		
		Vec2 expansion = facing.clone().mult(dLength);
		
		dimension.add(expansion.getAbs());
		end.add(expansion);

		if(facing.getX() == -1 ||
		   facing.getZ() == -1) {
			
			relativeMin.add(expansion);
		}
	}
	
	private void calculateDimensions(int length, int width, boolean isExit) {
		
		Vec2 dStartToEnd = facing.clone().mult(length - width);
		end.add(dStartToEnd);

		if(facing.getX() != 0) {
			dimension.set(length, width);
			
			if(facing.getX() == -1) {
				relativeMin = dStartToEnd;
				
				if(isExit) {
					move(-width+1, -width+1);
				}
			}
			
		}else {
			dimension.set(width, length);
			
			if(facing.getZ() == -1) {
				relativeMin = dStartToEnd;
				
				if(isExit)
					move(0, -width+1);
			
			}else if(isExit)
				move(-width+1, 0);
		}
	}
}