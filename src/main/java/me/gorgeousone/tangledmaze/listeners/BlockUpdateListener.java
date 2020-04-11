package me.gorgeousone.tangledmaze.listeners;

import me.gorgeousone.tangledmaze.handlers.ClipToolHandler;
import me.gorgeousone.tangledmaze.handlers.MazeHandler;
import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.tools.ClipTool;
import me.gorgeousone.tangledmaze.utils.Vec2;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for hiding mazes and clipboards whenever a border block of them
 * is broken/somehow changed, touched or breathed at.
 */
public class BlockUpdateListener implements Listener {
	
	private JavaPlugin plugin;
	private ClipToolHandler clipHandler;
	private MazeHandler mazeHandler;
	
	public BlockUpdateListener(JavaPlugin plugin,
	                           ClipToolHandler clipHandler,
	                           MazeHandler mazeHandler) {
		this.plugin = plugin;
		this.clipHandler = clipHandler;
		this.mazeHandler = mazeHandler;
	}
	
	private void updateChangedClips(Block changedBlock, boolean hideClips) {
		
		Vec2 changedPoint = new Vec2(changedBlock);
		
		Map<Player, Maze> changedMazes = getChangedMazes(changedPoint);
		Map<Player, ClipTool> changedClipTools = getChangedClipTools(changedPoint);
		
		if(!changedClipTools.isEmpty() || !changedMazes.isEmpty())
			new BukkitRunnable() {
				@Override
				public void run() {
					updateClipsWithChangedTerrain(changedBlock, changedMazes, changedClipTools, hideClips);
				}
			}.runTask(plugin);
	}
	
	
	private Map<Player, Maze> getChangedMazes(Vec2 point) {
		
		Map<Player, Maze> changedMazes = new HashMap<>();
		
		for (Map.Entry<UUID, Maze> entry : mazeHandler.getPlayerMazes().entrySet()) {
			Maze maze = entry.getValue();
			
			if (maze.hasClip() && !maze.isConstructed() && maze.getClip().contains(point))
				changedMazes.put(Bukkit.getPlayer(entry.getKey()), maze);
		}
		
		return changedMazes;
	}
	
	private Map<Player, ClipTool> getChangedClipTools(Vec2 point) {
		
		Map<Player, ClipTool> changedClipboards = new HashMap<>();
		
		for (Map.Entry<UUID, ClipTool> entry : clipHandler.getPlayerClipTools().entrySet()) {
			ClipTool clipTool = entry.getValue();
			
			if (clipTool.isVertex(point) || clipTool.hasClip() && clipTool.getClip().contains(point))
				changedClipboards.put(Bukkit.getPlayer(entry.getKey()), clipTool);
		}
		
		return changedClipboards;
	}
	
	private void updateClipsWithChangedTerrain(Block changedBlock,
	                                           Map<Player, Maze> changedMazes,
	                                           Map<Player, ClipTool> changedClipTools,
	                                           boolean hideClips) {
		
		for (Player player : changedMazes.keySet()) {
			Maze maze = changedMazes.get(player);
			
			if (hideClips && mazeHandler.isMazeVisible(maze) && maze.getClip().isBorderBlock(changedBlock))
				mazeHandler.hideMazeOf(player);
			
			Block updatedBlock = maze.updateHeight(changedBlock);
			
			if (!hideClips && updatedBlock != null && mazeHandler.isMazeVisible(maze) && maze.getClip().isBorderBlock(updatedBlock))
				mazeHandler.redisplayMazeBlock(player, updatedBlock);
		}
		
		for (Player player : changedClipTools.keySet()) {
			ClipTool clipTool = changedClipTools.get(player);
			
			if (hideClips && clipHandler.isClipToolVisible(clipTool) &&
			    (clipTool.getClip().isBorderBlock(changedBlock) || clipTool.isVertexBlock(changedBlock)))
				clipHandler.hideClipToolOf(player, true);
			
			Block updatedBlock = clipTool.updateHeight(changedBlock);
			
			if (!hideClips && updatedBlock != null && clipHandler.isClipToolVisible(clipTool) && clipTool.getClip().isBorderBlock(updatedBlock))
				clipHandler.redisplayClipToolBlock(player, updatedBlock);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		updateChangedClips(event.getBlock().getRelative(BlockFace.DOWN), true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		updateChangedClips(event.getBlock(), true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent event) {
		
		for (Block block : event.blockList()) {
			updateChangedClips(block, true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		updateChangedClips(event.getBlock(), false);
	}
	
	//pumpkin/melon growing
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent event) {
		updateChangedClips(event.getBlock().getRelative(BlockFace.DOWN), false);
	}
	
	//grass, mycelium spreading
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		updateChangedClips(event.getBlock(), false);
	}
	
	//obsidian, concrete
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		updateChangedClips(event.getBlock().getRelative(BlockFace.DOWN), false);
	}
	
	//ice melting
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		updateChangedClips(event.getBlock(), false);
	}
	
	//falling sand... and maybe endermen
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		updateChangedClips(event.getBlock().getRelative(BlockFace.DOWN), true);
	}
}