package me.gorgeousone.tangledmaze.command;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.maze.MazeHandler;
import me.gorgeousone.tangledmaze.tool.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;

public class DiscardAll {
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		MazeHandler.getMaze(p).reset();
		ToolHandler.resetToDefaultTool(p);
		p.sendMessage(Constants.prefix + "Deselected your selection and your maze.");
	}
}