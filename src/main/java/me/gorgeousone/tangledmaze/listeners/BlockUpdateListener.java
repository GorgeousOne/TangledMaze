package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.handlers.Renderer;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.Vec2;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

/**
 * This class is responsible for hiding mazes and clipboards whenever a border block of them
 * is broken/somehow changed, touched or breathed at.
 */
public class BlockUpdateListener implements Listener {
	
	private JavaPlugin plugin;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	private Renderer renderer;
	
	public BlockUpdateListener(JavaPlugin plugin,
	                           ClipToolHandler clipHandler,
	                           MazeHandler mazeHandler, Renderer renderer) {
		this.plugin = plugin;
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
		this.renderer = renderer;
	}
	
	private void checkClipsForTerrainChanges(Block block, boolean hideAffectedElements) {
		
		Vec2 point = new Vec2(block);
		HashSet<Maze> affectedMazes = getAffectedMazes(point);
		HashSet<ClipTool> affectedClipboards = getAffectedClipTools(point);
		
		if (!affectedClipboards.isEmpty() || !affectedMazes.isEmpty())
			updateClipsWithChangedTerrain(block, affectedMazes, affectedClipboards, hideAffectedElements);
	}
	
	private HashSet<Maze> getAffectedMazes(Vec2 point) {
		
		HashSet<Maze> affectedMazes = new HashSet<>();
		
		for (Maze maze : mazeHandler.getMazes()) {
			
			if (maze.hasClip() && !maze.isConstructed() && maze.getClip().contains(point))
				affectedMazes.add(maze);
		}
		
		return affectedMazes;
	}
	
	private HashSet<ClipTool> getAffectedClipTools(Vec2 point) {
		
		HashSet<ClipTool> affectedClipboards = new HashSet<>();
		
		for (ClipTool clipTool : clipHandler.getPlayerClipTools()) {
			
			if (clipTool.getClip().contains(point) || clipTool.isVertex(point))
				affectedClipboards.add(clipTool);
		}
		
		return affectedClipboards;
	}
	
	private void updateClipsWithChangedTerrain(Block changedBlock,
	                                           HashSet<Maze> affectedMazes,
	                                           HashSet<ClipTool> affectedClipTools,
	                                           boolean hideAffectedClips) {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				for (Maze maze : affectedMazes) {
					
					if (hideAffectedClips && renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(changedBlock))
						renderer.hideMaze(maze);
					
					Block updatedBlock = maze.updateHeight(changedBlock);
					
					if (!hideAffectedClips && updatedBlock != null && renderer.isMazeVisible(maze) && maze.getClip().isBorderBlock(updatedBlock))
						renderer.redisplayMazeBlock(maze, updatedBlock.getLocation());
				}
				
				for (ClipTool clipTool : affectedClipTools) {
					
					if (hideAffectedClips && renderer.isClipToolVisible(clipTool) &&
					    (clipTool.getClip().isBorderBlock(changedBlock) || clipTool.isVertex(changedBlock)))
						renderer.hideClipboard(clipTool, true);
					
					Block updatedBlock = clipTool.updateHeight(changedBlock);
					
					if (!hideAffectedClips && updatedBlock != null && renderer.isClipToolVisible(clipTool) && clipTool.getClip().isBorderBlock(updatedBlock))
						renderer.redisplayClipboardBlock(clipTool, updatedBlock.getLocation());
				}
			}
		}.runTask(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		checkClipsForTerrainChanges(event.getBlock().getRelative(BlockFace.DOWN), true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		checkClipsForTerrainChanges(event.getBlock(), true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent event) {
		
		for (Block block : event.blockList()) {
			checkClipsForTerrainChanges(block, false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		checkClipsForTerrainChanges(event.getBlock(), false);
	}
	
	//pumpkin/melon growing
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent event) {
		checkClipsForTerrainChanges(event.getBlock().getRelative(BlockFace.DOWN), false);
	}
	
	//grass, mycelium spreading
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		checkClipsForTerrainChanges(event.getBlock(), false);
	}
	
	//obsidian, concrete
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		checkClipsForTerrainChanges(event.getBlock().getRelative(BlockFace.DOWN), false);
	}
	
	//ice melting
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		checkClipsForTerrainChanges(event.getBlock(), false);
	}
	
	//falling sand... and maybe endermen
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		checkClipsForTerrainChanges(event.getBlock().getRelative(BlockFace.DOWN), true);
	}
}