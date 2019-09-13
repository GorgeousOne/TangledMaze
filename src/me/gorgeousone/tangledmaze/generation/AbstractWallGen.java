package me.gorgeousone.tangledmaze.generation;

import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.handler.BuildHandler;
import me.gorgeousone.tangledmaze.util.Utils;

public abstract class AbstractWallGen {
	
	private List<Material> wallMaterials;
	
	public void generateWalls(TerrainMap terrainMap, List<Material> wallMaterials) {
		
		setWallMaterials(wallMaterials);
		updateBlocksContinuously(getWallBlocks(terrainMap), null);
	}
	
	protected List<Material> getWallMaterials() {
		return wallMaterials;
	}

	protected void setWallMaterials(List<Material> wallMaterials) {
		this.wallMaterials = wallMaterials;
	}
	
	protected List<BlockState> getWallBlocks(TerrainMap terrainMap) {
		
		Maze maze = terrainMap.getMaze();

		List<BlockState> blocksToUpdate = new LinkedList<>();
		List<BlockState> backupBlocks = new LinkedList<>();

		for(int x = terrainMap.getMinX(); x < terrainMap.getMaxX(); x++) {
			for(int z = terrainMap.getMinZ(); z < terrainMap.getMaxZ(); z++) {
				
				if(terrainMap.getType(x, z) != MazeAreaType.WALL)
					continue;
				
				for(int height = terrainMap.getGroundHeight(x, z) + 1; height <= terrainMap.getMazeHeight(x, z); height++) {
					
					Block block = new Location(maze.getWorld(), x, height, z).getBlock();
					
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

	protected void updateBlocksContinuously(List<BlockState> blocksToUpdate, ActionListener callback) {
		
		BukkitRunnable builder = new BukkitRunnable() {
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!blocksToUpdate.isEmpty()) {
					
					BlockState block = blocksToUpdate.get(0);
					setBlock(block);
					
					block.update(true, false);
					blocksToUpdate.remove(0);
					
					if(System.currentTimeMillis() - timer >= 49)
						return;
				}
				
				this.cancel();
				
				if(callback != null)
					callback.actionPerformed(null);
			}
		};
		
		builder.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
	
	protected abstract void setBlock(BlockState block);
}