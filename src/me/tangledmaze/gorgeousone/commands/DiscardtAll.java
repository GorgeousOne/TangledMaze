package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class DiscardtAll {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public DiscardtAll() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		sHandler.discardSelection(p);
		mHandler.discardMaze(p);
		sHandler.resetTool(p);

		p.sendMessage(Constants.prefix + "Deselected your selection and your maze.");
	}
}