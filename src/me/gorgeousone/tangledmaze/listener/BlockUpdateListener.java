package me.gorgeousone.tangledmaze.listener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.tool.Tool;
import me.gorgeousone.tangledmaze.util.MazePoint;

import java.util.HashSet;

public class BlockUpdateListener implements Listener {

	//TODO hide mazes and clipboards only if owner breaks a block
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		checkForUpdates(e.getBlock().getRelative(BlockFace.DOWN), true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		checkForUpdates(e.getBlock(), true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent e) {

		for(Block block : e.blockList()) {
			checkForUpdates(block, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e) {
		checkForUpdates(e.getBlock(), false);
	}

	//pumpkin/melon growing
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent e) {
		checkForUpdates(e.getBlock().getRelative(BlockFace.DOWN), false);
	}

	//grass, mycelium spreading
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent e) {
		checkForUpdates(e.getBlock(), false);
	}

	//obsidian, concrete
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent e) {
		checkForUpdates(e.getBlock().getRelative(BlockFace.DOWN), false);
	}

	//ice melting
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent e) {
		checkForUpdates(e.getBlock(), false);
	}

	//falling sand/gravel... and maybe endermen
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		//TODO hide block under falling sand and make sand elements border
		checkForUpdates(e.getBlock(), true);
	}

	private void checkForUpdates(Block block, boolean hideAffectedElements) {

		MazePoint point = new MazePoint(block.getLocation());
		HashSet<Maze> affectedMazes = getAffectedMazes(point);
		HashSet<ClippingTool> affectedClipboards = getAffectedClipboards(point);

		if(affectedClipboards.isEmpty() && affectedMazes.isEmpty()) {
			return;
		}

		update(block, affectedMazes, affectedClipboards, hideAffectedElements);
	}

	private HashSet<Maze> getAffectedMazes(MazePoint point) {

		HashSet<Maze> affectedMazes = new HashSet<>();

		for(Maze maze : MazeHandler.getMazes()) {

			if(!maze.getClip().contains(new MazePoint(point)))
				continue;

			affectedMazes.add(maze);
		}

		return affectedMazes;
	}

	private HashSet<ClippingTool> getAffectedClipboards(MazePoint point) {

		HashSet<ClippingTool> affectedClipboards = new HashSet<>();

		for(Tool tool : ToolHandler.getTools()) {

			if(!(tool instanceof ClippingTool)) {
				continue;
			}

			ClippingTool clipboard = (ClippingTool) tool;

			if(!clipboard.getClip().contains(point)) {
				continue;
			}

			affectedClipboards.add(clipboard);
		}

		return affectedClipboards;
	}

	private void update(Block changedBlock, HashSet<Maze> affectedMazes, HashSet<ClippingTool> affectedClipboards, boolean hideAffectedElements) {

		new BukkitRunnable() {
			@Override
			public void run() {

				for(Maze maze : affectedMazes) {

					if(hideAffectedElements && Renderer.isMazeVisible(maze) && maze.isHighlighted(changedBlock)) {
						Renderer.hideMaze(maze);
					}

					Block updatedBlock = maze.updateHeight(changedBlock);

					//TODO only update visibility of changed block
					if(!hideAffectedElements && updatedBlock != null) {
						Renderer.updateChunk(updatedBlock.getChunk());
					}
				}

				for(ClippingTool clipboard : affectedClipboards) {

					if(hideAffectedElements && Renderer.isClipboardVisible(clipboard) && clipboard.isHighlighted(changedBlock)) {
						Renderer.hideClipboard(clipboard, true);
					}

					Block updatedBlock = clipboard.updateHeight(changedBlock);

					if(!hideAffectedElements && updatedBlock != null) {
						Renderer.updateChunk(updatedBlock.getChunk());
					}
				}
			}
		}.runTask(TangledMain.getPlugin());
	}
}