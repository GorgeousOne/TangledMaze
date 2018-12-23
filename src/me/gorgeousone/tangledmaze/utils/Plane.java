package me.gorgeousone.tangledmaze.utils;

import org.bukkit.util.Vector;

public class Plane {

	private Vector origin, normal;
	
	public Plane(Vector origin, Vector u, Vector v) {
		this.origin = origin.clone();
		this.normal = u.getCrossProduct(v);
		
		if(normal.length() == 0)
			throw new IllegalArgumentException("The direction vector(s) cannot be 0.");
	}
	
	public Plane(Vector origin, Vector normal) {
		this.origin = origin.clone();
		this.normal = normal.clone().normalize();
		
		if(normal.length() == 0)
			throw new IllegalArgumentException("The normal vector cannot be 0.");
	}
	
	public Vector getOrigin() {
		return origin.clone();
	}

	public Vector getNormal() {
		return normal.clone();
	}
	
	public boolean contains(Vector point) {
		Vector sub = getOrigin().subtract(point);
		return getNormal().dot(sub) < 0.1;
	}
	
//	public boolean intersects(Line l) {
//		return getIntersection(l) != null;
//	}
	
	//TODO work on it
//	public Vector getIntersection(Line l) {
//		float r = (float) getOrigin().subtract(l.getOrigin()).multiply(getNormal()) / l.getDirection().multiply(getNormal());
//		Vector intersection = l.getPoint(r);
//		
//		return contains(intersection) ? intersection : null;
//	}
}