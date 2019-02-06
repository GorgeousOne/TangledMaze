package me.gorgeousone.tangledmaze.util;

import org.bukkit.util.Vector;

public class Line {

	private Vector origin, direction;
	
	public Line(Vector origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
		
		if(direction.length() == 0)
			throw new IllegalArgumentException("The direction vector cannot be 0.");
	}

	public Vector getOrigin() {
		return origin.clone();
	}

	public Vector getDirection() {
		return direction.clone();
	}
	
	public Vector getPoint(float f) {
		return getOrigin().add(multiply(getDirection(), f));
	}
	
	private Vector multiply(Vector v, float f) {
		return new Vector(
				f * v.getX(),
				f * v.getY(),
				f * v.getZ());
	}
}