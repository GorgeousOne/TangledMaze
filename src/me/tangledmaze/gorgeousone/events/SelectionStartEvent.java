package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;

public class SelectionStartEvent extends SelectionEvent {
	
	private SelectionHandler sHandler;
	
	public SelectionStartEvent(Player p, Block clickedBlock) {
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
		if(sHandler.hasSelection(p))
			sHandler.getSelection(p).hide();
		
		RectSelection selection = new RectSelection(super.clickedBlock, super.p, sHandler.getSelectionType(p));
		sHandler.setSelection(super.p, selection);
		selection.show();
	}
}
