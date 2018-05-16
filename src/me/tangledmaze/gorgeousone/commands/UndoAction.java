package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.core.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeAction;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class UndoAction {

	private MazeHandler mHandler;
	
	public UndoAction() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
	}
	
	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}

		if(!mHandler.hasMaze(p)) {
			p.sendMessage(Constants.prefix + "You did not start a maze where aything can be undone.");
			return;
		}
		
		Maze maze = mHandler.getMaze(p);
		
		if(maze.getActionHistory().isEmpty()) {
			
			if(Math.random() < 1/3d)
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "There is nothing left to be undone...");
			
		}else {
			MazeAction action = maze.getActionHistory().popLastAction().reverse();
			
			maze.processAction(action, false);
			mHandler.showMazeAction(p, maze, action);
			
			p.sendMessage(Constants.prefix + "Undid last action.");
		}
	}
}