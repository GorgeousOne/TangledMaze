package me.gorgeousone.tangledmaze.command;

import me.gorgeousone.tangledmaze.util.Settings;
import me.gorgeousone.tangledmaze.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;
import me.gorgeousone.tangledmaze.util.Messages;
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
			pathWidth = Utils.limitInt(Integer.parseInt(pathWidthString), 1, Settings.MAX_PATH_WIDTH);
		
		} catch (NumberFormatException ex) {
			
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", pathWidthString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getPathWidth() != pathWidth) {
			
			maze.setPathWidth(pathWidth);
			player.sendMessage(Constants.prefix + "Set path width to " + pathWidth + " blocks.");
		}

		return true;
	}
	
	public void execute(Player player, String argument) {
		
		if(!player.hasPermission(Constants.BUILD_PERM)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int pathWidth;
		
		try {
			pathWidth = Integer.parseInt(argument);
		} catch (NumberFormatException e) {
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", argument));

			return;
		}
		
		if(pathWidth < 1) {
			player.sendMessage(ChatColor.RED + "A path cannot be thinner than 1 block.");
			return;
		}
		
		if(pathWidth > Settings.MAX_PATH_WIDTH) {
			player.sendMessage(Constants.prefix
					+ "Grandma still wants to cross the path on her own. "
					+ "There will not always be a handsome person like you around to help her. "
					+ "The path width is limited to " + Settings.MAX_PATH_WIDTH + " blocks.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getPathWidth() != pathWidth) {
			maze.setPathWidth(pathWidth);
			player.sendMessage(Constants.prefix + "Set path width to " + pathWidth + " blocks.");
		}
	}
}