package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.terrainmap.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;

import java.util.Set;

public abstract class AbstractBlockLocator {
	
	public abstract Set<BlockDataState> locateBlocks(TerrainMap terrainMap);
}
