package me.gorgeousone.tangledmaze.commands;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;
import me.gorgeousone.tangledmaze.utils.MazePoint;

public class TpToMaze {
	
	public void execute(Player p) {

		if(!p.hasPermission(Constants.mazeTpPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(ChatColor.RED + "You did not create any any maze to teleport to, yet.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);

		if(maze.getClip().contains(new MazePoint(p.getLocation()))) {
			p.sendMessage(Constants.prefix + "You are already standing inside your maze xD");
			return;
		}
		
		MazePoint target = maze.getClip().getBorder().first();
		target.add(0.5, 2, 0.5);
		target.setDirection(p.getLocation().getDirection());
		
		p.teleport(target);
	}
}