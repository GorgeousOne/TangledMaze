package me.gorgeousone.tangledmaze.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class BlockTypeWrapper {

	private Material material;
	private BlockFace facing;
	private BlockFace half;

	public BlockTypeWrapper(Material material) {
		this(material, null, null);
	}

	public BlockTypeWrapper(Material material, BlockFace facing, BlockFace half) {
		this.material = material;
		this.facing = facing;
		this.half = half;
	}

	public void setFacing(BlockFace facing) {
		this.facing = facing;
	}

	public void setHalf(BlockFace half) {
		this.half = half;
	}
}
