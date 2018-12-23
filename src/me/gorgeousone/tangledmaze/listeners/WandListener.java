package me.gorgeousone.tangledmaze.listeners;

import org.bukkit.Bukkit;
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
import me.gorgeousone.tangledmaze.core.TangledMain;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

@SuppressWarnings("deprecation")
public class WandListener implements Listener{
	
	private TangledMain plugin;
	
	public WandListener(TangledMain plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if(plugin.isMazeWand(e.getItem()))
			e.setCancelled(true);
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
		
		if(!plugin.isMazeWand(e.getItem()))
			return;
		
		e.setCancelled(true);
		
		Player p = e.getPlayer();
		ItemStack wand = e.getItem();
		
		if(!p.hasPermission(Constants.buildPerm)) {
			destroyMazeWand(p, wand);
			return;
		}
		
		ToolHandler.getTool(p).interact(e.getClickedBlock(), action);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSlotSwitch(PlayerItemHeldEvent e) {
		
		Player p = e.getPlayer();
		ItemStack newItem = p.getInventory().getItem(e.getNewSlot());
		
		if(TangledMain.getPlugin().isMazeWand(newItem)) {
				
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(ToolHandler.hasClipboard(p) && !Renderer.isShapeVisible(ToolHandler.getClipboard(p)))
				Renderer.showClipboard(ToolHandler.getClipboard(p));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickUp(PlayerPickupItemEvent e) {

		if(TangledMain.getPlugin().isMazeWand(e.getItem().getItemStack())) {
			Player p = e.getPlayer();
			
			if(MazeHandler.hasMaze(p) && !Renderer.isMazeVisible(MazeHandler.getMaze(p)))
				Renderer.showMaze(MazeHandler.getMaze(p));
			
			if(ToolHandler.hasClipboard(p) && !Renderer.isShapeVisible(ToolHandler.getClipboard(p)))
				Renderer.showClipboard(ToolHandler.getClipboard(p));
		}
	}
	
	private void destroyMazeWand(Player p, ItemStack wand) {
		
		p.getInventory().remove(wand);
		p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "It seems like you are unworthy to use such a mighty tool... it broke apart.");

		p.damage(0);
		
		if(Bukkit.getVersion().contains("1.8")) {
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ITEM_BREAK"), 1f, 1f);
			p.getWorld().playEffect(p.getLocation().add(0, 1, 0), Effect.EXPLOSION_HUGE, 0);

		}else {
			p.getWorld().playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1f, 1f);
			p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 1);
		}
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		Renderer.updateChunk(e.getChunk());
	}
}
