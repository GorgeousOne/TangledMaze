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
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.*;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.utils.*;

public class SelectionHandler {

	@SuppressWarnings("unused")
	private PluginManager pm;
	private MazeHandler mHandler;
	
	private HashMap<UUID, Selection> selections;
	private HashMap<UUID, Block> resizingSelections;
	private HashMap<ShapeSelection, Boolean> selectionVisibilities;
	
	public SelectionHandler() {
		pm = Bukkit.getServer().getPluginManager();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		selections            = new HashMap<>();
		resizingSelections    = new HashMap<>();
		selectionVisibilities = new HashMap<>();
	}
	
	/**
	 * Hides all selections for a reload. They will also be deleted afterwards.
	 */
	public void reload() {
		for(Selection s : selections.values())
			if(s instanceof ShapeSelection)
				hide((ShapeSelection) s);
		
		selections.clear();
		resizingSelections.clear();
		selectionVisibilities.clear();
	}
	
	public void handleInteraction(Player p, Block b, Action a) {
		
		UUID uuid = p.getUniqueId();
		
		//give this player a type of selection he is starting xD
		if(!selections.containsKey(uuid))
			selections.put(uuid, new ShapeSelection(p, new Rectangle()));
		
		selections.get(uuid).interact(b, a);
		
		//handle rectangles and selections separately
//		if(type == ToolType.RECTANGLE || type == ToolType.ELLIPSE)
//			selectRect(p, b);
//		
//		//handle the brushing here
//		else if(type == ToolType.BRUSH) {
//			
//			Maze maze = mHandler.getMaze(p);
//			MazeAction brush = null;
//			
//			//get affected blocks of shaping as a MazeAction
//			if(a == Action.RIGHT_CLICK_BLOCK) {
//				brush = maze.reduce(b);
//			}else
//				brush = maze.enlarge(b);
//			
//			//if the maze was shaped somehow call an event
//			if(brush != null) {
//			
//				MazeShapeEvent brushing = new MazeShapeEvent(maze, brush);
//				pm.callEvent(brushing);
//				
//				if(brushing.isCancelled())	//TODO wait a tick?
//					Utils.sendBlockDelayed(p, b.getLocation(), Constants.MAZE_BORDER);
//			
//			}else if(Math.random() < 1/3d)
//				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This is not your maze's outline...");
//			
//		}else if(type == ToolType.EXIT_SETTER)
//			mHandler.addExitToMaze(p, b);
	}
	
//	private void selectRect(Player p, Block b) {
//		ShapeSelection selection;
//		UUID uuid = p.getUniqueId();
//		
//		//if there is already a selection started by the player
//		if(selections.containsKey(uuid)) {
//			selection = selections.get(uuid);
//			
//			//handles selection resizing
//			if(resizingSelections.containsKey(uuid)) {
//				SelectionResizeEvent resize = new SelectionResizeEvent(p, resizingSelections.get(uuid), b, toolTypes.get(uuid));
//				pm.callEvent(resize);
//				
//				if(!resize.isCancelled())	//TODO actually wait that one tick before doing this?
//					resizingSelections.remove(uuid);
//				return;
//			}
//			
//			//begins selection resizing
//			if(selection.isVertex(b)) {
//				if(!selection.isComplete())
//					return;
//				
//				resizingSelections.put(uuid, b);
//				return;
//			}
//			
//			//begins a new selection 
//			if(selection.isComplete())
//				pm.callEvent(new SelectionStartEvent(p, b));
//			//sets second vertex for selection
//			else
//				pm.callEvent(new SelectionCompleteEvent(p, b, toolTypes.get(uuid)));
//			
//		//begins players very first selection since they joined
//		}else
//			pm.callEvent(new SelectionStartEvent(p, b));
//	}
	
	public ArrayList<ShapeSelection> getShapeSelections() {
		
		ArrayList<ShapeSelection> shapes = new ArrayList<>();
		
		for(Selection s : selections.values())
			if(s instanceof ShapeSelection)
				shapes.add((ShapeSelection) s);
		
		return shapes;
	}
	
	public boolean hasShapeSelection(Player p) {
		UUID uuid = p.getUniqueId();
		return selections.containsKey(uuid) && selections.get(uuid) instanceof ShapeSelection;
	}
	
	public Selection getSelection(Player p) {
		return selections.get(p.getUniqueId());
	}
	
	public void setSelection(Player p, ShapeSelection selection) {
		selections.put(p.getUniqueId(), selection);
		selectionVisibilities.put(selection, false);
	}

	/**
	 * @return if a specific selection is displayed to the player who is creating it.
	 */
	public boolean isVisible(ShapeSelection selection) {
		return selectionVisibilities.get(selection);
	}
	
	/**
	 * Displays the border of a selection to the player who is creating it. A visibility check with <b>isVisible(RectSelection);</b> is recommended before.
	 */
	@SuppressWarnings("deprecation")
	public void show(ShapeSelection selection) {
		Player p = selection.getPlayer();
		
		if(p == null)
			return;

		BukkitRunnable r = new BukkitRunnable() {
			@Override
			public void run() {
				selectionVisibilities.put(selection, true);
				
				if(selection.isComplete())
					for(ArrayList<Location> chunk : selection.getBorder().values())
						for(Location point : chunk)
							p.sendBlockChange(point, Constants.SELECTION_BORDER, (byte) 0);
				
				for(Location vertex : selection.getVertices())
					p.sendBlockChange(vertex, Constants.SELECTION_CORNER, (byte) 0);
			}
		};
		r.runTask(TangledMain.getPlugin());
	}

	/**
	 * Hides a selection from the player who is creating it. A visibility check with <b>isVisible(RectSelection);</b> is recommended before.
	 */
	@SuppressWarnings("deprecation")
	public void hide(ShapeSelection selection) {
		Player p = selection.getPlayer();
		
		if(p == null)
			return;
		
		selectionVisibilities.put(selection, false);
		
		if(selection.isComplete()) {
			HashMap<Chunk, ArrayList<Location>> border = selection.getBorder();

			for(Chunk c : border.keySet())
				for(Location point : border.get(c))
					p.sendBlockChange(point, point.getBlock().getType(), point.getBlock().getData());
		}
		
		for(Location vertex : selection.getVertices())
			p.sendBlockChange(vertex, vertex.getBlock().getType(), vertex.getBlock().getData());
		
		if(mHandler.hasMaze(p) && mHandler.isVisible(mHandler.getMaze(p))) {
			Maze maze = mHandler.getMaze(p);
			
			if(selection.isComplete()) {
				HashMap<Chunk, ArrayList<Location>> border = selection.getBorder();

				for(Chunk c : border.keySet())
					if(maze.getBorder().containsKey(c))
						for(Location point : border.get(c))
							if(maze.isBorder(point.getBlock()))
								p.sendBlockChange(point, Constants.MAZE_BORDER, (byte) 0);
			}
			
			for(Location vertex : selection.getVertices())
				if(maze.isBorder(vertex.getBlock()))
					p.sendBlockChange(vertex, Constants.MAZE_BORDER, (byte) 0);
		}
	}
	
	public void discardSelection(Player p) {
//		UUID uuid = p.getUniqueId();
		
		//TODO
//		if(selections.containsKey(uuid)) {
//			if(isVisible(selections.get(uuid)))
//				hide(selections.get(uuid));
//			
//			selectionVisibilities.remove(selections.get(uuid));
//			resizingSelections.remove(uuid);
//			selections.remove(uuid);
//		}
	}

	/**
	 * Removes all held data to a player in this object.
	 */
	public void remove(Player p) {
		UUID uuid = p.getUniqueId();
		
		selectionVisibilities.remove(selections.get(uuid));
		selections.remove(uuid);
		resizingSelections.remove(uuid);
	}

	/**
	 * Resets the selection type of a player to an actual shape (rectangle / ellipse).<br>
	 */
//	public void resetTool(Player p) {
//		UUID uuid = p.getUniqueId();
//		
//		if(!toolTypes.containsKey(uuid))
//			return;
//
//		ToolType type = toolTypes.get(uuid);
//		
//		if(type != ToolType.RECTANGLE && type != ToolType.ELLIPSE)
//			toolTypes.put(uuid, ToolType.RECTANGLE);
//	}
}