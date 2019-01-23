package me.gorgeousone.tangledmaze.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.clip.Clip;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.tool.ClippingTool;
import me.gorgeousone.tangledmaze.util.Constants;

public class StartMaze {
	
	public void execute(Player player) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}
		
		if(!ToolHandler.hasClipboard(player)) {
			player.sendMessage(ChatColor.RED + "Please select an area first.");
			return;
		}
		
		ClippingTool clipboard = ToolHandler.getClipboard(player);
		
		if(!clipboard.isComplete()) {
			player.sendMessage(ChatColor.RED + "Please finish your selection first.");
			return;
		}
		
		Clip clip = clipboard.getClip();
		clipboard.reset();
		
		Maze maze = MazeHandler.getMaze(player);
		maze.setClip(clip);
		
		player.sendMessage(Constants.prefix + "Started a maze from selection.");
	}
}