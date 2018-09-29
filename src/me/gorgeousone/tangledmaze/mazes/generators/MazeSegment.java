package me.gorgeousone.tangledmaze.mazes.generators;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import me.gorgeousone.tangledmaze.utils.Vec2;

public class MazeSegment {
	
	public static final int
		UNDEFINED = 1,
		WALL = 2,
		PATH = 3,
		EXIT = 4;
	
//	private Vector relativeMin, start, end;
	private Vec2 start;

	Vec2 relativeMin;

	private Vec2 end;

	private Vec2 facing;

	Vec2 dimension;
	
	public MazeSegment(Vec2 start, Vec2 facing, int length, int width, boolean isExit) {

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
			
			Bukkit.broadcastMessage("  expanding: " + relativeMin.toString() + " with " + expansion.toString());
			relativeMin.add(expansion);
			Bukkit.broadcastMessage("  result: " + relativeMin.toString());
		}
	}
	
	private void calculateDimensions(int length, int width, boolean isExit) {
		
		Vec2 dStartToEnd = facing.clone().mult(length - width);
		end.add(dStartToEnd);

		if(facing.getX() != 0) {
			dimension.set(length, width);
			
			if(facing.getX() == -1) {
				relativeMin = dStartToEnd;
				
				if(isExit)
					move(-width+1, -width+1);
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
		
//		if(facing.getX() != 0) {
//			
//			dimension.set(length, width);
//			
//			//move the end into the facing direction
//			//this should not be confused with a maximum or minimum. the end is followed by a width-sized square ending the segment
//			end.add(facing.getX() * (length - width), 0);
//			
//			if(facing.getX() == -1) {
//				relativeMin.set(-length + width, 0);
//
//				if(isExit)
//					move(-width+1, -width+1);
//			}
//			
//		}else {
//			
//			dimension.set(width, length);
//			end.add(0, facing.getZ() * (length - width));
//			
//			if(facing.getZ() == -1) {
//				relativeMin.set(0, -length + width);
//				
//				if(isExit)
//					move(0, -width+1);
//			
//			}else if(isExit)
//				move(-width+1, 0);
//		}
	}
}