package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

public class SetPathWidth {

	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int pathWidth = 0;
		
		try {
			pathWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(pathWidth < 1) {
			p.sendMessage(ChatColor.RED + "A path cannot be thinner than 1 block.");
			return;
		}
		
		if(pathWidth > 10) {
			p.sendMessage(Constants.prefix + "So grandma can still cross the path without help the width is limited to 10 blocks. "
					+ "There will not always be a handsome guy like you around to help her.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getPathWidth() != pathWidth) {
			maze.setPathWidth(pathWidth);
			p.sendMessage(Constants.prefix + "Set path width to " + pathWidth + ".");
		}
	}
}