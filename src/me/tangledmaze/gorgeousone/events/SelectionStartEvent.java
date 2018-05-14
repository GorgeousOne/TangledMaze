package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class SelectionStartEvent extends SelectionEvent {
	
	private SelectionHandler sHandler;
	
	public SelectionStartEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				
				if(isCancelled)
					p.sendMessage(cancelMessage);
				
				else {
					sHandler.deselectSelection(p);
					
					RectSelection selection = new RectSelection(clickedBlock, p);
					sHandler.setSelection(p, selection);
					sHandler.show(selection);
				}
			}
		};
		event.runTask(TangledMain.getPlugin());
	}
}