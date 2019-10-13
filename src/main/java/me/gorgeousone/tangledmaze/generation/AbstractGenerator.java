package me.gorgeousone.tangledmaze.generation;

import java.awt.event.ActionListener;
import java.util.List;

import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;

public abstract class AbstractGenerator {
	
	public void generatePart(TerrainMap terrainMap, List<BlockType> blockTypes, ActionListener callback) {
		updateBlocksContinuously(getRelevantBlocks(terrainMap), blockTypes, callback);
	}

	protected abstract List<BlockState> getRelevantBlocks(TerrainMap terrainMap);

	protected void assignBlockType(BlockState block, List<BlockType> blockBlockTypes) {

		BlockType random = blockBlockTypes.get((int) (Math.random() * blockBlockTypes.size()));
		block.setType(random.getMaterial());
		block.setBlockData(random.getData());
	}

	protected void updateBlocksContinuously(
			List<BlockState> blocksToUpdate,
			List<BlockType> blockTypes,
			ActionListener callback) {

		new BukkitRunnable() {
			@Override
			public void run() {

				long timer = System.currentTimeMillis();

				while(!blocksToUpdate.isEmpty()) {

					BlockState block = blocksToUpdate.get(0);
					assignBlockType(block, blockTypes);

					block.update(true, false);
					blocksToUpdate.remove(0);

					if(System.currentTimeMillis() - timer >= 49)
						return;
				}

				this.cancel();

				if(callback != null)
					callback.actionPerformed(null);
			}
		}.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
}