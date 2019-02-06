package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;

public class SetPathWidth {

	public void execute(Player player, String arg0) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int pathWidth = 0;
		
		try {
			pathWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(pathWidth < 1) {
			player.sendMessage(ChatColor.RED + "A path cannot be thinner than 1 block.");
			return;
		}
		
		if(pathWidth > Constants.MAX_PATH_WIDTH) {
			player.sendMessage(Constants.prefix
					+ "Grandma still wants to cross the path on her own. "
					+ "There will not always be a handsome person like you around to help her. "
					+ "The path width is limited to " + Constants.MAX_PATH_WIDTH + " blocks.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getPathWidth() != pathWidth) {
			maze.setPathWidth(pathWidth);
			player.sendMessage(Constants.prefix + "Set path width to " + pathWidth + " blocks.");
		}
	}
}