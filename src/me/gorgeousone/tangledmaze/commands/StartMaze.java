package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.tools.ClippingTool;
import me.gorgeousone.tangledmaze.utils.Constants;

public class StartMaze {
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!ToolHandler.hasClip(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		ClippingTool selection = ToolHandler.getClip(p);
		
		if(!selection.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		maze.setClip(selection);
		
		p.sendMessage(Constants.prefix + "Started a maze from selection.");
		selection.reset();
	}
}