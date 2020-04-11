package me.gorgeousone.tangledmaze.generation.blockdatapickers;

import me.gorgeousone.tangledmaze.generation.BlockComposition;
import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import org.bukkit.block.data.BlockData;

public abstract class AbstractBlockDataPicker {
	
	public abstract BlockData pickBlockData(LocatedBlockData block, BlockComposition blockComposition,
	                                        TerrainMap terrainMap);
}
