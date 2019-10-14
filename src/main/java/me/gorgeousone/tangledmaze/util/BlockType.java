package me.gorgeousone.tangledmaze.util;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class BlockType {

	private Material material;
	private BlockData data;

	public BlockType(Material material) {
		this(material, material.createBlockData());
	}

	public BlockType(Material material, BlockData data) {
		this.material = material;
		this.data = data;
	}

	public Material getMaterial() {
		return material;
	}

	public BlockData getData() {
		return data;
	}

	@Override
	public BlockType clone() {
		return new BlockType(material, data.clone());
	}
}