package me.tangledmaze.gorgeousone.commands;

import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.utils.Constants;

public class Undo {

	private MazeHandler mHandler;
	
	public Undo() {
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
		
		//inform the player if there could be undone something
		if(maze.undoLast())
			p.sendMessage(Constants.prefix + "Undid last action.");
	}
}