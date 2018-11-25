package me.gorgeousone.tangledmaze.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.gorgeousone.tangledmaze.core.Renderer;
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

@SuppressWarnings("deprecation")
public class WandListener implements Listener{
	
	private TangledMain plugin;
	
	public WandListener(TangledMain plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if(plugin.isWand(e.getItem()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		
		Action a = e.getAction();
		
		if(a != Action.LEFT_CLICK_BLOCK &&
		   a != Action.RIGHT_CLICK_BLOCK)
			return;
		
		try {
			if(e.getHand() != EquipmentSlot.HAND)
				return;
		} catch (NoSuchMethodError err) {}
		
		if(!plugin.isWand(e.getItem()))
			return;
		
		e.setCancelled(true);
		
		Player p = e.getPlayer();
		ItemStack wand = e.getItem();
		
		if(!p.hasPermission(Constants.buildPerm)) {
			destroyTool(p, wand);
			return;
		}
		
		SelectionHandler.getSelection(p).interact(e.getClickedBlock(), a);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		
		Player p = e.getPlayer();
		ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
		
		if(TangledMain.getPlugin().isWand(newItem)) {
				
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(SelectionHandler.hasShape(p) && !Renderer.isShapeVisible(SelectionHandler.getShape(p)))
				Renderer.showShape(SelectionHandler.getShape(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickUp(PlayerPickupItemEvent e) {

		if(TangledMain.getPlugin().isWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(SelectionHandler.hasShape(p) && !Renderer.isShapeVisible(SelectionHandler.getShape(p)))
				Renderer.showShape(SelectionHandler.getShape(p));
		}
	}
	
	private void destroyTool(Player p, ItemStack wand) {
		
		p.getInventory().remove(wand);
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such mighty tool... it broke apart.");

		p.damage(0);
		p.getWorld().playEffect(p.getLocation().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);
		
		if(Bukkit.getVersion().contains("1.8"))
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ITEM_BREAK"), 1f, 1f);
		else
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1f, 1f);
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		Renderer.updateChunk(e.getChunk());
	}
}
