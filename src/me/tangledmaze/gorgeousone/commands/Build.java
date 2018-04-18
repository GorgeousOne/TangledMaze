package me.tangledmaze.gorgeousone.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tangledmaze.gorgeousone.main.Constants;
import me.tangledmaze.gorgeousone.main.TangledMain;
import me.tangledmaze.gorgeousone.mazes.Maze;
import me.tangledmaze.gorgeousone.mazes.MazeBuilder;
import me.tangledmaze.gorgeousone.mazes.MazeHandler;
import me.tangledmaze.gorgeousone.selections.SelectionHandler;
import me.tangledmaze.gorgeousone.shapes.Brush;
import me.tangledmaze.gorgeousone.shapes.ExitSetter;
import me.tangledmaze.gorgeousone.shapes.Rectangle;

public class Build {

	private SelectionHandler sHandler;
	private MazeHandler mHandler;
	private MazeBuilder mBuilder;
	
	public Build() {
		sHandler = TangledMain.getPlugin().getSelectionHandler();
		mHandler = TangledMain.getPlugin().getMazeHandler();
		mBuilder  = TangledMain.getPlugin().getMazeBuilder();
	}
	
	public void execute(Player p) {
		
		if(!mHandler.hasMaze(p)) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		Maze maze = mHandler.getMaze(p);
		
		if(maze.getExits().isEmpty()) {
			p.sendMessage(Constants.prefix + "This plugin is not smart enough to choose a start point for the algorithm. " + 
											 "Could you be so nice and mark at least one exit at the border?");
			p.sendMessage("/tangledmaze select exit");
			return;
		}
		
		mHandler.deselctMaze(p);
		int queuePosition = mBuilder.enqueueMaze(maze);

		if(queuePosition != 0) {
			p.sendMessage(Constants.prefix + "Your maze has been queued. Position in queue: " + queuePosition);
			p.sendMessage(Constants.prefix + "If you leave the server before it gets built your work will be discarded!");
		}
		
		if(sHandler.getSelectionType(p) == Brush.class ||
		   sHandler.getSelectionType(p) == ExitSetter.class)
			sHandler.setSelectionType(p, Rectangle.class);
		
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
