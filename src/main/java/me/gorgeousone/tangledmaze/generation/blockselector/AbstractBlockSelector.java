package me.gorgeousone.tangledmaze.generation.blockselector;

import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.BlockDataState;

import java.util.Set;

public abstract class AbstractBlockSelector {

	public abstract Set<BlockDataState> getBlocks(TerrainMap terrainMap);
}
