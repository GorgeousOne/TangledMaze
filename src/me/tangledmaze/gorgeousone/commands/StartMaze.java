package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import net.md_5.bungee.api.ChatColor;

public class StartMaze {
	
	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public StartMaze() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!sHandler.hasSelection(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		try {
			mHandler.startMaze(p, sHandler.getSelection(p));
			sHandler.deselectSelection(p);
			p.sendMessage(Constants.prefix + "Started a maze from selection.");
			
		} catch (IllegalArgumentException e) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
		}
	}
}