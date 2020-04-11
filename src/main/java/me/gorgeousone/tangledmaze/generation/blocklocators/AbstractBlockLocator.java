package me.gorgeousone.tangledmaze.generation.blocklocators;

import me.gorgeousone.tangledmaze.generation.LocatedBlockData;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;

import java.util.Set;

public abstract class AbstractBlockLocator {
	
	public abstract Set<LocatedBlockData> locateBlocks(TerrainMap terrainMap);
}
