package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class Deselect {
	
private SelectionHandler sHandler;
	
	public Deselect() {
		sHandler = TangledMain.plugin.getSelectionHandler();
	}
	
	public void execute(Player p, String selectionType) {
		
		if(sHandler.hasSelection(p))
			sHandler.deselect(p);
	}
}