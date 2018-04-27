package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
import net.md_5.bungee.api.ChatColor;

public class CutMaze {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	
	public CutMaze() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!sHandler.hasSelection(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
			p.sendMessage("/tangledmaze wand");
			return;
		}
		
		try {
			mHandler.cutSelctionFromMaze(p, sHandler.getSelection(p));
			sHandler.deselectSelection(p);
			p.sendMessage(Constants.prefix + "Subtracted area from maze.");
			
		}catch (Exception e) {
			if(e instanceof NullPointerException) {
				p.sendMessage(ChatColor.RED + "Please start a maze first.");
				p.sendMessage("/tangledmaze start");
				
			}else if(e instanceof IllegalArgumentException)
				p.sendMessage(ChatColor.RED + "Please finish your selection first.");
		}
	}
}
