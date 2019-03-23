package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.data.Settings;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetPathWidth extends MazeCommand {

	public SetPathWidth() {
		super("pathwidth", "/tangledmaze pathwidth <integer>", 1, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {

		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		String pathWidthString = arguments[0];
		int pathWidth;
		
		try {
			pathWidth = Utils.limitInt(Integer.parseInt(pathWidthString), 1, Settings.MAX_PATHWIDTH);
		
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", pathWidthString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getPathWidth() != pathWidth) {
			
			maze.setPathWidth(pathWidth);
			Messages.MESSAGE_PATHWIDTH_CHANGED.send(player, new PlaceHolder("number", pathWidth));
		}

		return true;
	}
}