package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sun.javafx.geom.Rectangle;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Shape;
import net.md_5.bungee.api.ChatColor;

public class SelectionCompleteEvent extends SelectionEvent {

private SelectionHandler sHandler;
	
	public SelectionCompleteEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		RectSelection selection = sHandler.getSelection(p);
		Class<? extends Shape> shapeType = sHandler.getSelectionType(p);
		
		int dx = Math.abs(selection.getVertices().get(0).getBlockX() - clickedBlock.getX()),
		    dz = Math.abs(selection.getVertices().get(0).getBlockZ() - clickedBlock.getZ());
		double size;
		
		if(shapeType == Rectangle.class)
			size = dx * dz;
		else
			size = (Math.PI * Math.pow(dz/2d, 2) * dx/dz);
				
		int buildLimit;
		
		if(p.hasPermission(Constants.staffPerm))
			buildLimit = TangledMain.getBuidlSizeStaff();
		else if(p.hasPermission(Constants.vipPerm))
			buildLimit = TangledMain.getBuidlSizeVIP();
		else
			buildLimit = TangledMain.getBuidlSizeNormal();
		
		if(buildLimit >= 0 && size > buildLimit)
			setCancelled(true);
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				if(isCancelled())
					p.sendMessage(ChatColor.RED + "This action was cancelled because the marked area would be " +
				    (int) (size*100) / 100 + " blocks bigger than your maze build limit of " + buildLimit + " blocks.");
				
				else {
					RectSelection selection = sHandler.getSelection(p);
					sHandler.hide(selection);
					selection.complete(clickedBlock);
					sHandler.show(selection);
				}
				
			}
		};
		event.runTask(TangledMain.getPlugin());
		event.runTask(TangledMain.getPlugin());
	}
}