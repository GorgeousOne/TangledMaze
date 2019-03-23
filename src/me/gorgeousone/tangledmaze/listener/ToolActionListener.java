package me.gorgeousone.tangledmaze.listener;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Particle;
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
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.Utils;

@SuppressWarnings("deprecation")
public class ToolActionListener implements Listener{
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {

		if(Utils.isMazeWand(e.getItem())) {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
	}
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		
		Action action = e.getAction();
		
		if(action != Action.LEFT_CLICK_BLOCK &&
		   action != Action.RIGHT_CLICK_BLOCK)
			return;
		
		try {
			if(e.getHand() != EquipmentSlot.HAND)
				return;
		} catch (NoSuchMethodError err) {}
		
		if(!Utils.isMazeWand(e.getItem()))
			return;
		
		e.setCancelled(true);
		
		Player p = e.getPlayer();
		ItemStack wand = e.getItem();
		
		if(!p.hasPermission(Constants.BUILD_PERM)) {
			destroyMazeWand(p, wand);
			return;
		}
		
		ToolHandler.getTool(p).interact(e.getClickedBlock(), action);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		
		Player p = e.getPlayer();
		ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
		
		if(Utils.isMazeWand(newItem)) {
				
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(ToolHandler.hasClipboard(p) && !Renderer.isClipboardVisible(ToolHandler.getClipboard(p)))
				Renderer.showClipboard(ToolHandler.getClipboard(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickUp(PlayerPickupItemEvent e) {

		if(Utils.isMazeWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(ToolHandler.hasClipboard(p) && !Renderer.isClipboardVisible(ToolHandler.getClipboard(p)))
				Renderer.showClipboard(ToolHandler.getClipboard(p));
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		Renderer.updateChunk(e.getChunk());
	}
	
	private void destroyMazeWand(Player p, ItemStack wand) {
		
		p.getInventory().remove(wand);
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such mighty tool, it broke apart.");
		p.damage(0);
		
		if(Constants.BUKKIT_VERSION == 8) {
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ITEM_BREAK"), 1f, 1f);
			p.getWorld().playEffect(p.getLocation().add(0, 1, 0), Effect.valueOf("EXPLOSION_HUGE"), 0);
			
		}else {
			p.getWorld().playSound(p.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
			p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 1);
		}
	}
}