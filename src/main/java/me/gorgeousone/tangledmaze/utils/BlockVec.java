package me.gorgeousone.tangledmaze.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Objects;

public class BlockVec {

	private World world;
	private int x;
	private int y;
	private int z;

	public BlockVec(Block block) {
		this(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public BlockVec(Location location) {
		this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public BlockVec(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public World getWorld() {
		return world;
	}

	public Vec2 toVec2() {
		return new Vec2(x, z);
	}

	public Vector toVector() {return new Vector(getX(), getY(), getZ());
	}
	public Location toLocation() {
		return new Location(world, getX(), getY(), getZ());
	}

	public Block getBlock() {
		return toLocation().getBlock();
	}


	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof BlockVec))
			return false;

		BlockVec that = (BlockVec) o;
		return that.x == x && that.z == z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, z);
	}
}
