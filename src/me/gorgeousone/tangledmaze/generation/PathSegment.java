package me.gorgeousone.tangledmaze.generation;

import java.util.ArrayList;

import me.gorgeousone.tangledmaze.util.Directions;
import me.gorgeousone.tangledmaze.util.Vec2;

public class PathSegment {
	
	private Vec2 start;
	private Vec2 end;
	private Vec2 relativeMin;
	private Vec2 size;
	private Directions facing;
	
	public PathSegment(Vec2 start, int length, int width, Directions facing, boolean isExit) {

		//the segment can either face aligned to x or z axis. in positive or negative direction
		this.start = start;
		this.end = start.clone();
		this.facing = facing;
		
		//a vector relative to start, pointing to minimum corner of segment
		relativeMin = new Vec2();
		size = new Vec2();
		
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
		Vec2 max = min.clone().add(size);

		for(int x = min.getIntX(); x < max.getIntX(); x++)
			for(int z = min.getIntZ(); z < max.getIntZ(); z++)
				fill.add(new Vec2(x, z));
		
		return fill;
	}
	
	private void move(int distanceX, int distanceZ) {
		start.add(distanceX, distanceZ);
		end.add(distanceX, distanceZ);
	}
	
	public void expand(int dLength) {
		
		Vec2 expansion = facing.toVec2().clone().mult(dLength);
		
		size.add(expansion.getAbs());
		end.add(expansion);
		
		if(facing.getSign() == -1)
			relativeMin.add(expansion);
	}
	
	private void calculateDimensions(int length, int width, boolean isExit) {
		
		Vec2 distanceStartToEnd = facing.toVec2().clone().mult(length - width);
		end.add(distanceStartToEnd);

		if(facing.isZAligned()) {
			
			size.set(length, width);
			
			if(facing.getSign() == -1) {
				relativeMin = distanceStartToEnd;
				
				if(isExit) {
					move(-width+1, -width+1);
				}
			}
			
		}else {
			
			size.set(width, length);
			
			if(facing.getSign() == -1) {
				relativeMin = distanceStartToEnd;
				
				if(isExit)
					move(0, -width+1);
			
			}else if(isExit)
				move(-width+1, 0);
		}
	}
}