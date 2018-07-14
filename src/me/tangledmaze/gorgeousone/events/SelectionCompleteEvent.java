package me.tangledmaze.gorgeousone.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.ShapeSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.selections.ToolType;
import me.tangledmaze.gorgeousone.shapes.Ellipse;
import me.tangledmaze.gorgeousone.shapes.Rectangle;
import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;

public class SelectionCompleteEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	private ShapeSelection selection;
	private Shape shape;
	
	public SelectionCompleteEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		selection = (ShapeSelection) sHandler.getSelection(p);
		
		//save the given locations as the first 2 corners of the selection.
		Location
			v0 = selection.getVertices().get(0),
			v1 = clickedBlock.getLocation();
		
		//get the vertices for the shape
		ArrayList<Location> vertices = Utils.calcRectangleVertices(v0, v1);
		
		int maxMazeSize = TangledMain.getPlugin().getNormalMazeSize();
		
		if(p.hasPermission(Constants.staffPerm))
			maxMazeSize = TangledMain.getPlugin().getStaffMazeSize();
		else if(p.hasPermission(Constants.vipPerm))
			maxMazeSize = TangledMain.getPlugin().getVipMazeSize();
		
		if(maxMazeSize >= 0 && shape.size() > maxMazeSize) {
			setCancelled(true, ChatColor.RED + "Already this selection would be " + (shape.size() - maxMazeSize)
					+ " blocks greater than the amount of blocks you are allowed to use for a maze (" + maxMazeSize + " blocks).");
		}
		
		HashMap<Chunk, ArrayList<Location>> shapeFill = shape.getFill();
		
		for(Maze maze : mHandler.getMazes()) {
			if(p.equals(maze.getPlayer()))
				continue;
			
			for(Chunk c : shapeFill.keySet())
				for(Location point : shapeFill.get(c))
					
					if(maze.isFill(point.getBlock())) {
						cancelMessage = ChatColor.RED + "You cannot create your selection here. It would intersect the maze of someone else.";
						setCancelled(true);
						break;
					}
		}
		
		if(isCancelled)
			p.sendMessage(cancelMessage);
		
		//set the shape of the selection to this new shape
		else {
			sHandler.hide(selection);
			selection.setShape(shape);
			sHandler.show(selection);
		}
	}
	
	public Player getPlayer() {
		return selection.getPlayer();
	}
	
	public ShapeSelection getSelection() {
		return selection;
	}
	
	public Shape getShape() {
		return shape;
	}
}