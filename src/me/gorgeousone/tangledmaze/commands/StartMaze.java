package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Constants;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;

public class StartMaze {
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!SelectionHandler.hasShapeSel(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		
		try {
			ShapeSelection selection = SelectionHandler.getShapeSel(p);
			MazeHandler.getMaze(p).setShape(selection);
			selection.reset();
			p.sendMessage(Constants.prefix + "Started a maze from selection.");
			
		} catch (IllegalArgumentException e) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
		}
	}
}