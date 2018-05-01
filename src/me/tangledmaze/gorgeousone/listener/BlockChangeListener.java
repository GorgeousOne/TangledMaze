package me.tangledmaze.gorgeousone.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class BlockChangeListener implements Listener {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public BlockChangeListener() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
//	lockBreakEvent,
//	BlockBurnEvent,
//	BlockExplodeEvent,
//	BlockFadeEvent,
//	BlockFormEvent,
//	BlockGrowEvent,
//	BlockIgniteEvent,
//	BlockPhysicsEvent,
//	BlockPistonEvent,
//	BlockPistonExtendEvent,
//	BlockPlaceEvent,
//	BlockRedstoneEvent,
//	PlayerInteractEvent
//	this are theoretically all events that need to be listened to in order to detect block changes and updates. i don't want to do this. and i wont do this until this plugin brings in money
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!e.isCancelled())
					update(e.getBlock().getLocation().add(0, -1, 0));
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!e.isCancelled())
					update(e.getBlock().getLocation().add(0, -1, 0));
			}
		}.runTask(TangledMain.getPlugin());
	}
	
	//right clicking block doesn't really get detected... only shovels n stuff
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() != Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			return;
		
		ItemStack item = e.getItem();
		
		if(TangledMain.isSelectionWand(item))
			return;

		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		
		if(mHandler.hasMaze(p)) {
			Maze maze = mHandler.getMaze(p);
			if(mHandler.isVisible(maze) && maze.isHighlighted(b))
				mHandler.hide(maze);
		}
		
		if(sHandler.hasSelection(p)) {
			RectSelection selection = sHandler.getSelection(p);
			if(sHandler.isVisible(selection) && selection.isHighlighted(b))
				sHandler.hide(selection);
		}
	}
	
	private void update(Location point) {
		for(Maze maze : mHandler.getMazes())
			if(maze.contains(point)) {
				maze.recalc(point);
				
				if(mHandler.isVisible(maze) && maze.borderContains(point))
					mHandler.hide(maze);
			}
		
		for(RectSelection selection : sHandler.getSelections()) {
			if(selection.contains(point)) {
				selection.recalc(point);
				
				if(sHandler.isVisible(selection) && selection.borderContains(point))
					sHandler.hide(selection);
			}
		}
	}
}