package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetWallWidth extends MazeCommand {

	public SetWallWidth() {
		super("wallwidth", "/tangledmaze wallwidth <integer>", 1, true, null);
	}

	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;

		String wallWidthString = arguments[0];
		int wallWidth;
		
		try {
			wallWidth = Utils.limit(Integer.parseInt(wallWidthString), 1, Constants.MAX_WALLWIDTH);
		
		}catch (NumberFormatException ex) {
			
			Messages.ERROR_INVALID_NUMBER.sendTo(player, new PlaceHolder("number", wallWidthString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallWidth() == wallWidth)
			return false;
			
		maze.setWallWidth(wallWidth);
		Messages.MESSAGE_WALLWIDTH_CHANGED.sendTo(player, new PlaceHolder("number", wallWidth));
		return true;
	}
}