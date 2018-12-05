package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

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

		if(maze.contains(p.getLocation())) {
			p.sendMessage(Constants.prefix + "You are already standing inside your maze xD");
			return;
		}
		
		Location target = maze.getFill().get(maze.getChunks().get(0)).get(0).clone();
		target.add(0.5, 1, 0.5);
		target.setDirection(p.getLocation().getDirection());
		
		p.teleport(target);
	}
}