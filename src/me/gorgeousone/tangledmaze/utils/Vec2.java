package me.gorgeousone.tangledmaze.utils;

import org.bukkit.util.Vector;

public class Vec2 {
	
	private int x, z;
	
	public Vec2() {
		this.x = 0;
		this.z = 0;
	}
	
	public Vec2(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Vec2(Vector vec3) {
		this.x = vec3.getBlockX();
		this.z = vec3.getBlockZ();
	}

	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public Vec2 set(int x, int z) {
		this.x = x;
		this.z = z;
		return this;
	}
	
	public Vec2 setX(int x) {
		this.x = x;
		return this;
	}
	
	public Vec2 setZ(int z) {
		this.z = z;
		return this;
	}
	
	public Vec2 add(int dx, int dz) {
		x += dx;
		z += dz;
		return this;
	}
	
	public Vec2 add(Vec2 vec2) {
		x += vec2.x;
		z += vec2.z;
		return this;
	}
	
	public Vec2 sub(Vec2 vec2) {
		x -= vec2.x;
		z -= vec2.z;
		return this;
	}
	
	public Vec2 mult(int i) {
		x *= i;
		z *= i;
		return this;
	}
	
	public Vec2 getAbs() {
		return new Vec2(Math.abs(x), Math.abs(z));
	}
	
	public Vector toVec3() {
		return new Vector(x, 0, z);
	}
	
	@Override
	public String toString() {
		return "vec2[x:" + x + ", z:" + z + "]";
	}
	
	@Override
	public Vec2 clone() {
		return new Vec2(x, z);
	}
}