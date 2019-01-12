package me.gorgeousone.tangledmaze.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.mazes.Maze;
import me.gorgeousone.tangledmaze.mazes.MazeHandler;
import me.gorgeousone.tangledmaze.tools.ToolHandler;
import me.gorgeousone.tangledmaze.tools.Clip;
import me.gorgeousone.tangledmaze.tools.ClippingTool;
import me.gorgeousone.tangledmaze.tools.ExitSettingTool;
import me.gorgeousone.tangledmaze.utils.Constants;

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
		
		//todo remoev debug
		ToolHandler.setTool(player, new ExitSettingTool(player));
	}
}