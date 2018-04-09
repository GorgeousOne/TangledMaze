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
				if(!isCancelled())
					execute();
			}
		};
		event.runTask(TangledMain.getPlugin());
	}
	
	private void execute() {
		sHandler.deselect(p);

		RectSelection selection = new RectSelection(super.clickedBlock, super.p, sHandler.getSelectionType(p));
		sHandler.setSelection(super.p, selection);
		selection.show();
	}
}
