package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.data.Messages;
import me.gorgeousone.tangledmaze.data.Settings;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.PlaceHolder;

public class SetWallHeight extends MazeCommand {

	public SetWallHeight() {
		super("wallheight", "/tangledmaze wallheight <integer>", 1, true, null);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {

		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		String wallHeightString = arguments[0];
		int wallHeight;

		try {
			wallHeight = Utils.limitInt(Integer.parseInt(wallHeightString), 1, Settings.MAX_WALLHEIGHT);
			
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", wallHeightString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallHeight() == wallHeight) {
			return false;
		}
		
		maze.setWallHeight(wallHeight);
		Messages.MESSAGE_WALLHEIGHT_CHANGED.send(player, new PlaceHolder("number", wallHeight));
		return true;
	}
}