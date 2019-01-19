package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.maze.Maze;
import me.gorgeousone.tangledmaze.maze.MazeAction;
import me.gorgeousone.tangledmaze.maze.MazeHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.tool.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;

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
		
		if(!ToolHandler.hasClipboard(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area first.");
			p.sendMessage("/tangledmaze select rectangle/ellipse");
			return;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(p);
		
		if(!clipboard.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		MazeAction action = maze.getAddition(clipboard.getClip());
		
		if(action.getAddedFill().size() == 0) {
			p.sendMessage(ChatColor.RED + "Your selection is entirely covered by your maze.");
			return;
			
		}else if(action.getAddedFill().size() == clipboard.getClip().size()) {
			p.sendMessage(ChatColor.RED + "Your selection does not seem to touch your maze directly (outline on outline).");
			return;
		}
		
		clipboard.reset();
		maze.processAction(action, true);
	}
}