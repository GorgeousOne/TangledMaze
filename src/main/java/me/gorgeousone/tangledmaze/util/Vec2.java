package me.gorgeousone.tangledmaze.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class Vec2 implements Comparable<Vec2>, ConfigurationSerializable {
	
	private int x, z;
	
	public Vec2() {
		this.x = 0;
		this.z = 0;
	}
	
	public Vec2(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Vec2(Location loc) {
		this.x = loc.getBlockX();
		this.z = loc.getBlockZ();
	}
	
	public Vec2(Block block) {
		this.x = block.getX();
		this.z = block.getZ();
	}
	
	public Vec2(String serialized) {
		
		if(!serialized.startsWith("vec2[x:") || !serialized.endsWith("]"))
			throw new IllegalArgumentException();
		
		String[] location = serialized.substring(5, serialized.length()-1).split(",");
		
		if(location.length != 2)
			throw new IllegalArgumentException();

		this.x = Integer.parseInt(location[0].substring(3));
		this.z = Integer.parseInt(location[1].substring(3));
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public int length() {
		return (int) Math.sqrt(x*x + z*z);
	}
	
	public boolean isZero() {
		return x == 0 && z == 0;
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
		return "vec2[x:" + x + ",z:" + z + "]";
	}
	
	@Override
	public Vec2 clone() {
		return new Vec2(x, z);
	}
	
	@Override
	public int hashCode() {
		
		int hash = 3;
		
	    hash = 19 * hash + (getX() ^ getX() >>> 32);
	    hash = 19 * hash + (getZ() ^ getZ() >>> 32);
	    
	    return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null || obj.getClass() != this.getClass())
			return false;
		
		Vec2 otherVec = (Vec2) obj;
		
		return otherVec.getX() == getX() && otherVec.getZ() == getZ();
	}
	
	@Override
	public int compareTo(Vec2 vec) {

		int deltaX = Double.compare(getX(), vec.getX());
		return deltaX != 0 ? deltaX : Double.compare(getZ(), vec.getZ());
	}
	
	@Override
	public Map<String, Object> serialize() {
		
		Map<String, Object> data = new HashMap<>();
		
		data.put("x", Integer.valueOf(x));
	    data.put("z", Integer.valueOf(z));
	    
		return data;
	}
	
	public static Vec2 desrialize(Map<String, Object> data) {
		
		return new Vec2(
				Integer.parseInt(data.get("x").toString()),
				Integer.parseInt(data.get("z").toString()));
	}
}