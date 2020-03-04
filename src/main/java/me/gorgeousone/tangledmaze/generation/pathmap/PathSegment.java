package me.gorgeousone.tangledmaze.generation.pathmap;

import me.gorgeousone.tangledmaze.utils.Vec2;

public class PathSegment extends RectSegment {
	
	private Vec2 minimum;
	private Vec2 size;
	
	public PathSegment(Vec2 minimum, Vec2 size) {
		this.minimum = minimum;
		this.size = size;
	}
	
	@Override
	public Vec2 getMinimum() {
		return minimum.clone();
	}
	
	@Override
	public Vec2 getMaximum() {
		return minimum.clone().add(size);
	}
}
