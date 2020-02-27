package me.gorgeousone.tangledmaze.generation.blockdatapickers;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.block.data.BlockData;

public abstract class AbstractBlockDataPicker {
	
	public abstract BlockData pickBlockData(BlockDataState block, BlockComposition blockComposition,
	                                        TerrainMap terrainMap);
}
