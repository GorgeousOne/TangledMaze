package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import net.md_5.bungee.api.ChatColor;

public class Add {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public Add() {
		sHandler = TangledMain.plugin.getSelectionHandler();
		mHandler = TangledMain.plugin.getMazeHandler();
	}
	
	public void execute(Player p) {
		if(!sHandler.hasSelection(p)) {
			p.sendMessage(ChatColor.RED + "Select an area first.");
			return;
		}
		
		try {
			mHandler.addSelectionToMaze(p, sHandler.getSelection(p));
			sHandler.deselect(p);
			p.sendMessage(Constants.prefix + "Added selection to maze.");
			
		}catch (Exception e) {
			if(e instanceof NullPointerException) {
				p.sendMessage(ChatColor.RED + "Start a maze first.");
				p.sendMessage("/tangledmaze start");
				
			}else if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "Finish your selection first.");
		}
	}
}