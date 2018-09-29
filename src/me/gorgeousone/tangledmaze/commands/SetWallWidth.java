package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

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
		
		if(wallWidth > 10) {
			p.sendMessage(Constants.prefix + "In order to not exclude Mexicans the wall thickness is limited to 10 blocks. "
					+ "In case you are a preseident you may look for another plugin.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getWallWidth() != wallWidth) {
			maze.setWallWidth(wallWidth);
			p.sendMessage(Constants.prefix + "Set wall width to " + wallWidth + ".");
		}
	}
}