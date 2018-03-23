package me.tangledmazes.spthiel.main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.tangledmazes.gorgeousone.model.Constants;
import me.tangledmazes.gorgeousone.model.RectSelection;
import me.tangledmazes.main.TangledMain;

public class SelectionHandler implements Listener {
	
	private HashMap<Player, RectSelection> selections;
	private HashMap<Player, Block> movingSelection;
	
	public SelectionHandler() {
		selections = new HashMap<>();
		movingSelection = new HashMap<>();
	}
	
	/**
	 * Hides all selections before reloading since they will be deleted anyway.
	 */
	public void reload() {
		for(RectSelection selection : selections.values())
			selection.hide();
	}
	
	/**
	 * Handles everything a player can do with their selection wand.
	 */
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		//check what item the player is holding
		if(e.getItem() == null || !TangledMain.isSelectionWand(e.getItem()))
			return;
		
		//cancel the event so the block does not break or so
		e.setCancelled(true);
		
		if(e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		
		Block b = e.getClickedBlock();
		RectSelection selection;

		//if there is already a selection started by the player
		if(selections.containsKey(p)) {
			selection = selections.get(p);
			
			//handles selection resizing
			if(movingSelection.containsKey(p)) {
				
				//returns if new vertex is an old vertex
				if(selection.isVertex(b)) {
					movingSelection.remove(p);
					selection.show();
					return;
				}
				
				//p.sendMessage("move");
				selection.moveVertexTo(movingSelection.get(p), b);
				movingSelection.remove(p);
				return;
			}
			
			//begins selection resizing
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				Location vertex = selection.getVertices().get(selection.indexOfVertex(b));
				selection.sendBlockLater(vertex, Constants.SELECTION_MOVE);
				movingSelection.put(p, vertex.getBlock());
				return;
			}
			
			selection.hide();
			
			//begins a new selection 
			if(selection.isComplete()) {
				//p.sendMessage("");
				//p.sendMessage("newer selection");
				selection = new RectSelection(p, b);
				selections.put(p, selection);
			
			//sets second vertex for selection
			}else {
				//p.sendMessage("expanding selection");
				selection.addVertex(b);
			}
		//begins players first selection since they joined
		}else {
			//p.sendMessage("new selection");
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
			selections.get(p).hide();
			selections.remove(p);
			
			if(movingSelection.containsKey(p))
				movingSelection.remove(p);
		}
	}
}