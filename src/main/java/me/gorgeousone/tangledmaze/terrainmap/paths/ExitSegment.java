package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.utils.Direction;
import me.gorgeousone.tangledmaze.utils.Vec2;

public class ExitSegment extends RectSegment {
	
	private Vec2 startPoint;
	private Vec2 endPoint;
	private Direction facing;
	
	private int length;
	private int width;
	
	public ExitSegment(Vec2 startPoint, Direction facing, int pathWidth) {
		
		this.startPoint = startPoint.clone();
		this.endPoint = startPoint.clone();
		this.facing = facing;
		
		this.length = pathWidth;
		this.width = pathWidth;
	}
	
	@Override
	public Vec2 getMinimum() {
		return facing.isPositive() ? startPoint.clone() : endPoint.clone();
	}
	
	@Override
	public Vec2 getMaximum() {
		Vec2 maximum = facing.isPositive() ? endPoint.clone() : startPoint.clone();
		return maximum.add(width, width);
	}
	
	public Vec2 getStartPoint() {
		return startPoint.clone();
	}
	
	public Vec2 getEndPoint() {
		return endPoint.clone();
	}
	
	public void expandLength(int dLength) {
		
		if(length + dLength < width)
			throw new IllegalArgumentException("Cannot (don't want to) make exit segment shorter than wide.");
		
		endPoint.add(facing.getVec2().mult(dLength));
	}
}