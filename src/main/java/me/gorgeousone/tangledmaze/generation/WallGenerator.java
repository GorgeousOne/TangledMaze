package me.gorgeousone.tangledmaze.generation;

import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.mapmaking.MazeAreaType;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.Utils;

public class WallGenerator extends AbstractGenerator {
	
	private Random rnd;
	
	public WallGenerator() {
		rnd = new Random();
	}

	@Override
	public void generatePart(TerrainMap terrainMap, List<BlockType> blockTypes, ActionListener callback) {
		super.generatePart(terrainMap, blockTypes, callback);
		terrainMap.getMaze().setConstructed(true);
	}

	protected List<BlockState> getRelevantBlocks(TerrainMap terrainMap) {
		
		List<BlockState> blocksToUpdate = new LinkedList<>();
		List<BlockState> backupBlocks = new LinkedList<>();

		Maze maze = terrainMap.getMaze();
		
		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				if(terrainMap.getAreaType(x, z) != MazeAreaType.WALL)
					continue;
				
				int floorHeight = terrainMap.getFloorHeight(x, z);
				
				for(int relHeight = 1; relHeight <= terrainMap.getWallHeight(x, z); relHeight++) {
					
					Block block = new Location(maze.getWorld(), x, floorHeight + relHeight, z).getBlock();
					
					if(Utils.canBeOverbuild(block.getType())) {
						
						blocksToUpdate.add(block.getState());
						backupBlocks.add(block.getState());		
					}
				}
			}
		}
		
		BuildHandler.setBuiltWallBlocks(maze, backupBlocks);
		return blocksToUpdate;
	}
}