package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.selections.SelectionHandler;
import me.gorgeousone.tangledmaze.selections.ShapeSelection;
import me.gorgeousone.tangledmaze.utils.Constants;

public class AddToMaze {

	public void execute(Player p) {
		
		if(!p.hasPermission(Constants.buildPerm)) {
			p.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!MazeHandler.getMaze(p).isStarted()) {
			p.sendMessage(ChatColor.RED + "Please start a maze first.");
			p.sendMessage("/tangledmaze start");
			return;
		}
		
		if(!SelectionHandler.hasShapeSel(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			p.sendMessage("/tangledmaze select rectangle/ellipse");
			return;
		}
		
		ShapeSelection shape = SelectionHandler.getShapeSel(p);
		
		if(!shape.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		MazeAction action = maze.getAddition(shape);
		
		if(action.getAddedFill().size() == 0) {
			p.sendMessage(ChatColor.RED + "Your selection is entirely covered by your maze.");
			return;
			
		}else if(action.getAddedFill().size() == shape.size()) {
			p.sendMessage(ChatColor.RED + "Your selection does not seem to touch your maze directly (outline on outline).");
			return;
		}

		shape.reset();
		maze.processAction(action, true);
	}
}