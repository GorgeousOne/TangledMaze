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

public class SelectionResizeEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	private ShapeSelection selection;
	private Shape shape;
	

	public SelectionResizeEvent(Player p, Block vertex, Block clickedBlock, ToolType type) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		selection = sHandler.getSelection(p);
		
		//return if the vertex would be at its old xz coord
		if(selection.isVertex(clickedBlock)) {
			Utils.sendBlockDelayed(p, vertex.getLocation(), Constants.SELECTION_CORNER);
			return;
		}
		
		//get the index of the resized corner
		int index = selection.indexOfVertex(vertex);

		//create new corners of new clicked block and the opposite existing corner
		Location
			p0 = clickedBlock.getLocation(),
			p1 = selection.getVertices().get((index+2) % 4);
		
		//get the new vertices for the shape
		ArrayList<Location> vertices = ShapeSelection.calcRectangleVertices(p0, p1);

		switch(type) {
		case RECTANGLE:
			shape = new Rectangle(vertices);
			break;
		case ELLIPSE:
			shape = new Ellipse(vertices);
			break;
		default:
			throw new IllegalArgumentException("Cannot create shape from tool type " + type);
	}
		
		int maxMazeSize = TangledMain.getPlugin().getNormalMazeSize();
		
		if(p.hasPermission(Constants.staffPerm))
			maxMazeSize = TangledMain.getPlugin().getStaffMazeSize();
		else if(p.hasPermission(Constants.vipPerm))
			maxMazeSize = TangledMain.getPlugin().getVipMazeSize();
		
		if(maxMazeSize >= 0 && shape.size() > maxMazeSize) {
			setCancelled(true, cancelMessage = ChatColor.RED + "This selection would be " + (shape.size() - maxMazeSize)
					+ " blocks greater that the amount of blocks you are allowed to use for a maze at once (" + maxMazeSize + " blocks).");
		}
		
		HashMap<Chunk, ArrayList<Location>> shapeFill = shape.getFill();
		
		for(Maze maze : mHandler.getMazes()) {
			if(p.equals(maze.getPlayer()))
				continue;
			
			for(Chunk c : shapeFill.keySet())
				for(Location point : shapeFill.get(c))
					
					if(maze.isFill(point.getBlock())) {
						this.cancelMessage = ChatColor.RED + "You cannot create your selection here. It would intersect the maze of someone else.";
						setCancelled(true);
						break;
					}
		}
		
		if(isCancelled)
			p.sendMessage(cancelMessage);
		
		else {
			//set the shape of the selection to the new shape
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