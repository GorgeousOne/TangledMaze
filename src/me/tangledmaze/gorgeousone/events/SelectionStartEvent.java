package me.tangledmaze.gorgeousone.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class SelectionStartEvent extends SelectionEvent {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public SelectionStartEvent(Player p, Block clickedBlock) {
		super(p, clickedBlock);
		
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		
		for(Maze maze : mHandler.getMazes()) {
			if(p.equals(maze.getPlayer()))
				continue;
			
			if(maze.isFill(clickedBlock)) {
				this.cancelMessage = ChatColor.RED + "You cannot select this block. Someone else is already working on a maze here.";
				setCancelled(true);
				break;
			}
		}
		
		if(isCancelled)
			p.sendMessage(cancelMessage);
		
		else {
			sHandler.discardSelection(p);
			
			RectSelection selection = new RectSelection(clickedBlock, p);
			sHandler.setSelection(p, selection);
			sHandler.show(selection);
		}
	}
}