package me.gorgeousone.tangledmaze.generation;

import me.gorgeousone.tangledmaze.generation.blockdatapickers.AbstractBlockDataPicker;
import me.gorgeousone.tangledmaze.generation.terrainmap.TerrainMap;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

public final class BlockGenerator {
	
	private BlockGenerator() {}
	
	public static void updateBlocks(
			JavaPlugin plugin,
			Set<LocatedBlockData> blocksToUpdate,
			BlockComposition blockComposition,
			AbstractBlockDataPicker blockDataPicker,
			TerrainMap terrainMap,
			ActionListener callback) {
		
		Iterator<LocatedBlockData> iter = blocksToUpdate.iterator();
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				long timer = System.currentTimeMillis();
				
				while (iter.hasNext()) {
					
					LocatedBlockData nextBlock = iter.next();
					BlockState state = nextBlock.getBlock().getState();
					
					if (blockDataPicker != null)
						state.setBlockData(blockDataPicker.pickBlockData(nextBlock, blockComposition, terrainMap));
					else
						state.setBlockData(nextBlock.getData());
					
					state.update(true, false);
					
					if (System.currentTimeMillis() - timer >= 49)
						return;
				}
				
				if (callback != null)
					callback.actionPerformed(null);
				
				this.cancel();
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}