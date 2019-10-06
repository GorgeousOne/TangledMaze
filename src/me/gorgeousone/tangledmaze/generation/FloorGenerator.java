package me.gorgeousone.tangledmaze.generation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

public class FloorGenerator extends AbstractGenerator {

	private Random rnd;
	
	public FloorGenerator() {
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
		
		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				if(terrainMap.getAreaType(x, z) != MazeAreaType.PATH)
					continue;

				Block block = new Location(maze.getWorld(), x, terrainMap.getFloorHeight(x,z), z).getBlock();
				
				blocksToUpdate.add(block.getState());
				backupBlocks.add(block.getState());		
			}
		}

		BuildHandler.setBuiltFloorBlocks(maze, backupBlocks);
		return blocksToUpdate;
	}
}