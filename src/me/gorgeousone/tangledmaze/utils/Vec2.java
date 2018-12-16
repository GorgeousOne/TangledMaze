package me.gorgeousone.tangledmaze.utils;

import org.bukkit.util.Vector;

public class Vec2 {
	
	private float x, z;
	
	public Vec2() {
		this.x = 0;
		this.z = 0;
	}
	
	public Vec2(float x, float z) {
		this.x = x;
		this.z = z;
	}

	public Vec2(Vector vec3) {
		this.x = vec3.getBlockX();
		this.z = vec3.getBlockZ();
	}
	
	public float getX() {
		return x;
	}
	
	public float getZ() {
		return z;
	}
	
	public int getIntX() {
		return (int) x;
	}
	
	public int getIntZ() {
		return (int) z;
	}
	
	public float length() {
		return (float) Math.sqrt(x*x + z*z);
	}
	
	public Vec2 set(float x, float z) {
		this.x = x;
		this.z = z;
		return this;
	}
	
	public Vec2 setX(float x) {
		this.x = x;
		return this;
	}
	
	public Vec2 setZ(float z) {
		this.z = z;
		return this;
	}
	
	public Vec2 add(float dx, float dz) {
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
	
	public Vec2 mult(float i) {
		x *= i;
		z *= i;
		return this;
	}
	
	public Vec2 cross(Vec2 vec2) {
		x *= vec2.z;
		z *= vec2.x;
		return this;
	}
	
	public Vec2 normalize() {
		mult(1f / length());
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