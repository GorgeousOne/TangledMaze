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

		new BukkitRunnable() {
			@Override
			public void run() {

				if(blockTypeChooser != null) {
					for (BlockState block : blocksToUpdate) {

						BlockType blockType = blockTypeChooser.chooseBlockType(block, blockTypeList, terrainMap);
						block.setType(blockType.getMaterial());
						block.setBlockData(blockType.getData());
					}
				}

				updateBlocks(blocksToUpdate, callback);
			}
		}.runTaskAsynchronously(TangledMain.getInstance());
	}

	private static void updateBlocks(
			Set<BlockState> blocksToUpdate,
			ActionListener callback) {

		Iterator<BlockState> iter = blocksToUpdate.iterator();

		new BukkitRunnable() {
			@Override
			public void run() {

				long timer = System.currentTimeMillis();

				while(iter.hasNext()) {
					iter.next().update(true, false);

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
