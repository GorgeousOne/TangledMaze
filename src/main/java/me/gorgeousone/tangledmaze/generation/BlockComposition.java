package me.gorgeousone.tangledmaze.generation;

import org.bukkit.block.data.BlockData;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockComposition {

	private LinkedHashMap<BlockData, Integer> composition;
	private int size;

	public BlockComposition() {
		composition = new LinkedHashMap<>();
	}

	public int getSize() {
		return size;
	}

	public void addBlock(BlockData data, int amount) {
		if(composition.putIfAbsent(data, amount) == null)
			size += amount;
	}

	public BlockData getDataAtPercentage(double percentage) {
		return getBlockAtAmount((int) (percentage * size + 0.5));
	}

	public BlockData getBlockAtAmount(int amount) {

		int iterAmount = 0;

		for(Map.Entry<BlockData, Integer> entry : getComposition().entrySet()) {
			iterAmount += entry.getValue();

			if(iterAmount >= amount || iterAmount == size)
				return entry.getKey();
		}

		return null;
	}

	public Map<BlockData, Integer> getComposition() {
		return composition;
	}
}
