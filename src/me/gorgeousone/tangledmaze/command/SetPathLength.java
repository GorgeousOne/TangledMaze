package me.gorgeousone.tangledmaze.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;
import me.gorgeousone.tangledmaze.util.Utils;

public class SetPathLength extends MazeCommand {
	
	public SetPathLength() {
		super("pathlength", "/tangledmaze pathlength <integer>", 1, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		
		String pathLengthString = arguments[0];
		int pathLength;
		
		try {
			pathLength = Utils.limit(Integer.parseInt(pathLengthString), 1, Constants.MAX_PATHLENGTH);
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_INVALID_NUMBER.sendTo(player, new PlaceHolder("number", pathLengthString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallHeight() == pathLength)
			return false;
		
		maze.setPathLength(pathLength);
		Messages.MESSAGE_PATHLENGTH_CHANGED.sendTo(player, new PlaceHolder("number", pathLength));
		
		return true;
	}
}