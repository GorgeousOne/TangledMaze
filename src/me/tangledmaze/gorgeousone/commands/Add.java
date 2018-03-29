package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.exceptions.MazeNotFoundException;
import me.tangledmaze.gorgeousone.exceptions.SelectionNotFoundExcetion;
import me.tangledmaze.gorgeousone.main.TangledMain_go;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import net.md_5.bungee.api.ChatColor;

public class Add {

	private SelectionHandler handler;

	public Add(TangledMain_go plugin) {
		handler = plugin.getSelectionHandler();
	}
	
	public void execute(Player p) {
		try {
			handler.addSelectionToMaze(p);
			
		} catch (Exception e) {

			if(e instanceof MazeNotFoundException) {
				p.sendMessage(ChatColor.RED + "Start a maze first.");
				p.sendMessage("/tangledmaze start");
			}else if(e instanceof SelectionNotFoundExcetion)
				p.sendMessage(ChatColor.RED + "Select an area first.");
			else if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "Finish your selection first.");
		}
	}
}