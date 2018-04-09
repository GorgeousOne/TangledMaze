package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;

public class Deselect {
	
	private SelectionHandler sHandler;
	
	public Deselect() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
	}
	
	public void execute(Player p) {
		sHandler.deselect(p);
	}
}