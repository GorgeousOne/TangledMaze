package me.tangledmazes.spthiel.main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
		
		@SuppressWarnings("deprecation")
		ItemStack tool = p.getInventory().getItemInHand();
		
		if(tool == null || tool.getType() != Material.GOLD_SPADE)
			return;
		
		Block b = e.getClickedBlock();
		RectSelection selection;
		
		p.sendMessage("Chop");
		
		if(selections.containsKey(p)) {
			selection = selections.get(p);
			
			if(selection.isComplete()) {
				selection = selections.put(p, new RectSelection(p));
				selection = selections.get(p);
			}else
				selection.addVertex(b);
			
		}else {
			selection = selections.put(p, new RectSelection(p));
			selection.addVertex(b);
		}
		
		
	}
}
