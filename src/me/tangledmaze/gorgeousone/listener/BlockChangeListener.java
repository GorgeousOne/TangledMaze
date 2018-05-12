package me.tangledmaze.gorgeousone.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
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
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		update(e.getBlock().getLocation().add(0, -1, 0), e);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		update(e.getBlock().getLocation(), e);
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent e) {
		
		for(Block b : e.blockList())
			if(Utils.isLikeGround(b.getType()))
				update(b.getLocation(), e);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		if(Utils.isLikeGround(e.getBlock().getType()))
			update(e.getBlock().getLocation(), e);
	}
	
	//pumpkin/melon growing
	@EventHandler
	public void onBlockGrow(BlockGrowEvent e) {
		if(Utils.isLikeGround(e.getNewState().getType()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
	}
	
	//ice melting
	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		if(Utils.isLikeGround(e.getBlock().getType()) && !Utils.isLikeGround(e.getNewState().getType()))
			update(e.getBlock().getLocation(), e);
	}
	
	//water freezing, obsidian forming TODO check if it is a subclass of BlockGrowEvent
	@EventHandler
	public void onBlockForm(BlockFormEvent e) {
		if(Utils.isLikeGround(e.getNewState().getType()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(!Utils.isLikeGround(e.getBlock().getType()) &&  Utils.isLikeGround(e.getTo()))
			update(e.getBlock().getLocation().add(0, -1, 0), e);
			
		if( Utils.isLikeGround(e.getBlock().getType()) && !Utils.isLikeGround(e.getTo()))
			update(e.getBlock().getLocation(), e);
	}
	
	//right clicking block does get detected. i only had some wrong statements in the beginning
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		ItemStack item = e.getPlayer().getInventory().getItemInHand();
		
		if(TangledMain.isSelectionWand(item))
			return;
		
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();

		//tnt ignite
		if(b.getType() == Material.TNT && item != null)
			if(item.getType() == Material.FLINT_AND_STEEL || item.getType() == Material.FIREBALL) {
				update(b.getLocation(), e);
				return;
			}
		
		if(mHandler.hasMaze(p) && mHandler.getMaze(p).isHighlighted(b))
			mHandler.hide(mHandler.getMaze(p));
		
		if(sHandler.hasSelection(p) && sHandler.getSelection(p).isHighlighted(b))
			sHandler.hide(sHandler.getSelection(p));
	}
	
	private void update(Location point, Cancellable event) {
		BukkitRunnable calculation = new BukkitRunnable() {
			@Override
			public void run() {
					
				for(Maze maze : mHandler.getMazes())
					if(maze.contains(point)) {
						if(mHandler.isVisible(maze) && maze.isHighlighted(point.getBlock()))
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