package me.gorgeousone.tangledmaze.generation;

import java.awt.event.ActionListener;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

public abstract class AbstractGenerator {
	
	private List<Material> blockMaterials;
	
	public void generatePart(TerrainMap terrainMap, List<Material> blockMaterials) {
		
		setWallMaterials(blockMaterials);
		updateBlocksContinuously(getRelevantBlocks(terrainMap), null);
	}
	
	protected List<Material> getWallMaterials() {
		return blockMaterials;
	}

	protected void setWallMaterials(List<Material> wallMaterials) {
		this.blockMaterials = wallMaterials;
	}
	
	protected abstract void setBlockMaterial(BlockState block);

	protected abstract List<BlockState> getRelevantBlocks(TerrainMap terrainMap);

	protected void updateBlocksContinuously(List<BlockState> blocksToUpdate, ActionListener callback) {
		
		BukkitRunnable builder = new BukkitRunnable() {
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while(!blocksToUpdate.isEmpty()) {
					
					BlockState block = blocksToUpdate.get(0);
					setBlockMaterial(block);
					
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
}