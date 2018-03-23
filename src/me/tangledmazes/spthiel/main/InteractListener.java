package me.tangledmazes.spthiel.main;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.tangledmazes.gorgeousone.model.Constants;
import me.tangledmazes.gorgeousone.model.RectSelection;

public class InteractListener implements Listener {
	
	private HashMap<Player, RectSelection> selections;
	private HashMap<Player, Block> movingSelection;
	
	public InteractListener() {
		selections = new HashMap<>();
		movingSelection = new HashMap<>();
	}
	
	public void reset() {
		for(RectSelection sel : selections.values())
			sel.vanish();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		//check what item the player is holding
		if(e.getItem() == null || e.getItem().getType() != Material.GOLD_SPADE)
			return;

		//cancel the event so the block does not break
		e.setCancelled(true);
		
		RectSelection selection;
		Block b = e.getClickedBlock();
		
		if(selections.containsKey(p)) {
			selection = selections.get(p);

			
			if(movingSelection.containsKey(p)) {
				p.sendMessage("move");
				selection.moveVertexTo(movingSelection.get(p), b);
				movingSelection.remove(p);
				return;
			}
			
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				selection.sendBlockLater(b.getLocation(), Constants.SELECTION_MOVE);
				movingSelection.put(p, b);
				return;
			}
			
			selection.vanish();

			if(selection.isComplete()) {
				p.sendMessage("");
				p.sendMessage("newer selection");
				selection = new RectSelection(p, b);
				selections.put(p, selection);
				
			}else {
				p.sendMessage("expanding selection");
				selection.addVertex(b);
			}			
		}else {
			p.sendMessage("new selection");
			selection = new RectSelection(p, b);
			selections.put(p, selection);
		}
	}
	
}