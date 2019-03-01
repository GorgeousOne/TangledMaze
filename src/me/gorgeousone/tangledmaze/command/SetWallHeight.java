package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Settings;
import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetWallHeight extends MazeCommand {

	public SetWallHeight() {
		super("wallheight", "/tangledmaze wallheight <integer>", 1, true, null);
	}
	
	public void execute(Player player, String argument) {
		
		if(!player.hasPermission(Constants.BUILD_PERM)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallHeight;

		try {
			wallHeight = Utils.limitInt(Integer.parseInt(argument), 1, Settings.MAX_WALL_HEIGHT);
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", argument));
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallHeight() != wallHeight) {
			maze.setWallHeight(wallHeight);
			Messages.MESSAGE_MAZE_BUILDING.send(player, new PlaceHolder("number", wallHeight));
		}
	}
}