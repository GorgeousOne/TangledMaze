package me.tangledmaze.gorgeousone.events;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class SelectionResizeEvent extends SelectionEvent {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private Block oldVertex;
	private Player p;
	
	public SelectionResizeEvent(Player p, Block vertex, Block clickedBlock) {
		super(p, clickedBlock);
		
		this.oldVertex = vertex;
		this.p = p;

		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
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
		
		if(mHandler.hasMaze(p))
			 mHandler.getMaze(p).update(new ArrayList<Chunk> (selection.getShape().getBorder().keySet()));
		
		selection.moveVertexTo(oldVertex, super.clickedBlock);
		selection.show();
	}
}
