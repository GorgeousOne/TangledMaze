package me.gorgeousone.tangledmaze.commands;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

public class DiscardAll {
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		MazeHandler.getMaze(p).reset();
		SelectionHandler.resetToDefaultSel(p);
		p.sendMessage(Constants.prefix + "Deselected your selection and your maze.");
	}
}