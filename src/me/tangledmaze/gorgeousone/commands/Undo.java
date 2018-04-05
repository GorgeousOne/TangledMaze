package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.main.TangledMain;

public class Undo {

	@SuppressWarnings("unused")
	private SelectionHandler handler;
	
	public Undo() {
		handler = TangledMain.plugin.getSelectionHandler();
	}
	
	public void execute(Player p) {
		try {
			
		} catch (Exception e) {
			if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "");
		}
	}
}