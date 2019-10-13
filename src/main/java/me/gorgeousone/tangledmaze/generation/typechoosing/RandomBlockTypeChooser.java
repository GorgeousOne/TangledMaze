package me.gorgeousone.tangledmaze.generation.typechoosing;

import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.block.BlockState;

import java.util.List;
import java.util.Random;

public class RandomBlockTypeChooser extends AbstractBlockTypeChooser {

	private Random random;

	public RandomBlockTypeChooser() {
		random = new Random();
	}

	@Override
	public BlockType chooseBlockType(BlockState state, List<BlockType> blockTypes, TerrainMap terrainMap) {
		return blockTypes.get(random.nextInt(blockTypes.size()));
	}
}
