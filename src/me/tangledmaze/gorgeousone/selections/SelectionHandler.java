package me.tangledmaze.gorgeousone.selections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;

import me.tangledmaze.gorgeousone.events.MazeShapeEvent;
import me.tangledmaze.gorgeousone.events.SelectionCompleteEvent;
import me.tangledmaze.gorgeousone.events.SelectionResizeEvent;
import me.tangledmaze.gorgeousone.events.SelectionStartEvent;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import me.tangledmaze.gorgeousone.shapes.ExitSetter;

public class SelectionHandler {

	private PluginManager pm;
	private MazeHandler mHandler;
	
	private HashMap<UUID, Class<? extends Shape>> selectionTypes;
	private HashMap<UUID, RectSelection> selections;
	private HashMap<UUID, Block> resizingSelections;
	private HashMap<RectSelection, Boolean> selectionVisibilities;
	
	public SelectionHandler() {
		pm = Bukkit.getServer().getPluginManager();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		selectionTypes        = new HashMap<>();
		selections            = new HashMap<>();
		resizingSelections    = new HashMap<>();
		selectionVisibilities = new HashMap<>();
	}
	
	//hides all selections before reloading since they will be deleted anyway.
	public void reload() {
		for(RectSelection selection : selections.values())
			hide(selection);
		
		selectionTypes.clear();
		selections.clear();
		resizingSelections.clear();
		selectionVisibilities.clear();
	}
	
	public void handleInteraction(Player p, Block b, Action a) {
		
		//give this player a type of selection he is starting xD
		if(!selectionTypes.containsKey(p.getUniqueId()))
			selectionTypes.put(p.getUniqueId(), Rectangle.class);
		
		Class<? extends Shape> type = selectionTypes.get(p.getUniqueId());
		
		//handle rectangles and selections separately
		if(type.equals(Rectangle.class) || type.equals(Ellipse.class))
			selectRect(p, b);
		
		//handle the brushing here
		else if(type.equals(Brush.class)) {
			
			Maze maze = mHandler.getMaze(p);
			MazeAction brush = null;
			
			//get affected blocks of shaping as a MazeAction
			if(a == Action.RIGHT_CLICK_BLOCK) {
				brush = maze.reduce(b);
			}else
				brush = maze.enlarge(b);
			
			//if the maze was shaped somehow call an event
			if(brush != null) {
			
				MazeShapeEvent brushing = new MazeShapeEvent(maze, brush);
				pm.callEvent(brushing);
				
				if(brushing.isCancelled())	//TODO wait a tick?
					Utils.sendBlockLater(p, b.getLocation(), Constants.MAZE_BORDER);
			
			}else if(Math.random() < 1/3d)
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
			
		}else if(type.equals(ExitSetter.class))
			mHandler.addExitToMaze(p, b);
	}
	
	private void selectRect(Player p, Block b) {
		RectSelection selection;
		UUID uuid = p.getUniqueId();
		
		//if there is already a selection started by the player
		if(selections.containsKey(uuid)) {
			selection = selections.get(uuid);
			
			//handles selection resizing
			if(resizingSelections.containsKey(uuid)) {
				SelectionResizeEvent resize = new SelectionResizeEvent(p, resizingSelections.get(uuid), b);
				pm.callEvent(resize);
				
				if(!resize.isCancelled())	//TODO actually wait that one tick before doing this?
					resizingSelections.remove(uuid);
				return;
			}
			
			//begins selection resizing
			if(selection.isVertex(b)) {
				if(!selection.isComplete())
					return;
				
				resizingSelections.put(uuid, b);
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
		return selections.containsKey(p.getUniqueId());
	}
	
	public RectSelection getSelection(Player p) {
		return selections.get(p.getUniqueId());
	}
	
	public void setSelection(Player p, RectSelection selection) {
		selections.put(p.getUniqueId(), selection);
		selectionVisibilities.put(selection, false);
	}

	public Class<? extends Shape> getSelectionType(Player p) {
		if(!p.isOnline() || !p.hasPermission(Constants.buildPerm))
			return null;

		return selectionTypes.containsKey(p.getUniqueId()) ? selectionTypes.get(p.getUniqueId()) : Rectangle.class;
	}
	
	public void setSelectionType(Player p, Class<? extends Shape> type) {
		selectionTypes.put(p.getUniqueId(), type);
	}
	
	public boolean isVisible(RectSelection selection) {
		return selectionVisibilities.get(selection);
	}
	
	@SuppressWarnings("deprecation")
	public void show(RectSelection selection) {
		Player p = selection.getPlayer();
		
		if(p == null)
			return;
		
		selectionVisibilities.put(selection, true);
		
		if(selection.isComplete())
			for(ArrayList<Location> chunk : selection.getShape().getBorder().values())
				for(Location point : chunk)
					p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
		
		for(Location vertex : selection.getVertices())
			p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
	}

	@SuppressWarnings("deprecation")
	public void hide(RectSelection selection) {
		Player p = selection.getPlayer();
		
		if(p == null)
			return;
		
		selectionVisibilities.put(selection, false);
		
		if(selection.isComplete()) {
			HashMap<Chunk, ArrayList<Location>> border = selection.getShape().getBorder();

			for(Chunk c : border.keySet())
				for(Location point : border.get(c))
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}
		
		for(Location vertex : selection.getVertices())
			p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		
		if(mHandler.hasMaze(p) && mHandler.isVisible(mHandler.getMaze(p))) {
			Maze maze = mHandler.getMaze(p);
			
			if(selection.isComplete()) {
				HashMap<Chunk, ArrayList<Location>> border = selection.getShape().getBorder();

				for(Chunk c : border.keySet())
					if(maze.getBorder().containsKey(c))
						for(Location point : border.get(c))
							if(maze.isHighlighted(point.getBlock()))
								p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
			}
			
			for(Location vertex : selection.getVertices())
				if(maze.isHighlighted(vertex.getBlock()))
					p.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
		}
	}
	
	public void deselectSelection(Player p) {
		UUID uuid = p.getUniqueId();
		
		if(selections.containsKey(uuid)) {
			if(isVisible(selections.get(uuid)))
				hide(selections.get(uuid));
			
			selectionVisibilities.remove(selections.get(uuid));
			resizingSelections.remove(uuid);
			selections.remove(uuid);
		}
	}
	
	public void remove(Player p) {
		UUID uuid = p.getUniqueId();
		
		selectionVisibilities.remove(selections.get(uuid));
		selectionTypes.remove(uuid);
		selections.remove(uuid);
		resizingSelections.remove(uuid);
	}

	public void resetTool(Player p) {
		UUID uuid = p.getUniqueId();
		
		if(!selectionTypes.containsKey(uuid))
			return;

		Class<? extends Shape> selectionType = selectionTypes.get(uuid);
		
		if(selectionType != Rectangle.class && selectionType != Ellipse.class)
			selectionTypes.put(uuid, Rectangle.class);
	}
}