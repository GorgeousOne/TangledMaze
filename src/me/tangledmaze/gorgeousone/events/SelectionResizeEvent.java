package me.tangledmaze.gorgeousone.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class SelectionResizeEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private Block oldVertex;
	private Player p;
	
	public SelectionResizeEvent(Player p, Block vertex, Block clickedBlock) {
		super(p, clickedBlock);
		
		this.oldVertex = vertex;
		this.p = p;

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
		RectSelection selection = sHandler.getSelection(p);
		
		if(selection.isVertex(super.clickedBlock)) {
			sHandler.show(selection);
			return;
		}

		sHandler.hide(selection);
		selection.moveVertexTo(oldVertex, super.clickedBlock);
		sHandler.show(selection);
	}
}
