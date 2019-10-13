package me.gorgeousone.tangledmaze.generation.typechoosing;

import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.block.BlockState;

import java.util.List;

public abstract class AbstractBlockTypeChooser {

	public abstract BlockType chooseBlockType(BlockState state, List<BlockType> blockTypes, TerrainMap terrainMap);
}
