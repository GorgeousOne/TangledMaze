package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Constants;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetWallHeight extends MazeCommand {

	public SetWallHeight() {
		super("wallheight", "/tangledmaze wallheight <integer>", 1, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {

		if(!super.execute(sender, arguments))
			return false;
		
		Player player = (Player) sender;
		
		String wallHeightString = arguments[0];
		int wallHeight;

		try {
			wallHeight = Utils.limit(Integer.parseInt(wallHeightString), 1, Constants.MAX_WALLHEIGHT);
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_INVALID_NUMBER.sendTo(player, new PlaceHolder("number", wallHeightString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallHeight() == wallHeight)
			return false;
		
		maze.setWallHeight(wallHeight);
		Messages.MESSAGE_WALLHEIGHT_CHANGED.sendTo(player, new PlaceHolder("number", wallHeight));
		return true;
	}
}