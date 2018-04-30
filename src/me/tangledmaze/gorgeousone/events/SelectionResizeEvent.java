package me.tangledmaze.gorgeousone.events;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Shape;
import me.tangledmaze.gorgeousone.utils.Constants;
import me.tangledmaze.gorgeousone.utils.Utils;

public class SelectionResizeEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private RectSelection selection;
	private Shape shape;
	

	public SelectionResizeEvent(Player p, Block vertex, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		selection = sHandler.getSelection(p);
		
		//return if the vertex would be at its old xz coord
		if(selection.isVertex(clickedBlock)) {
			Utils.sendBlockLater(p, vertex.getLocation(), Constants.SELECTION_CORNER);
			return;
		}
		
		//get the index of the resized corner
		int index = selection.indexOfVertex(vertex);

		//create new corners of new clicked block and the opposite existing corner
		Location
			p0 = clickedBlock.getLocation(),
			p1 = selection.getVertices().get((index+2) % 4);
		
		//get the new vertices for the shape
		ArrayList<Location> vertices = Utils.calcRectangleVertices(p0, p1);

		try {
			//create a shape with the new vertices
			Constructor<? extends Shape> con = sHandler.getSelectionType(p).getConstructor(ArrayList.class);
			shape = con.newInstance(vertices);
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				
				if(!isCancelled()) {
					//set the shape of the selection to the new shape
					sHandler.hide(selection);
					selection.setShape(shape);
					sHandler.show(selection);
				}
			}
		};
		event.runTask(TangledMain.getPlugin());
	}
	
	public Player getPlayer() {
		return selection.getPlayer();
	}
	
	public RectSelection getSelection() {
		return selection;
	}
	
	public Shape getShape() {
		return shape;
	}
}