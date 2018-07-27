package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.core.Constants;
import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;

public class UndoAction {

	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}

		if(!MazeHandler.hasMaze(p)) {
			p.sendMessage(Constants.prefix + "You did not start a maze where aything can be undone.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		
		if(maze.getActionHistory().isEmpty()) {
			
			if(Math.random() < 1/3d)
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "There is nothing left to be undone...");
			
		}else {
			MazeAction action = maze.getActionHistory().popLastAction().invert();
			
			maze.processAction(action, false);
			p.sendMessage(Constants.prefix + "Undid last action.");
		}
	}
}