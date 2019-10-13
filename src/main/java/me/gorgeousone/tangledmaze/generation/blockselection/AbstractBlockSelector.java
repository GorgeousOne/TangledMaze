package me.gorgeousone.tangledmaze.generation.blockselection;

import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import org.bukkit.block.BlockState;

import java.util.Set;

public abstract class AbstractBlockSelector {

	public abstract Set<BlockState> getRelevantBlocks(TerrainMap terrainMap);
}
