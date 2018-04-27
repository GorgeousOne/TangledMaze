package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class DeselectAll {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public DeselectAll() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		sHandler.deselectSelection(p);
		mHandler.deselctMaze(p);
		
		p.sendMessage(Constants.prefix + "Deselected your selection and your maze.");
	}
}