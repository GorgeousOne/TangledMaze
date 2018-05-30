package me.tangledmaze.gorgeousone.listener;

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

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Utils;

public class BlockChangeListener implements Listener {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public BlockChangeListener() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
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
			
		if( Utils.isLikeGround(e.getBlock().getType()) && !Utils.isLikeGround(e.getTo()))
			update(e.getBlock().getLocation(), e);
	}
	
	public void update(Location point, Cancellable event) {
		BukkitRunnable calculation = new BukkitRunnable() {
			@Override
			public void run() {
				
				if(event.isCancelled())
					return;
				
				for(Maze maze : mHandler.getMazes())
					if(maze.contains(point)) {
						if(mHandler.isVisible(maze) && maze.isBorder(point.getBlock()))
							mHandler.hide(maze);

						maze.recalc(point);
						break;
					}
				
				for(RectSelection selection : sHandler.getSelections()) {
					if(selection.contains(point)) {
						if(sHandler.isVisible(selection) && selection.isHighlighted(point.getBlock()))
							sHandler.hide(selection);
						
						selection.recalc(point);
						break;
					}
				}
			}
		};
		calculation.runTask(TangledMain.getPlugin());
	}
}