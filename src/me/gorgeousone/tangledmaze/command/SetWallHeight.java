package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetWallHeight {

	public void execute(Player player, String argument) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallHeight;

		try {
			wallHeight = Integer.parseInt(argument);
			
		} catch (NumberFormatException e) {
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", argument));
			return;
		}
		
		if(wallHeight < 1) {
			player.sendMessage(ChatColor.RED + "A wall cannot be flatter than 1 block.");
			return;
		}
		
		if(wallHeight > Settings.MAX_WALL_HEIGHT) {
			player.sendMessage(Constants.prefix
					+ "People also thought that the tower of babel was a good idea. "
					+ "And now look at what happened back then. "
					+ "The wall height is limited to " + Settings.MAX_WALL_HEIGHT + " blocks.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallHeight() != wallHeight) {
			maze.setWallHeight(wallHeight);
			player.sendMessage(Constants.prefix + "Set wall height to " + wallHeight + " blocks.");
		}
	}
}