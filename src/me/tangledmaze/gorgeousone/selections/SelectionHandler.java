package me.tangledmaze.gorgeousone.selections;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import me.tangledmaze.gorgeousone.events.SelectionCompleteEvent;
import me.tangledmaze.gorgeousone.events.SelectionResizeEvent;
import me.tangledmaze.gorgeousone.events.SelectionStartEvent;
import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.shapes.EntranceSetter;

public class SelectionHandler implements Listener {

	private PluginManager pm;
	private MazeHandler mHandler;
	
	private HashMap<Player, Class<? extends Shape>> selectionTypes;
	private HashMap<Player, RectSelection> selections;
	private HashMap<Player, Block> resizingSelections;
	
	public SelectionHandler() {
		pm = Bukkit.getServer().getPluginManager();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		selectionTypes  = new HashMap<>();
		selections = new HashMap<>();
		resizingSelections = new HashMap<>();
	}
	
	//hides all selections before reloading since they will be deleted anyway.
	public void reload() {
		for(RectSelection selection : selections.values())
			selection.hide();
	}
	
	//handles interactions of players with blocks when using a selection wand
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(e.getAction() != Action.LEFT_CLICK_BLOCK &&
		   e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
					
		if(e.getItem() == null || !TangledMain.isSelectionWand(e.getItem()))
			return;
		
		e.setCancelled(true);
		Block b = e.getClickedBlock();
		
		if(!selectionTypes.containsKey(p))
			selectionTypes.put(p, Rectangle.class);
		
		Class<? extends Shape> type = selectionTypes.get(p);
		
		if(type.equals(Rectangle.class) || type.equals(Ellipse.class)) {
			selectRect(p, b);
			
		}else if(type.equals(Brush.class)) {
			if(!mHandler.hasMaze(p))
				return;

			Maze maze = mHandler.getMaze(p);
			maze.brush(b);
				
			if(maze.getSize() == 0) {
				mHandler.removeMaze(p);
				setSelectionType(p, Rectangle.class);
			}
		
		}else if(type.equals(EntranceSetter.class)) {
			if(!mHandler.hasMaze(p))
				return;
			
			mHandler.getMaze(p).addEntrance(b);
		}
		
	}
	
	private void selectRect(Player p, Block b) {
		RectSelection selection;

		//if there is already a selection started by the player
		if(selections.containsKey(p)) {
			selection = selections.get(p);
			
			//handles selection resizing
			if(resizingSelections.containsKey(p)) {
				pm.callEvent(new SelectionResizeEvent(p, resizingSelections.get(p), b));
				resizingSelections.remove(p);
				return;
			}
			
			//begins selection resizing
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				resizingSelections.put(p, b);
				return;
			}
			
			//begins a new selection 
			if(selection.isComplete())
				pm.callEvent(new SelectionStartEvent(p, b));
			//sets second vertex for selection
			else
				pm.callEvent(new SelectionCompleteEvent(p, b));
			
		//begins players very first selection since they joined
		}else
			pm.callEvent(new SelectionStartEvent(p, b));
	}
	
	public ArrayList<RectSelection> getSelections() {
		return new ArrayList<RectSelection>(selections.values());
	}
	
	public boolean hasSelection(Player p) {
		return selections.containsKey(p);
	}
	
	public RectSelection getSelection(Player p) {
		return selections.get(p);
	}
	
	public void setSelection(Player p, RectSelection selection) {
		selections.put(p, selection);
	}

	public Class<? extends Shape> getSelectionType(Player p) {
		if(!p.isOnline())
			return null;
		//TODO permission check
		return selectionTypes.containsKey(p) ? selectionTypes.get(p) : Rectangle.class;
	}
	
	public void setSelectionType(Player p, Class<? extends Shape> type) {
		selectionTypes.put(p, type);
	}
	
	@SuppressWarnings("deprecation")
	public void deselect(Player p) {
		if(selections.containsKey(p)) {
			
			RectSelection selection = getSelection(p);
			selections.get(p).hide();
			
			if(mHandler.hasMaze(p)) {
				Maze maze = mHandler.getMaze(p);
				
				if(selection.isComplete())
					for(ArrayList<Location> chunk : selection.getShape().getBorder().values())
						for(Location point : chunk)
							if(maze.isHighlighted(point.getBlock()))
								p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
				
				for(Location point : selection.getVertices())
					if(maze.isHighlighted(point.getBlock()))
						p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
			}
			
			resizingSelections.remove(p);
			selections.remove(p);
		}
	}
	
	public void remove(Player p) {
		selectionTypes.remove(p);
		selections.remove(p);
		resizingSelections.remove(p);
	}
}