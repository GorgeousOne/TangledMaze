package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.TangledMain;
import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

/**
 * This class is responsible for hiding mazes and clipboards whenever a border block of them
 * is broken/somehow changed, touched or breathed at.
 */
public class BlockUpdateListener implements Listener {

	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private Renderer renderer;

	public BlockUpdateListener(ClipToolHandler clipHandler, MazeHandler mazeHandler,
	                           Renderer renderer) {
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
		this.renderer = renderer;
	}

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

		for (Block block : e.blockList()) {
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

	//falling sand... and maybe endermen
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		checkForUpdates(e.getBlock().getRelative(BlockFace.DOWN), true);
	}

	private void checkForUpdates(Block block, boolean hideAffectedElements) {

		Vec2 loc = new Vec2(block);
		HashSet<Maze> affectedMazes = getAffectedMazes(loc);
		HashSet<ClipTool> affectedClipboards = getAffectedClipboards(loc);

		if (!affectedClipboards.isEmpty() || !affectedMazes.isEmpty())
			update(block, affectedMazes, affectedClipboards, hideAffectedElements);
	}

	private HashSet<Maze> getAffectedMazes(Vec2 loc) {

		HashSet<Maze> affectedMazes = new HashSet<>();

		for (Maze maze : mazeHandler.getMazes()) {

			if (maze.hasClip() && !maze.isConstructed() && maze.getClip().contains(loc))
				affectedMazes.add(maze);
		}

		return affectedMazes;
	}

	private HashSet<ClipTool> getAffectedClipboards(Vec2 loc) {

		HashSet<ClipTool> affectedClipboards = new HashSet<>();

		for (ClipTool clipTool : clipHandler.getPlayerClipTools()) {

			if (clipTool.getClip().contains(loc) || clipTool.isVertex(loc))
				affectedClipboards.add(clipTool);
		}

		return affectedClipboards;
	}

	private void update(Block changedBlock, HashSet<Maze> affectedMazes, HashSet<ClipTool> affectedClipboards,
	                    boolean hideAffectedElements) {

		new BukkitRunnable() {
			@Override
			public void run() {

				for (Maze maze : affectedMazes) {

					if (hideAffectedElements && renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(changedBlock))
						renderer.hideMaze(maze);

					Location updatedBlock = maze.updateHeight(changedBlock);

					if (!hideAffectedElements && updatedBlock != null && renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(updatedBlock.getBlock()))
						renderer.redisplayMazeBlock(maze, updatedBlock);
				}

				for (ClipTool clipboard : affectedClipboards) {

					if (hideAffectedElements && renderer.isClipboardVisible(clipboard) &&
							(clipboard.getClip().isBorderBlock(changedBlock) || clipboard.isVertex(changedBlock)))
						renderer.hideClipboard(clipboard, true);

					Location updatedBlock = clipboard.updateHeight(changedBlock);

					if (!hideAffectedElements && updatedBlock != null && renderer.isClipboardVisible(clipboard) && clipboard.getClip().isBorderBlock(updatedBlock.getBlock()))
						renderer.redisplayClipboardBlock(clipboard, updatedBlock);
				}
			}
		}.runTask(TangledMain.getInstance());
	}
}