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

public class SetWallWidth extends MazeCommand {

	public SetWallWidth() {
		super("wallwidth", "/tangledmaze wallwidth <integer", 1, true, null);
	}

	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		
		if(!super.execute(sender, arguments)) {
			return false;
		}
		
		Player player = (Player) sender;

		String wallWidthString = arguments[0];
		int wallWidth;
		
		try {
			wallWidth = Utils.limitInt(Integer.parseInt(wallWidthString), 1, Settings.MAX_WALL_WIDTH);
		
		}catch (NumberFormatException ex) {
			
			Messages.ERROR_NUMBER_NOT_VALID.send(player, new PlaceHolder("number", wallWidthString));
			return false;
		}
		
		Maze maze = MazeHandler.getMaze(player);
		
		if(maze.getWallWidth() != wallWidth) {
			maze.setWallWidth(wallWidth);
			player.sendMessage(Constants.prefix + "Set wall width to " + wallWidth + " blocks.");
		}
		return true;
		
	}
	
	public void execute(Player p, String wallWidthString) {
		
		if(!p.hasPermission(Constants.BUILD_PERM)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallWidth;
		
		try {
			wallWidth = Integer.parseInt(wallWidthString);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + wallWidthString + "\" is not an integer.");
			return;
		}
		
		if(wallWidth < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be thinner than 1 block.");
			return;
		}
		
		if(wallWidth > Settings.MAX_WALL_WIDTH) {
			p.sendMessage(Constants.prefix
					+ "With a wall that thick you could already lock out Mexicans. "
					+ "If you are a preseident please look out for another maze generator. "
					+ "The wall width is litmited to " + Settings.MAX_WALL_WIDTH + " blocks. ");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getWallWidth() != wallWidth) {
			maze.setWallWidth(wallWidth);
			p.sendMessage(Constants.prefix + "Set wall width to " + wallWidth + " blocks.");
		}
	}
}