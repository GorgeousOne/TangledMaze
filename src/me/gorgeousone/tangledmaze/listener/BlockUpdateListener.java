package me.gorgeousone.tangledmaze.listener;

import org.bukkit.event.block.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import me.gorgeousone.tangledmaze.util.Vec2;

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

		Vec2 loc = new Vec2(block);
		HashSet<Maze> affectedMazes = getAffectedMazes(loc);
		HashSet<ClippingTool> affectedClipboards = getAffectedClipboards(loc);

		if(affectedClipboards.isEmpty() && affectedMazes.isEmpty())
			return;

		update(block, affectedMazes, affectedClipboards, hideAffectedElements);
	}

	private HashSet<Maze> getAffectedMazes(Vec2 loc) {

		HashSet<Maze> affectedMazes = new HashSet<>();

		for(Maze maze : MazeHandler.getMazes()) {

			if(maze.isStarted() && !maze.isConstructed() && maze.getClip().contains(loc))
				affectedMazes.add(maze);
		}

		return affectedMazes;
	}

	private HashSet<ClippingTool> getAffectedClipboards(Vec2 loc) {

		HashSet<ClippingTool> affectedClipboards = new HashSet<>();

		for(Tool tool : ToolHandler.getTools()) {

			if(!(tool instanceof ClippingTool))
				continue;

			ClippingTool clipboard = (ClippingTool) tool;

			if(clipboard.getClip().contains(loc))
				affectedClipboards.add(clipboard);
		}

		return affectedClipboards;
	}

	private void update(Block changedBlock, HashSet<Maze> affectedMazes, HashSet<ClippingTool> affectedClipboards, boolean hideAffectedElements) {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for(Maze maze : affectedMazes) {

					if(hideAffectedElements && Renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(changedBlock))
						Renderer.hideMaze(maze);

					Location updatedBlock = maze.updateHeight(changedBlock);
					
					if(!hideAffectedElements && updatedBlock != null && Renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(updatedBlock.getBlock()))
						Renderer.redisplayMazeBorder(maze, updatedBlock);
				}

				for(ClippingTool clipboard : affectedClipboards) {

					if(hideAffectedElements && Renderer.isClipboardVisible(clipboard) && clipboard.getClip().isBorderBlock(changedBlock))
						Renderer.hideClipboard(clipboard, true);

					Location updatedBlock = clipboard.updateHeight(changedBlock);

					if(!hideAffectedElements && updatedBlock != null && Renderer.isClipboardVisible(clipboard) && clipboard.getClip().isBorderBlock(updatedBlock.getBlock()))
						Renderer.redisplayClipboardBorder(clipboard, updatedBlock);
				}
			}
		}.runTask(TangledMain.getInstance());
	}
}