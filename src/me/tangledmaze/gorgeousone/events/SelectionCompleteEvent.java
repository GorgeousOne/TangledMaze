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
import me.tangledmaze.gorgeousone.utils.Utils;

public class SelectionCompleteEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private RectSelection selection;
	private Shape shape;
	
	public SelectionCompleteEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		selection = sHandler.getSelection(p);
		
		//get 2 corners of the selection of the first existing on and the new clicked blocked
		Location
			p0 = selection.getVertices().get(0),
			p1 = clickedBlock.getLocation();
		
		//get the vertices for a shape
		ArrayList<Location> vertices = Utils.calcRectangleVertices(p0, p1);

		try {
			//create a new shape with the the shape type the selection handler holds
			Constructor<? extends Shape> con = sHandler.getSelectionType(p).getConstructor(ArrayList.class);
			shape = con.newInstance(vertices);
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				
				//set the shape of the selection to this new shape
				if(!isCancelled()) {
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