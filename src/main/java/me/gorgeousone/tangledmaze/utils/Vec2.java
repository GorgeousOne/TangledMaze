package me.gorgeousone.tangledmaze.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Vec2 implements Comparable<Vec2> {
	
	private int x, z;
	
	public Vec2() {
		this.x = 0;
		this.z = 0;
	}
	
	public Vec2(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public Vec2(Location point) {
		this.x = point.getBlockX();
		this.z = point.getBlockZ();
	}
	
	public Vec2(Block block) {
		this.x = block.getX();
		this.z = block.getZ();
	}
	
	public Vec2(String serialized) {
		
		if (!serialized.startsWith("vec2[x:") || !serialized.endsWith("]"))
			throw new IllegalArgumentException();
		
		String[] location = serialized.substring(5, serialized.length() - 1).split(",");
		
		if (location.length != 2)
			throw new IllegalArgumentException();
		
		this.x = Integer.parseInt(location[0].substring(3));
		this.z = Integer.parseInt(location[1].substring(3));
	}
	
	public int getX() {
		return x;
	}
	
	public Vec2 setX(int x) {
		this.x = x;
		return this;
	}
	
	public int getZ() {
		return z;
	}
	
	public Vec2 setZ(int z) {
		this.z = z;
		return this;
	}
	
	public int length() {
		return (int) Math.sqrt(x * x + z * z);
	}
	
	public Vec2 set(int x, int z) {
		this.x = x;
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
	
	public Vector toVec3(double y) {
		return new Vector(x, y, z);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, z);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o)
			return true;
		
		if (!(o instanceof Vec2))
			return false;
		
		Vec2 vec = (Vec2) o;
		return x == vec.x && z == vec.z;
	}
	
	@Override
	public Vec2 clone() {
		return new Vec2(x, z);
	}
	
	@Override
	public String toString() {
		return "vec2[x=" + x + ",z=" + z + "]";
	}
	
	@Override
	public int compareTo(Vec2 vec) {
		int deltaX = Double.compare(getX(), vec.getX());
		return deltaX != 0 ? deltaX : Double.compare(getZ(), vec.getZ());
	}
}