package me.gorgeousone.tangledmaze.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataState {

	private Location blockLoc;
	private BlockData blockData;

	public BlockDataState(Location blockLoc) {
		this(blockLoc.getBlock());
	}

	public BlockDataState(Block block) {
		this.blockLoc = block.getLocation();
		this.blockData = block.getBlockData();
	}

	protected BlockDataState(Location blockLoc, BlockData blockData) {
		this.blockLoc = blockLoc;
		this.blockData = blockData;
	}

	public Block getBlock() {
		return blockLoc.getBlock();
	}

	public Location getLoc() {
		return blockLoc.clone();
	}

	public BlockData getData() {
		return blockData.clone();
	}

	public void updateBlock(boolean withPhysics) {
		blockLoc.getBlock().setBlockData(blockData);
	}

	@Override
	public BlockDataState clone() {
		return new BlockDataState(getLoc(), getData());
	}
}
