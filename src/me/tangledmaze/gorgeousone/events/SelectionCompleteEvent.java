package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class SelectionCompleteEvent extends SelectionEvent {

private SelectionHandler sHandler;
	
	public SelectionCompleteEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.plugin.getSelectionHandler();
		
		BukkitRunnable event = new BukkitRunnable() {
			@Override
			public void run() {
				if(!isCancelled())
					execute();
			}
		};
		event.runTask(TangledMain.plugin);
	}
	
	private void execute() {
		RectSelection selection = sHandler.getSelection(p);
		selection.hide();
		selection.complete(super.clickedBlock);
		selection.show();
	}
}
