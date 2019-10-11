package me.gorgeousone.tangledmaze.generation;

import java.awt.event.ActionListener;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

public abstract class AbstractGenerator {
	
	public void generatePart(TerrainMap terrainMap, List<Material> blockMaterials, ActionListener callback) {
		updateBlocksContinuously(getRelevantBlocks(terrainMap), blockMaterials, callback);
	}
	
	protected abstract void chooseBlockMaterial(BlockState block, List<Material> blockMaterials);

	protected abstract List<BlockState> getRelevantBlocks(TerrainMap terrainMap);

	protected void updateBlocksContinuously(
			List<BlockState> blocksToUpdate,
			List<Material> blockMaterials,
			ActionListener callback) {

		new BukkitRunnable() {
			@Override
			public void run() {

				long timer = System.currentTimeMillis();

				while(!blocksToUpdate.isEmpty()) {

					BlockState block = blocksToUpdate.get(0);
					chooseBlockMaterial(block, blockMaterials);

					block.update(true, false);
					blocksToUpdate.remove(0);

					if(isLeaves(block))
						setLeavesPersistant(block.getBlock());

					if(System.currentTimeMillis() - timer >= 49)
						return;
				}

				this.cancel();

				if(callback != null)
					callback.actionPerformed(null);
			}
		}.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}

	protected boolean isLeaves(BlockState block) {
		return block.getType().name().endsWith("LEAVES");
	}

	protected void setLeavesPersistant(Block block) {
		Leaves leaves = (Leaves) block.getBlockData();
		leaves.setPersistent(true);
		block.setBlockData(leaves);
	}
}