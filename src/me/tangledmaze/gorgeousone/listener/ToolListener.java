package me.tangledmaze.gorgeousone.listener;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class ToolListener implements Listener {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	private HashMap<Player, Long> times;
	private BukkitRunnable timer;
	private static final int expiration = 10*1000;
	
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
							mHandler.hide(mHandler.getMaze(p));
					}
			}
		};
		timer.runTaskTimer(TangledMain.getPlugin(), 0, 1*20);
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if(TangledMain.isSelectionWand(e.getItem()))
			e.setCancelled(true);
	}

	@EventHandler
	public void inInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if(e.getAction() != Action.LEFT_CLICK_BLOCK &&
		   e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		ItemStack item = e.getItem();
		
		if(item == null || !TangledMain.isSelectionWand(item))
			return;
		
		e.setCancelled(true);

		if(!p.hasPermission(Constants.buildPerm)) {
			p.getWorld().playEffect(p.getLocation().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);
			p.getWorld().playSound( p.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
			p.damage(0);

			p.getInventory().remove(item);
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such a mighty tool... it broke apart.");
			return;
		}
		sHandler.handleInteraction(p, e.getClickedBlock());
	}
	
	@EventHandler
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		Inventory i = p.getInventory();
		
		ItemStack previousItem = i.getItem(e.getPreviousSlot()),
				  newItem      = i.getItem(e.getNewSlot());
		
		if(TangledMain.isSelectionWand(newItem)) {
			times.remove(p);
				
			if(mHandler.hasMaze(p) && !mHandler.isVisible(mHandler.getMaze(p)))
				mHandler.show(mHandler.getMaze(p));
			if(sHandler.hasSelection(p) && !sHandler.isVisible(sHandler.getSelection(p)))
				sHandler.show(sHandler.getSelection(p));
		
		}else if(TangledMain.isSelectionWand(previousItem))			
			times.put(p, System.currentTimeMillis());
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onPickUp(PlayerPickupItemEvent e) {
		if(!e.isCancelled() && TangledMain.isSelectionWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
				times.remove(p);
			
			if(mHandler.hasMaze(p) && !mHandler.isVisible(mHandler.getMaze(p)))
				mHandler.show(mHandler.getMaze(p));
			if(sHandler.hasSelection(p) && !sHandler.isVisible(sHandler.getSelection(p)))
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

}