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

public class SelecionHandler implements Listener {
	
	private HashMap<Player, RectSelection> selections;
	private HashMap<Player, Block> movingSelection;
	
	public SelecionHandler() {
		selections = new HashMap<>();
		movingSelection = new HashMap<>();
	}
	
	/**
	 * Handles everything a player can do with their selection wand.
	 */
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

		//if there is already a selection started by the player
		if(selections.containsKey(p)) {
			selection = selections.get(p);
			
			//handles selection resizing
			if(movingSelection.containsKey(p)) {
				p.sendMessage("move");
				selection.moveVertexTo(movingSelection.get(p), b);
				movingSelection.remove(p);
				return;
			}
			
			//begins selection resizing
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				selection.sendBlockLater(b.getLocation(), Constants.SELECTION_MOVE);
				movingSelection.put(p, b);
				return;
			}
			
			selection.vanish();
			
			//begins a new selection 
			if(selection.isComplete()) {
				p.sendMessage("");
				p.sendMessage("newer selection");
				selection = new RectSelection(p, b);
				selections.put(p, selection);
			
			//sets second vertex for selection
			}else {
				p.sendMessage("expanding selection");
				selection.addVertex(b);
			}
		//begins players first selection since they joined
		}else {
			p.sendMessage("new selection");
			selection = new RectSelection(p, b);
			selections.put(p, selection);
		}
	}
	
	public RectSelection getSelection(Player p) {
		return selections.get(p);
	}

	public boolean hasSelection(Player p) {
		return selections.containsKey(p);
	}
	
	public void deselect(Player p) {
		if(selections.containsKey(p)) {
			selections.get(p).vanish();
			selections.remove(p);
			
			if(movingSelection.containsKey(p))
				movingSelection.remove(p);
		}
	}
}