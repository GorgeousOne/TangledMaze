package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.generation.typechoosing.AbstractBlockTypeChooser;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.util.BlockType;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class BlockGenerator {

	private BlockGenerator() {}

	public static void updateBlocks(
			Set<BlockState> blocksToUpdate,
			List<BlockType> blockTypeList,
			AbstractBlockTypeChooser blockTypeChooser,
			TerrainMap terrainMap,
			ActionListener callback) {

		Iterator<BlockState> iter = blocksToUpdate.iterator();

		new BukkitRunnable() {
			@Override
			public void run() {

				long timer = System.currentTimeMillis();

				while(iter.hasNext()) {

					BlockState nextBlock = iter.next();

					if(blockTypeChooser != null) {
						BlockType blockType = blockTypeChooser.chooseBlockType(nextBlock, blockTypeList, terrainMap);
						nextBlock.setType(blockType.getMaterial());
						nextBlock.setBlockData(blockType.getData());
					}

					nextBlock.update(true, false);
					iter.remove();

					if(System.currentTimeMillis() - timer >= 49)
						return;
				}

				if(callback != null)
					callback.actionPerformed(null);

				this.cancel();
			}
		}.runTaskTimer(TangledMain.getInstance(), 0, 1);
	}
}
