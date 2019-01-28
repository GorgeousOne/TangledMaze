package me.gorgeousone.tangledmaze.command;

import org.bukkit.entity.Player;

import me.gorgeousone.tangledmaze.handler.MazeHandler;
import me.gorgeousone.tangledmaze.handler.ToolHandler;
import me.gorgeousone.tangledmaze.util.Constants;

public class DiscardAll {
	
	public void execute(Player player) {
		
		if(!player.hasPermission(Constants.buildPerm)) {
			player.sendMessage(Constants.insufficientPerms);
			return;
		}

		MazeHandler.getMaze(player).reset();
		ToolHandler.resetToDefaultTool(player);
		player.sendMessage(Constants.prefix + "Deselected your maze and your clipboard.");
	}
}