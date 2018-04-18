package me.tangledmaze.gorgeousone.listener;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class ToolListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	private HashMap<Player, Long> times;
	private BukkitRunnable timer;
	private static final int expiration = 0*1000;
	
	public ToolListener() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		times = new HashMap<>();
		
		timer = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : times.keySet())
					
					if(System.currentTimeMillis() - times.get(p) >= expiration) {
						times.remove(p);
								
						if(sHandler.hasSelection(p))
							sHandler.hide(sHandler.getSelection(p));
						if(mHandler.hasMaze(p))
							mHandler.getMaze(p).hide();
					}
			}
		};
		timer.runTaskTimer(TangledMain.getPlugin(), 0, 1*20);
	}
	
	@EventHandler
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		Inventory i = p.getInventory();
		
		ItemStack previousItem = i.getItem(e.getPreviousSlot()),
				  newItem      = i.getItem(e.getNewSlot());
		
		if(TangledMain.isSelectionWand(newItem)) {
			times.remove(p);
			
			if(mHandler.hasMaze(p))
				mHandler.getMaze(p).show();
			if(sHandler.hasSelection(p))
				sHandler.show(sHandler.getSelection(p));
		
		}else if(TangledMain.isSelectionWand(previousItem))			
			times.put(p, System.currentTimeMillis());
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onPickUp(PlayerPickupItemEvent e) {
		if(!e.isCancelled() && TangledMain.isSelectionWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
			if(mHandler.hasMaze(p))
				mHandler.getMaze(p).show();
			if(sHandler.hasSelection(p))
				sHandler.show(sHandler.getSelection(p));
		}
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onDrop(PlayerDropItemEvent e) {
		if(!e.isCancelled() && TangledMain.isSelectionWand(e.getItemDrop().getItemStack())) {
			Player p = e.getPlayer();

			if(sHandler.hasSelection(p) || mHandler.hasMaze(p))
				times.put(p, System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		//sHandler.update(e.getChunk()); TODO implement
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if(TangledMain.isSelectionWand(e.getItem()))
			e.setCancelled(true);
	}
}