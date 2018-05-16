package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.RectSelection;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.utils.Constants;
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
		
		RectSelection selection = sHandler.getSelection(p);
		sHandler.discardSelection(p);

		try {
			mHandler.startMaze(p, selection);
			p.sendMessage(Constants.prefix + "Started a maze from selection.");
			
		} catch (IllegalArgumentException e) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
		}
	}
}