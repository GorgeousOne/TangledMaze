package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.utils.Constants;

public class UndoAction {

	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}

		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(Constants.prefix + "You did not start a maze where aything can be undone.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getActionHistory().isEmpty()) {
			p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "There is nothing left to be undone...");
			
		}else {
			MazeAction action = maze.getActionHistory().popLastAction().invert();
			maze.processAction(action, false);
		}
	}
}