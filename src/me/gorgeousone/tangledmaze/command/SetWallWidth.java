package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;

public class SetWallWidth {

	public void execute(Player p, String arg0) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		int wallWidth = 0;
		
		try {
			wallWidth = Integer.parseInt(arg0);
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "\"" + arg0 + "\" is not an integer.");
			return;
		}
		
		if(wallWidth < 1) {
			p.sendMessage(ChatColor.RED + "A wall cannot be thinner than 1 block.");
			return;
		}
		
		if(wallWidth > Constants.MAX_WALL_WIDTH) {
			p.sendMessage(Constants.prefix
					+ "With a wall that thick you could already lock out Mexicans. "
					+ "If you are a preseident please look out for another maze generator. "
					+ "The wall width is litmited to " + Constants.MAX_WALL_WIDTH + " blocks. ");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getWallWidth() != wallWidth) {
			maze.setWallWidth(wallWidth);
			p.sendMessage(Constants.prefix + "Set wall width to " + wallWidth + " blocks.");
		}
	}
}