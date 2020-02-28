package me.gorgeousone.tangledmaze.generation.blockdatapickers;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.block.data.BlockData;

import java.util.Random;

public class RandomBlockDataPicker extends AbstractBlockDataPicker {
	
	private Random random;
	
	public RandomBlockDataPicker() {
		random = new Random();
	}
	
	@Override
	public BlockData pickBlockData(BlockDataState block, BlockComposition blockComposition, TerrainMap terrainMap) {
		return blockComposition.getBlockAtAmount(random.nextInt(blockComposition.getSize() + 1));
	}
}
