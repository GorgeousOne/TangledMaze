package me.gorgeousone.tangledmaze.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
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
import me.gorgeousone.tangledmaze.util.Utils;

public class BlockChangeListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		update(e.getBlock().getLocation().add(0, -1, 0), e);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		update(e.getBlock().getLocation(), e);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent e) {
		
		for(Block b : e.blockList())
			if(Utils.isLikeGround(b.getType()))
				update(b.getLocation(), e);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e) {
		if(Utils.isLikeGround(e.getBlock().getType()))
			update(e.getBlock().getLocation(), e);
	}
	
	//pumpkin/melon growing
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent e) {
		if(Utils.isLikeGround(e.getNewState().getType()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
	}
	
	//grass, mycelium spreading
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent e) {
		if(Utils.isLikeGround(e.getBlock().getType()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
	}
	
	//ice melting
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent e) {
		if(Utils.isLikeGround(e.getBlock().getType()) && !Utils.isLikeGround(e.getNewState().getType()))
			update(e.getBlock().getLocation(), e);
	}

	//falling sand/gravel... and maybe endermen
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(!Utils.isLikeGround(e.getBlock().getType()) &&  Utils.isLikeGround(e.getTo()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
			
		if(Utils.isLikeGround(e.getBlock().getType()) && !Utils.isLikeGround(e.getTo()))
			update(e.getBlock().getLocation(), e);
	}
	
	public void update(Location loc, Cancellable event) {
		BukkitRunnable calculation = new BukkitRunnable() {
			@Override
			public void run() {
				
				for(Maze maze : MazeHandler.getMazes()) {
					
					if(!maze.getClip().contains(new MazePoint(loc)))
						continue;
					
					if(Renderer.isMazeVisible(maze) && maze.isHighlighted(loc.getBlock()))
						Renderer.hideMaze(maze);
					
					maze.updateHeight(loc);
				}
				
				for(Tool tool : ToolHandler.getTools()) {
					
					if(!(tool instanceof ClippingTool))
						continue;
					
					ClippingTool clipboard = (ClippingTool) tool;
					
					//TODOcheck if contains() should be modified to Location
					if(!clipboard.getClip().contains(new MazePoint(loc)))
						continue;
					
					if(Renderer.isClipboardVisible(clipboard) && clipboard.isHighlighted(loc.getBlock()))
						Renderer.hideClipboard(clipboard, true);
					
					clipboard.updateHeight(loc);
				}
			}
		};
		calculation.runTask(TangledMain.getPlugin());
	}
}