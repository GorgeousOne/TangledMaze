package me.gorgeousone.tangledmaze.generation.pathmap;

import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.HashSet;
import java.util.Set;

public abstract class RectSegment {
	
	public Set<Vec2> getFill() {
		
		Set<Vec2> fill = new HashSet<>();
		Vec2 minimum = getMinimum();
		Vec2 maximum = getMaximum();
		
		for (int x = minimum.getX(); x < maximum.getX(); x++) {
			for (int z = minimum.getZ(); z < maximum.getZ(); z++)
				fill.add(new Vec2(x, z));
		}
		
		return fill;
	}
	
	public abstract Vec2 getMinimum();
	
	public abstract Vec2 getMaximum();
}
