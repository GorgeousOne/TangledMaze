package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

public class SetWallHeight {

	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallHeight = 0;
		
		try {
			wallHeight = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(wallHeight < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be flatter than 1 block.");
			return;
		}
		
		if(wallHeight > Constants.MAX_WALL_HEIGHT) {
			p.sendMessage(Constants.prefix
					+ "People also thought that the tower of babel was agood idea. "
					+ "And now look what happened back then. "
					+ "The wall height is limited to " + Constants.MAX_WALL_HEIGHT + " blocks.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getWallHeight() != wallHeight) {
			maze.setWallHeight(wallHeight);
			p.sendMessage(Constants.prefix + "Set wall height to " + wallHeight + ".");
		}
	}
}