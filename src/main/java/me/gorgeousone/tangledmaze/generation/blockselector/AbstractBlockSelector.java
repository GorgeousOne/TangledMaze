package me.gorgeousone.tangledmaze.generation.blockselector;

import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;

import java.util.Set;

public abstract class AbstractBlockSelector {
	
	public abstract Set<BlockDataState> getBlocks(TerrainMap terrainMap);
}
