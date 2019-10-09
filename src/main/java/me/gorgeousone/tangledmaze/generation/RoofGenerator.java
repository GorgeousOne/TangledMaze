package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.MazeDimension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RoofGenerator extends AbstractGenerator {

	private Random rnd;

	public RoofGenerator() {
		rnd = new Random();
	}
	
	@Override
	protected void chooseBlockMaterial(BlockState block,  List<Material> blockMaterials) {
		block.setType(blockMaterials.get(rnd.nextInt(blockMaterials.size())));
	}
	
	@Override
	protected List<BlockState> getRelevantBlocks(TerrainMap terrainMap) {

		List<BlockState> blocksToUpdate = new LinkedList<>();
		List<BlockState> backupBlocks = new LinkedList<>();

		Maze maze = terrainMap.getMaze();
		int roofWidth = maze.getDimension(MazeDimension.ROOF_WIDTH);

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {

				if(terrainMap.getAreaType(x, z) == MazeAreaType.NOT_MAZE)
					continue;

				int roofHeight = terrainMap.getRoofHeight(x, z);

				for(int y = roofHeight+1; y < roofHeight+1 + roofWidth; y++) {

					Block block = new Location(maze.getWorld(), x, y, z).getBlock();
					blocksToUpdate.add(block.getState());
					backupBlocks.add(block.getState());
				}
			}
		}

		BuildHandler.setBuiltRoofBlocks(maze, backupBlocks);
		return blocksToUpdate;
	}
}