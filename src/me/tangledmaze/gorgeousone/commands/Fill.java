package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeFiller;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;

public class Fill {

	private MazeHandler mHandler;
	private MazeFiller mFiller;
	
	public Fill() {
		mHandler = TangledMain.getPlugin().getMazeHandler();
		mFiller  = TangledMain.getPlugin().getMazeFiller();
	}
	
	public void execute(Player p) {
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = mHandler.getMaze(p);
		
		if(maze.getEntrances().isEmpty()) {
			p.sendMessage(Constants.prefix + "This plugin is not smart enough to choose a start point for the algorithm. " + 
											 "Could you be so nice and mark one entrance at the border?");
			p.sendMessage("/tangledmaze select entrance");
			return;
		}
		
		mFiller.fillMaze(maze);
		
//		if(args.isEmpty()) {
//			p.sendMessage(ChatColor.RED + "");
//		}
//		
//		for(String arg : args) {
//			if(!arg.contains("%")) {
//				p.sendMessage("Could not use \"" + arg + "\"");
//				return;
//			}
//		}
	}
}
