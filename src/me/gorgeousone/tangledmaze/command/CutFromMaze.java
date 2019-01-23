package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.ClipAction;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Constants;

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
		
		if(!ToolHandler.hasClipboard(p)) {
			p.sendMessage(ChatColor.RED + "Please select an area with a maze wand first.");
			p.sendMessage("/tangledmaze wand");
			return;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(p);

		if(!clipboard.isComplete()) {
			p.sendMessage(ChatColor.RED + "Please finish your clipboard first.");
			return;
		}
		
		Maze maze = MazeHandler.getMaze(p);
		ClipAction action = maze.getDeletion(clipboard.getClip());
		
		if(action == null) {
			p.sendMessage(ChatColor.RED + "Your clipboard does not seem to intersect your maze directly.");
			return;
		}

		clipboard.reset();
		maze.processAction(action, true);
	}
}
