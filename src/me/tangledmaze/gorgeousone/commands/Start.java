package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.listener.MazeHandler;
import me.tangledmaze.gorgeousone.listener.SelectionHandler;
import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import net.md_5.bungee.api.ChatColor;

public class Start {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public Start() {
		sHandler = TangledMain.plugin.getSelectionHandler();
		mHandler = TangledMain.plugin.getMazeHandler();
	}
	
	public void execute(Player p) {
		if(!sHandler.hasSelection(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		try {
			mHandler.startMaze(p, sHandler.getSelection(p));
			sHandler.deselect(p);
			p.sendMessage(Constants.prefix + "Started a maze from selection.");
			
		} catch (IllegalArgumentException e) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
		}
	}
}