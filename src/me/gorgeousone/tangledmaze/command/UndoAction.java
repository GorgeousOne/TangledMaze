package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.util.Constants;

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
			ClipAction action = maze.getActionHistory().popLastAction().invert();
			maze.processAction(action, false);
		}
	}
}