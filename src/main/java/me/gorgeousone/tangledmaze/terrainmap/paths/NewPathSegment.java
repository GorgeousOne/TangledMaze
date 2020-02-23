package me.gorgeousone.tangledmaze.terrainmap.paths;

import me.gorgeousone.tangledmaze.utils.Vec2;

import java.util.HashSet;
import java.util.Set;

public class NewPathSegment {
	
	private Vec2 start;
	private Vec2 size;
	
	public NewPathSegment(Vec2 start, Vec2 size) {
		this.start = start;
		this.size = size;
	}
	
	public void add(int dx, int dz) {
		size.add(dx, dz);
	}
	
	public void translate(int dx, int dz) {
		start.add(dx, dz);
	}
	
	public Set<Vec2> getFill() {
		
		Set<Vec2> fill = new HashSet<>();
		Vec2 end = start.clone().add(size);
		
		for (int x = start.getX(); x < end.getX(); x++) {
			for (int z = start.getZ(); z < end.getZ(); z++) {
				fill.add(new Vec2(x, z));
			}
		}
		
		return fill;
	}
}
