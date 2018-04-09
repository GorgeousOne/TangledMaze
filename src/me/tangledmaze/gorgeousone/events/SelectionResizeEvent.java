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
	
	public SelectionResizeEvent(Player p, Block vertex, Block clickedBlock) {
		super(p, clickedBlock);
		
		this.oldVertex = vertex;
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
			selection.showVertices();
			return;
		}

		selection.hide();
		selection.moveVertexTo(oldVertex, super.clickedBlock);
		selection.show();
	}
}
