package me.gorgeousone.tangledmaze.commands;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;
import me.gorgeousone.tangledmaze.shapes.Rectangle;
import me.gorgeousone.tangledmaze.utils.Constants;

public class DiscardAll {
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
//		SelectionHandler.resetSelection(p);
//		SelectionHandler.discardMaze(p);
		SelectionHandler.setSelection(p, new ShapeSelection(p, new Rectangle()));
		p.sendMessage(Constants.prefix + "Deselected your selection and your maze.");
	}
}