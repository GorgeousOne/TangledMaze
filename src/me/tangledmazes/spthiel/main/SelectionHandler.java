package me.tangledmazes.spthiel.main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import me.tangledmazes.gorgeousone.exceptions.MazeNotFoundException;
import me.tangledmazes.gorgeousone.exceptions.SelectionNotFoundExcetion;
import me.tangledmazes.gorgeousone.main.Constants;
import me.tangledmazes.gorgeousone.main.TangledMain_go;
import me.tangledmazes.gorgeousone.mazestuff.Maze;
import me.tangledmazes.gorgeousone.selectionstuff.RectSelection;
import me.tangledmazes.main.TangledMain;

public class SelectionHandler implements Listener {
	
	public static final int
		RECTANGULAR = 0,
		ELLIPTICAL = 1,
		POLYGONAL = 2;

	private HashMap<Player, Maze> mazes;
	private HashMap<Player, Integer> selectionTypes;
	private HashMap<Player, RectSelection> selections;
	private HashMap<Player, Block> resizingSelections;
	
	public SelectionHandler() {
		mazes           = new HashMap<>();
		selectionTypes  = new HashMap<>();
		selections      = new HashMap<>();
		resizingSelections = new HashMap<>();
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
			if(resizingSelections.containsKey(p)) {
				
				//returns if new vertex is an old vertex
				if(selection.isVertex(b)) {
					resizingSelections.remove(p);
					selection.show();
					return;
				}
				
				//p.sendMessage("move");
				selection.moveVertexTo(resizingSelections.get(p), b);
				resizingSelections.remove(p);
				return;
			}
			
			//begins selection resizing
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				Location vertex = selection.getVertices().get(selection.indexOfVertex(b));
				TangledMain_go.sendBlockLater(selection.getPlayer(), vertex, Constants.SELECTION_MOVE);
				resizingSelections.put(p, vertex.getBlock());
				return;
			}
			
			selection.hide();
			
			//begins a new selection 
			if(selection.isComplete()) {
				selection = new RectSelection(p, b);
				selections.put(p, selection);
			
			//sets second vertex for selection
			}else {
				selection.complete(b);
				selections.remove(p);
				
				if(!mazes.containsKey(p))
					mazes.put(p, new Maze(p, selection.getShape()));
				else
					mazes.get(p).add(selection.getShape());
				
			}
		//begins players first selection since they joined
		}else {
			selection = new RectSelection(p, b);
			selections.put(p, selection);
		}
	}
	
	@EventHandler
	public void onDurabilityChange(PlayerItemDamageEvent e) {
		if(TangledMain.isSelectionWand(e.getItem()))
			e.setCancelled(true);
	}
	
	public RectSelection getSelection(Player p) {
		return selections.get(p);
	}

	public boolean hasSelection(Player p) {
		return selections.containsKey(p);
	}
	
	public void setSelectionType(Player p, int type) {
		selectionTypes.put(p, type);
	}
	
	public void deselect(Player p) {
		if(selections.containsKey(p)) {
			selections.get(p).hide();
			selections.remove(p);
			
			if(resizingSelections.containsKey(p))
				resizingSelections.remove(p);
		}
	}
	
	public void startMaze(Player p) throws Exception {
		if(!selections.containsKey(p))
			throw new SelectionNotFoundExcetion();
		RectSelection selection = selections.get(p);
		
		if(!selection.isComplete())
			throw new IllegalArgumentException();
		
		mazes.put(p, new Maze(p, selection.getShape()));
	}
	
	public void addSelectionToMaze(Player p) throws Exception {
		if(!selections.containsKey(p))
			throw new SelectionNotFoundExcetion();
		if(!mazes.containsKey(p))
			throw new MazeNotFoundException();
		RectSelection selection = selections.get(p);
		
		if(!selection.isComplete())
			throw new IllegalArgumentException();
		
		mazes.get(p).add(selection.getShape());
	}
	
	public void subtractSelctionFromMaze(Player p)  throws Exception {
		if(!selections.containsKey(p))
			throw new SelectionNotFoundExcetion();
		if(!mazes.containsKey(p))
			throw new MazeNotFoundException();
		RectSelection selection = selections.get(p);
		
		if(!selection.isComplete())
			throw new IllegalArgumentException();
		
		mazes.get(p).subtract(selection.getShape());
	}
}