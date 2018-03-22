package me.tangledmazes.spthiel.main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.tangledmazes.gorgeousone.model.RectSelection;

public class InteractListener implements Listener {
	
	private HashMap<Player, RectSelection> selections;
	
	public InteractListener() {
		selections = new HashMap<>();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		if(e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		
		if(e.getItem() == null || e.getItem().getType() != Material.GOLD_SPADE)
			return;
		
		Block b = e.getClickedBlock();
		RectSelection selection;
		
		p.sendMessage("Chop");
		e.setCancelled(true);
		
		if(selections.containsKey(p)) {
			selection = selections.get(p);
			
			if(selection.isComplete()) {
				p.sendMessage("newer selection");
				selection = new RectSelection(p);
				selection.addVertex(b);
				selections.put(p, selection);
				
			}else {
				p.sendMessage("expanding selection");
				selection.addVertex(b);
			}			
		}else {
			p.sendMessage("new selection");
			selection = new RectSelection(p);
			selection.addVertex(b);
			selections.put(p, selection);
		}
	}
	
}