package me.gorgeousone.tangledmaze.generation;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class LocatedBlockData {
	
	private Location location;
	private BlockData data;
	
	public LocatedBlockData(Location blockLoc) {
		this(blockLoc.getBlock());
	}
	
	public LocatedBlockData(Block block) {
		this.location = block.getLocation();
		this.data = block.getBlockData();
	}
	
	protected LocatedBlockData(Location location, BlockData data) {
		this.location = location;
		this.data = data;
	}
	
	public Block getBlock() {
		return location.getBlock();
	}
	
	public Location getLoc() {
		return location.clone();
	}
	
	public BlockData getData() {
		return data.clone();
	}
	
	@Override
	public LocatedBlockData clone() {
		return new LocatedBlockData(getLoc(), getData());
	}
}
