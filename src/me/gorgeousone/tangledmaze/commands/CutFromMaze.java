package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeAction;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.tools.ClippingTool;
import me.gorgeousone.tangledmaze.utils.Constants;

public class CutFromMaze {

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
		
		if(!ToolHandler.hasClip(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area with a selection wand first.");
			p.sendMessage("/tangledmaze wand");
			return;
		}
		
		ClippingTool shape = ToolHandler.getClip(p);

		if(!shape.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		MazeAction action = maze.getDeletion(shape);
		
		if(action.getRemovedFill().size() == 0) {
			p.sendMessage(ChatColor.RED + "Your selection does not seem to intersect your maze directly (outline inside borders).");
			return;
		}

		shape.reset();
		maze.processAction(action, true);
	}
}
