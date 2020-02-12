package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.generation.datapicker.AbstractBlockDataPicker;
import me.gorgeousone.tangledmaze.mapmaking.TerrainMap;
import me.gorgeousone.tangledmaze.utils.BlockDataState;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

public final class BlockGenerator {

	private BlockGenerator() {}

	public static void updateBlocks(
			Set<BlockDataState> blocksToUpdate,
			BlockComposition blockComposition,
			AbstractBlockDataPicker blockDataPicker,
			TerrainMap terrainMap,
			ActionListener callback) {

		Iterator<BlockDataState> iter = blocksToUpdate.iterator();

		new BukkitRunnable() {
			@Override
			public void run() {

				long timer = System.currentTimeMillis();

				while(iter.hasNext()) {

					BlockDataState nextBlock = iter.next();
					BlockState state = nextBlock.getBlock().getState();
					state.setBlockData(nextBlock.getData());

					if(blockDataPicker != null) {
						BlockData newBlockData = blockDataPicker.pickBlockData(nextBlock, blockComposition, terrainMap);
						state.setBlockData(newBlockData);
					}

					state.update(true, false);
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
