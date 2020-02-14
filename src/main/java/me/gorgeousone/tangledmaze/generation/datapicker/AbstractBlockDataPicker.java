package me.gorgeousone.tangledmaze.generation.datapicker;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.block.data.BlockData;

public abstract class AbstractBlockDataPicker {

	public abstract BlockData pickBlockData(BlockDataState block, BlockComposition blockComposition,
	                                        TerrainMap terrainMap);
}
