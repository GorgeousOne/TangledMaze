package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.exceptions.SelectionNotFoundExcetion;
import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import net.md_5.bungee.api.ChatColor;

public class Start {
	
	private SelectionHandler handler;

	public Start(TangledMain_go plugin) {
		handler = plugin.getSelectionHandler();
	}
	
	public void execute(Player p) {
		try {
			handler.startMaze(p);
			
		} catch (Exception e) {

			if(e instanceof SelectionNotFoundExcetion)
				p.sendMessage(ChatColor.RED + "Select an area first.");
			else if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "Finish your selection first.");
		}
	}
}